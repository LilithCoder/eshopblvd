package com.hatsukoi.eshopblvd.member.dao;

import com.hatsukoi.eshopblvd.member.entity.MemberLevel;
import com.hatsukoi.eshopblvd.member.entity.MemberLevelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberLevelMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    long countByExample(MemberLevelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int deleteByExample(MemberLevelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int insert(MemberLevel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int insertSelective(MemberLevel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    List<MemberLevel> selectByExample(MemberLevelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    MemberLevel selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MemberLevel record, @Param("example") MemberLevelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") MemberLevel record, @Param("example") MemberLevelExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(MemberLevel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ums_member_level
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(MemberLevel record);
}