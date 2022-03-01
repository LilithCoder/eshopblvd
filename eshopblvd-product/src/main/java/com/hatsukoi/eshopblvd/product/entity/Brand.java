package com.hatsukoi.eshopblvd.product.entity;

import java.io.Serializable;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table pms_brand
 */
public class Brand implements Serializable {
    /**
     * Database Column Remarks:
     *   品牌id
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.brand_id
     *
     * @mbg.generated
     */
    private Long brandId;

    /**
     * Database Column Remarks:
     *   品牌名
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     * Database Column Remarks:
     *   品牌logo地址
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.logo
     *
     * @mbg.generated
     */
    private String logo;

    /**
     * Database Column Remarks:
     *   显示状态[0-不显示；1-显示]
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.show_status
     *
     * @mbg.generated
     */
    private Byte showStatus;

    /**
     * Database Column Remarks:
     *   检索首字母
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.first_letter
     *
     * @mbg.generated
     */
    private String firstLetter;

    /**
     * Database Column Remarks:
     *   排序
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.sort
     *
     * @mbg.generated
     */
    private Integer sort;

    /**
     * Database Column Remarks:
     *   介绍
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pms_brand.descript
     *
     * @mbg.generated
     */
    private String descript;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pms_brand
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.brand_id
     *
     * @return the value of pms_brand.brand_id
     *
     * @mbg.generated
     */
    public Long getBrandId() {
        return brandId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.brand_id
     *
     * @param brandId the value for pms_brand.brand_id
     *
     * @mbg.generated
     */
    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.name
     *
     * @return the value of pms_brand.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.name
     *
     * @param name the value for pms_brand.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.logo
     *
     * @return the value of pms_brand.logo
     *
     * @mbg.generated
     */
    public String getLogo() {
        return logo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.logo
     *
     * @param logo the value for pms_brand.logo
     *
     * @mbg.generated
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.show_status
     *
     * @return the value of pms_brand.show_status
     *
     * @mbg.generated
     */
    public Byte getShowStatus() {
        return showStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.show_status
     *
     * @param showStatus the value for pms_brand.show_status
     *
     * @mbg.generated
     */
    public void setShowStatus(Byte showStatus) {
        this.showStatus = showStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.first_letter
     *
     * @return the value of pms_brand.first_letter
     *
     * @mbg.generated
     */
    public String getFirstLetter() {
        return firstLetter;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.first_letter
     *
     * @param firstLetter the value for pms_brand.first_letter
     *
     * @mbg.generated
     */
    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.sort
     *
     * @return the value of pms_brand.sort
     *
     * @mbg.generated
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.sort
     *
     * @param sort the value for pms_brand.sort
     *
     * @mbg.generated
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pms_brand.descript
     *
     * @return the value of pms_brand.descript
     *
     * @mbg.generated
     */
    public String getDescript() {
        return descript;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pms_brand.descript
     *
     * @param descript the value for pms_brand.descript
     *
     * @mbg.generated
     */
    public void setDescript(String descript) {
        this.descript = descript;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_brand
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", brandId=").append(brandId);
        sb.append(", name=").append(name);
        sb.append(", logo=").append(logo);
        sb.append(", showStatus=").append(showStatus);
        sb.append(", firstLetter=").append(firstLetter);
        sb.append(", sort=").append(sort);
        sb.append(", descript=").append(descript);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}