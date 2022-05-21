package com.hatsukoi.eshopblvd.order.vo;

import com.hatsukoi.eshopblvd.vo.MemberAddressVO;
import com.hatsukoi.eshopblvd.vo.OrderItemVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:26 AM
 */
public class OrderConfirmVO {
    /**
     * 收货地址列表 「ums_member_receive_address」
     */
    List<MemberAddressVO> addresses;
    /**
     * 选中的所有购物项
     */
    List<OrderItemVO> items;
    /**
     * 优惠信息
     */
    Integer integration;
    /**
     * 总计商品数量
     */
    Integer count;
    /**
     * 所有购物项总价
     */
    BigDecimal totalPrice;
    /**
     * 运费
     */
    BigDecimal fare;
    /**
     * 订单计算的应付价格
     */
    BigDecimal payPrice;
    /**
     * 当前选中的地址和收货人信息
     */
    MemberAddressVO selectedAddress;
    /**
     * 防重令牌
     */
    String orderToken;

    public List<MemberAddressVO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<MemberAddressVO> addresses) {
        this.addresses = addresses;
    }

    public List<OrderItemVO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemVO> items) {
        this.items = items;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getCount() {
        Integer result = 0;
        if (this.items != null) {
            for (OrderItemVO item: items) {
                result += item.getCount();
            }
        }
        return result;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal result = new BigDecimal("0.00");
        if (items != null) {
            for (OrderItemVO item: items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                result = result.add(multiply);
            }
        }
        return result;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public BigDecimal getPayPrice() {
        if (this.fare != null && this.integration != null) {
            return getTotalPrice().add(this.fare).subtract(new BigDecimal(String.valueOf(Math.round(this.integration / 100))));
        } else if (this.fare != null) {
            return getTotalPrice().add(this.fare);
        } else if (this.integration != null) {
            return getTotalPrice().subtract(new BigDecimal(String.valueOf(Math.round(this.integration / 100))));
        } else {
            return getTotalPrice();
        }
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public MemberAddressVO getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(MemberAddressVO selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }
}
