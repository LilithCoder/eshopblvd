package com.hatsukoi.eshopblvd.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table pms_category
 */
@Data
public class Category implements Serializable {
    /**
     * Database Column Remarks:
     *   分类id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.cat_id
     *
     * @mbg.generated
     */
    private Long catId;

    /**
     * Database Column Remarks:
     *   分类名称
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     * Database Column Remarks:
     *   父分类id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.parent_cid
     *
     * @mbg.generated
     */
    private Long parentCid;

    /**
     * Database Column Remarks:
     *   层级
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.cat_level
     *
     * @mbg.generated
     */
    private Integer catLevel;

    /**
     * Database Column Remarks:
     *   是否显示[0-不显示，1显示]
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.show_status
     *
     * @mbg.generated
     */
    private Byte showStatus;

    /**
     * Database Column Remarks:
     *   排序
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.sort
     *
     * @mbg.generated
     */
    private Integer sort;

    /**
     * Database Column Remarks:
     *   图标地址
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.icon
     *
     * @mbg.generated
     */
    private String icon;

    /**
     * Database Column Remarks:
     *   计量单位
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.product_unit
     *
     * @mbg.generated
     */
    private String productUnit;

    /**
     * Database Column Remarks:
     *   商品数量
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_category.product_count
     *
     * @mbg.generated
     */
    private Integer productCount;

    /**
     * 子分类
     */
    private transient List<Category> children;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pms_category
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_category
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", catId=").append(catId);
        sb.append(", name=").append(name);
        sb.append(", parentCid=").append(parentCid);
        sb.append(", catLevel=").append(catLevel);
        sb.append(", showStatus=").append(showStatus);
        sb.append(", sort=").append(sort);
        sb.append(", icon=").append(icon);
        sb.append(", productUnit=").append(productUnit);
        sb.append(", productCount=").append(productCount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}