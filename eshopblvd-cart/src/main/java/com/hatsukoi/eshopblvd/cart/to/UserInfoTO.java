package com.hatsukoi.eshopblvd.cart.to;

import lombok.Data;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 1:46 AM
 */
@Data
public class UserInfoTO {
    /**
     * 已登陆用户的标识id
     */
    private Long userId;
    /**
     * 临时购物车的用户标识id
     * （一定会有，无论有没有用户登录，都会存在这个userKey）
     */
    private String userKey;
    /**
     * 是否已经有临时用户userKey
     * 这个字段存在的意义是
     * 如果cookie里已经保存了userKey临时用户，那这个tempUser为true，就说明没有必要再生成一个userKey去持续更新cookie里的userKey
     * 反之tempUser为false的话就说明我们需要生成一个新的临时用户userKey存到cookie里
     * 临时用户标识userKey是登陆与否都需要存在的，但是不需要每个请求结束后都持续被更新
     */
    private boolean tempUser = false;
}
