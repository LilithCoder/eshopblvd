package com.hatsukoi.eshopblvd.ware.dao;

import com.hatsukoi.eshopblvd.ware.entity.WareSku;
import com.hatsukoi.eshopblvd.ware.entity.WareSkuExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WareSkuMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    long countByExample(WareSkuExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int deleteByExample(WareSkuExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int insert(WareSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int insertSelective(WareSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    List<WareSku> selectByExample(WareSkuExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    WareSku selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") WareSku record, @Param("example") WareSkuExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") WareSku record, @Param("example") WareSkuExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(WareSku record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table wms_ware_sku
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(WareSku record);

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}