package com.hatsukoi.eshopblvd.ware.service;

import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.ware.entity.WareInfo;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/03 Sun 3:45 PM
 */
public interface WareInfoService {
    CommonPageInfo<WareInfo> queryWareInfoPage(Map<String, Object> params);
}
