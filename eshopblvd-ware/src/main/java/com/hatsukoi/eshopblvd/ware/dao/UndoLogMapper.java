package com.hatsukoi.eshopblvd.ware.dao;

import com.hatsukoi.eshopblvd.ware.entity.UndoLog;
import com.hatsukoi.eshopblvd.ware.entity.UndoLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UndoLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    long countByExample(UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int deleteByExample(UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int insert(UndoLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int insertSelective(UndoLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    List<UndoLog> selectByExampleWithBLOBs(UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    List<UndoLog> selectByExample(UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    UndoLog selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") UndoLog record, @Param("example") UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") UndoLog record, @Param("example") UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") UndoLog record, @Param("example") UndoLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(UndoLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(UndoLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table undo_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(UndoLog record);
}