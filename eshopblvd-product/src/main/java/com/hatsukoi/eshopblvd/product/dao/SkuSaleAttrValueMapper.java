package com.hatsukoi.eshopblvd.product.dao;

import com.hatsukoi.eshopblvd.product.entity.SkuSaleAttrValue;
import com.hatsukoi.eshopblvd.product.entity.SkuSaleAttrValueExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SkuSaleAttrValueMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    long countByExample(SkuSaleAttrValueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int deleteByExample(SkuSaleAttrValueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int insert(SkuSaleAttrValue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int insertSelective(SkuSaleAttrValue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    List<SkuSaleAttrValue> selectByExample(SkuSaleAttrValueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    SkuSaleAttrValue selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") SkuSaleAttrValue record, @Param("example") SkuSaleAttrValueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") SkuSaleAttrValue record, @Param("example") SkuSaleAttrValueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(SkuSaleAttrValue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pms_sku_sale_attr_value
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SkuSaleAttrValue record);
}