package com.hatsukoi.eshopblvd.utils;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 *
 * 通用分页数据封装类
 */
@Data
public class CommonPageInfo<T> {
    /**
     * 总记录数
     */
    private long totalCount;
    /**
     * 每页记录数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 当前页数
     */
    private int currPage;
    /**
     * 列表数据
     */
    private List<T> listData;

    /**
     * 将pagehelper分页查询结果封装为通用分页封装结果
     * 调用这个方法前需要保证PageHelper.startPage已经开始分页，这样就能通过构造PageInfo对象获取分页信息
     * @param list 需要被封装的数据列表
     * @param <T> 泛型类型的元素
     * @return 被通用分页数据封装类封装的数据列表
     */
    public static <T> CommonPageInfo<T> convertToCommonPage(List<T> list) {
        CommonPageInfo<T> result = new CommonPageInfo<>();
        // 通过构造PageInfo对象获取分页信息，如当前页码，总页数，总条数
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        result.setTotalCount(pageInfo.getTotal());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotalPage(pageInfo.getPages());
        result.setCurrPage(pageInfo.getPageNum());
        result.setListData(pageInfo.getList());
        return result;
    }
}
