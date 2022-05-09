package com.hatsukoi.eshopblvd.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.cart.CartService;
import com.hatsukoi.eshopblvd.api.member.MemberService;
import com.hatsukoi.eshopblvd.order.interceptor.LoginUserInterceptor;
import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.hatsukoi.eshopblvd.order.vo.OrderConfirmVO;
import com.hatsukoi.eshopblvd.to.MemberTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.vo.MemberAddressVO;
import com.hatsukoi.eshopblvd.vo.OrderItemVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:25 AM
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ThreadPoolExecutor executor;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.member.MemberService", check = false)
    private MemberService memberService;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.cart.CartService", check = false)
    private CartService cartService;

    @Override
    public OrderConfirmVO getOrderConfirmData(Long addrId) throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirm = new OrderConfirmVO();
        MemberTO memberTO = LoginUserInterceptor.loginUser.get();
        // 1. 异步RPC调用会员服务获取登陆用户的地址列表
        CompletableFuture<Void> getAddressTask = CompletableFuture.runAsync(() -> {
            CommonResponse commonResponse = CommonResponse.convertToResp(memberService.getAddress(memberTO.getId()));
            List<MemberAddressVO> data = commonResponse.getData(new TypeReference<List<MemberAddressVO>>() {
            });
            // 1.2 根据addrId设置选中的地址，否则就设置默认地址
            for (MemberAddressVO address: data) {
                if (address.getDefaultStatus() && addrId == null) {
                    orderConfirm.setSelectedAddress(address);
                    break;
                }
                if (addrId != null && address.getId() == addrId) {
                    orderConfirm.setSelectedAddress(address);
                    break;
                }
            }
            orderConfirm.setAddresses(data);
        }, executor);

        // 2. 异步RPC调用购物车服务获取当前用户选中的购物项（包括库存）
        CompletableFuture<Void> orderItemsTask = CompletableFuture.runAsync(() -> {
            // TODO: 搞个拦截器透传sessionId过去
            CommonResponse resp = CommonResponse.convertToResp(cartService.getUserCartItems(memberTO.getId()));
            if (resp.getCode() == HttpStatus.SC_OK) {
                List<OrderItemVO> data = resp.getData(new TypeReference<List<OrderItemVO>>() {
                });
                orderConfirm.setItems(data);
            } else {
                log.error("调用RPC购物车服务失败，未传userId");
            }
        }, executor);

        // 3. 积分信息
        orderConfirm.setIntegration(memberTO.getIntegration());

        // 4. 查询运费
        // TODO: 调用ware服务根据地址去计算运费，这里先不写了
        orderConfirm.setFare(new BigDecimal(5.00));

        CompletableFuture.allOf(getAddressTask, orderItemsTask).get();
        return orderConfirm;
    }

}
