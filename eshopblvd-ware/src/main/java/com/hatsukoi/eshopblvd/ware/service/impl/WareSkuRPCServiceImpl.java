package com.hatsukoi.eshopblvd.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.member.MemberService;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.exception.ware.NoStockException;
import com.hatsukoi.eshopblvd.to.FareAddrInfoTO;
import com.hatsukoi.eshopblvd.to.SkuHasStockVO;
import com.hatsukoi.eshopblvd.to.StockLockedTo;
import com.hatsukoi.eshopblvd.to.WareSkuLockTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.vo.MemberAddressVO;
import com.hatsukoi.eshopblvd.vo.OrderItemVO;
import com.hatsukoi.eshopblvd.ware.dao.WareOrderTaskDetailMapper;
import com.hatsukoi.eshopblvd.ware.dao.WareOrderTaskMapper;
import com.hatsukoi.eshopblvd.ware.dao.WareSkuMapper;
import com.hatsukoi.eshopblvd.ware.entity.WareOrderTask;
import com.hatsukoi.eshopblvd.ware.entity.WareOrderTaskDetail;
import com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存服务RPC远程调用的实现
 * @author gaoweilin
 * @date 2022/04/10 Sun 3:09 PM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class WareSkuRPCServiceImpl implements WareSkuRPCService {
    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.member.MemberService", check = false)
    private MemberService memberService;

    @Autowired
    private WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    private WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 返回「sku -> 是否有库存」映射
     * @param skuIds
     * @return
     */
    @Override
    public CommonResponse getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVO> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVO skuHasStockVO = new SkuHasStockVO();
            // select sum(stock - stock_locked) from `wms_ware_sku` where sku_id = ?
            Long count = wareSkuMapper.getSkuStock(skuId);
            skuHasStockVO.setSkuId(skuId);
            skuHasStockVO.setHasStock(count == null ? false : count > 0);
            return skuHasStockVO;
        }).collect(Collectors.toList());
        return CommonResponse.success().setData(collect);
    }

    /**
     * 根据地址id返回地址信息和相应的运费
     * @param addrId
     * @return
     */
    @Override
    public CommonResponse getAddrInfoAndFare(Long addrId) {
        FareAddrInfoTO addrInfoTO = new FareAddrInfoTO();
        // 根据地址id先查出地址信息
        CommonResponse resp = CommonResponse.convertToResp(memberService.getAddrInfoById(addrId));
        MemberAddressVO address = resp.getData(new TypeReference<MemberAddressVO>() {
        });
        addrInfoTO.setAddress(address);

        // 根据地址来计算运费，这里就模拟返回运费为5
        if (address != null) {
            addrInfoTO.setFare(new BigDecimal("5.00"));
        }
        return CommonResponse.success().setData(addrInfoTO);
    }

    /**
     * 为某订单的这些订单项锁库存
     * @param wareSkuLock
     * @return
     */
    @Override
    @Transactional
    public CommonResponse wareSkuLock(WareSkuLockTo wareSkuLock) {
        // 保存新的库存工作单，供后续回滚解锁库存用
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setOrderSn(wareSkuLock.getOrderSn());
        wareOrderTaskMapper.insertSelective(wareOrderTask);

        List<OrderItemVO> locks = wareSkuLock.getLocks();

        for (OrderItemVO lock: locks) {
            // 每个订单项根据商品的skuId以及对应的数量查出了哪些仓库有足够的库存
            // SELECT ware_id
            // FROM `wms_ware_sku`
            // WHERE sku_id=#{lock.skuId} AND stock-stock_locked>=#{lock.count}
            List<Long> wareIds = wareSkuMapper.getWareIdsByLock(lock);

            // TODO: 这里原本应该根据下单的收货地址，找最近的一个仓库来锁定库存的，现在就直接用第一个仓库
            Boolean skuStocked = false;
            if (wareIds != null && wareIds.size() > 0) {
                for (Long wareId: wareIds) {
                    // UPDATE `wms_ware_sku`
                    // SET stock=stock+#{count}
                    // WHERE sku_id=#{skuId} AND ware_id=#{wareId} AND stock-stock_locked>=#{count}
                    Long count = wareSkuMapper.lockSkuStock(lock.getSkuId(), lock.getCount(), wareId);
                    if (count == 1) {
                        // 锁定成功
                        skuStocked = true;
                        // 保存库存工作单记录 - 解锁库存用
                        WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail(null, lock.getSkuId(), lock.getTitle(), lock.getCount(), wareOrderTask.getId(), wareId, 1);
                        wareOrderTaskDetailMapper.insertSelective(wareOrderTaskDetail);
                        // 将当前商品锁定了几件的工作单记录发给MQ
                        StockLockedTo lockedTo = new StockLockedTo();
                        BeanUtils.copyProperties(wareOrderTaskDetail, lockedTo);
                        // 发消息给交换机，路由key为锁库存，消息会来到延时队列带上50min后，去解锁库存
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);
                        break;
                    }
                    // 锁定失败，尝试锁下一个仓库
                }
                if (skuStocked == false) {
                    // 手动强制回滚事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    // 当前商品锁住任何仓库的库存，返回无库存的业务码
                    return CommonResponse.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
                }
            } else {
                // 手动强制回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                // 没有任何仓库无库存，返回无库存的业务码
                return CommonResponse.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
            }
        }
        return CommonResponse.success();
    }
}


































