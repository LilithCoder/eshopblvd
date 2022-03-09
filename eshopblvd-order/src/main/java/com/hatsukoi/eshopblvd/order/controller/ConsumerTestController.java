package com.hatsukoi.eshopblvd.order.controller;

import com.hatsukoi.eshopblvd.order.service.ConsumerTestService;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gaoweilin
 * @date 2022/03/09 Wed 3:07 AM
 */

@Controller
public class ConsumerTestController {
    @Autowired
    ConsumerTestService consumerTestService;

    @ResponseBody
    @RequestMapping("/invokeProvider")
    public CommonResponse consumerTest() {
        return CommonResponse.success().setData(consumerTestService.consumerTest());
    }
}
