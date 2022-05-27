package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:59 AM
 */
@Data
public class SeckillSessionTo implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Boolean status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 秒杀活动关联商品
     */
    private List<SeckillSkuRelationTo> sessionSkuRelations;
}
