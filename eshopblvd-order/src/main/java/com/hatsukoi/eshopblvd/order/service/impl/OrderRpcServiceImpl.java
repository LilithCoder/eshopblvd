package com.hatsukoi.eshopblvd.order.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.hatsukoi.eshopblvd.api.order.OrderRpcService;
import com.hatsukoi.eshopblvd.order.dao.OrderMapper;
import com.hatsukoi.eshopblvd.order.entity.Order;
import com.hatsukoi.eshopblvd.order.entity.OrderExample;
import com.hatsukoi.eshopblvd.to.OrderTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/20 Fri 3:07 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class OrderRpcServiceImpl implements OrderRpcService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 根据订单号查订单状态
     * @param orderSn
     * @return
     */
    @Override
    public CommonResponse getOrderStatus(String orderSn) {
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        if (orders != null && orders.size() > 0) {
            Order order = orders.get(0);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(order, orderTo);
            return CommonResponse.success().setData(orderTo);
        } else {
            return CommonResponse.error();
        }
    }
}
