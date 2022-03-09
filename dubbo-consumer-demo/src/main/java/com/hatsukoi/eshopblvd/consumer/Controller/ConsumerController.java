package com.hatsukoi.eshopblvd.consumer.Controller;

import com.hatsukoi.eshopblvd.consumer.Service.ConsumerService;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gaoweilin
 * @date 2022/03/10 Thu 1:40 AM
 */

@Controller
public class ConsumerController {

    @Autowired
    ConsumerService consumerService;

    @ResponseBody
    @RequestMapping("/invokeProvider")
    public CommonResponse consumerTest() {
        return CommonResponse.success().setData(consumerService.requestProvider());
    }
}
