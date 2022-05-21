package com.hatsukoi.eshopblvd.to;

import com.hatsukoi.eshopblvd.vo.MemberAddressVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 选中的地址信息及相关的运费
 * @author gaoweilin
 * @date 2022/05/18 Wed 11:02 PM
 */
@Data
public class FareAddrInfoTO implements Serializable {
    /**
     * 选中的收货地址信息
     */
    private MemberAddressVO address;
    /**
     * 计算得到运费
     */
    private BigDecimal fare;
}
