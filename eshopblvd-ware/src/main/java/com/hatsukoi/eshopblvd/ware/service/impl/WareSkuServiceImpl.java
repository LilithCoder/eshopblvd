package com.hatsukoi.eshopblvd.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.order.OrderRpcService;
import com.hatsukoi.eshopblvd.to.OrderTo;
import com.hatsukoi.eshopblvd.to.SkuHasStockVO;
import com.hatsukoi.eshopblvd.to.StockLockedTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.ware.dao.WareOrderTaskDetailMapper;
import com.hatsukoi.eshopblvd.ware.dao.WareOrderTaskMapper;
import com.hatsukoi.eshopblvd.ware.dao.WareSkuMapper;
import com.hatsukoi.eshopblvd.ware.entity.*;
import com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService;
import com.hatsukoi.eshopblvd.ware.service.WareSkuService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/04/06 Wed 2:09 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class WareSkuServiceImpl implements WareSkuService {
    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Autowired
    private WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Autowired
    private WareOrderTaskMapper wareOrderTaskMapper;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.order.OrderRpcService", check = false)
    private OrderRpcService orderRpcService;

    /**
     * 加库存
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuExample wareSkuExample = new WareSkuExample();
        WareSkuExample.Criteria criteria = wareSkuExample.createCriteria();
        criteria.andSkuIdEqualTo(skuId);
        criteria.andWareIdEqualTo(wareId);
        List<WareSku> wareSkus = wareSkuMapper.selectByExample(wareSkuExample);
        if (wareSkus == null || wareSkus.size() == 0) {
            WareSku wareSku = new WareSku();
            wareSku.setSkuId(skuId);
            wareSku.setStock(skuNum);
            wareSku.setWareId(wareId);
            wareSku.setStockLocked(0);
            // TODO：远程调用product -> skuservice获取sku名字
            wareSkuMapper.insertSelective(wareSku);
        } else {
            wareSkuMapper.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 释放锁定库存
     * @param stockLock
     */
    @Override
    public void unlockStock(StockLockedTo stockLock) {
        // 查询数据库关于这个订单工作项的锁定库存信息
        WareOrderTaskDetail wareOrderTaskDetail = wareOrderTaskDetailMapper.selectByPrimaryKey(stockLock.getId());

        // 为保证幂等性，只有当工作单详情处于被锁定的情况下才进行解锁
        if (wareOrderTaskDetail != null && wareOrderTaskDetail.getLockStatus() == 1) {
            // 如果有，就说明之前库存锁定成功了，没有事务回滚
            Long taskId = stockLock.getTaskId();
            // 通过库存工作单的id来查询其对应的订单号
            WareOrderTask wareOrderTask = wareOrderTaskMapper.selectByPrimaryKey(taskId);
            String orderSn = wareOrderTask.getOrderSn();
            // 根据订单号获取订单的状态
            CommonResponse resp = CommonResponse.convertToResp(orderRpcService.getOrderStatus(orderSn));
            if (resp.getCode() == HttpStatus.SC_OK) {
                OrderTo data = resp.getData(new TypeReference<OrderTo>() {
                });
                if (data == null || data.getStatus() == 4) {
                    // 没有订单（在订单服务提交订单时，成功远程的锁定库存后的其他逻辑抛异常，导致订单服务本地事务回滚了，保存在数据库的订单也回滚了，需要解锁库存）
                    // 订单的状态为4，说明订单已经关闭，需要解锁库存
                    realUnlockStock(wareOrderTaskDetail);
                }
            } else {
                // 抛异常后，消费者就catch到然后拒绝收到的消息，重新放回释放库存队列中
                throw new RuntimeException("远程获取订单状态失败");
            }
        } else {
            // 没有锁定状态的库存单工作项，说明当时远程库存服务没有事务提交或者订单取消后已经率先一步解锁库存了，所以就是无需解锁
            return;
        }
    }

    /**
     * 订单生成后30min内没有支付，解锁库存
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期。查订单状态新建状态，什么都不做就走了
     * 导致卡顿的订单，永远不能解锁库存
     * @param orderTo
     */
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        // 查一下最新库存的状态，防止重复解锁库存
        WareOrderTaskExample example = new WareOrderTaskExample();
        // 查库存工作单的id
        example.createCriteria().andOrderSnEqualTo(orderSn);
        List<WareOrderTask> wareOrderTasks = wareOrderTaskMapper.selectByExample(example);
        if (wareOrderTasks != null && wareOrderTasks.size() > 0) {
            WareOrderTask task = wareOrderTasks.get(0);
            // 库存工作单的id来查还在锁定中待解锁工作项
            WareOrderTaskDetailExample taskDetailExample = new WareOrderTaskDetailExample();
            taskDetailExample.createCriteria().andTaskIdEqualTo(task.getId()).andLockStatusEqualTo(1);
            List<WareOrderTaskDetail> wareOrderTaskDetails = wareOrderTaskDetailMapper.selectByExample(taskDetailExample);
            for (WareOrderTaskDetail taskDetail: wareOrderTaskDetails) {
                realUnlockStock(taskDetail);
            }
        }
    }

    /**
     * 实际数据库中库存解锁（库存工作项）
     * @param wareOrderTaskDetail
     */
    private void realUnlockStock(WareOrderTaskDetail wareOrderTaskDetail) {
        // 库存数据库减去库存锁定
        // UPDATE `wms_ware_sku` SET stock_locked=stock_locked-#{skuNum}
        // WHERE sku_id=#{skuId} AND ware_id=#{wareId}
        wareSkuMapper.unlockStock(wareOrderTaskDetail.getSkuId(), wareOrderTaskDetail.getWareId(), wareOrderTaskDetail.getSkuNum());

        // 更新库存工作项的状态为已解锁，并更新到数据库
        wareOrderTaskDetail.setLockStatus(2);
        wareOrderTaskDetailMapper.updateByPrimaryKeySelective(wareOrderTaskDetail);
    }
}
