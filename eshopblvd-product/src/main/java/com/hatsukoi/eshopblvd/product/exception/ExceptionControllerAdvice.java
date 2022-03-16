package com.hatsukoi.eshopblvd.product.exception;

import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/17 Thu 2:20 AM
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.hatsukoi.eshopblvd.product.controller")
public class ExceptionControllerAdvice {

    /**
     * 数据校验异常处理
     * 遇到该类型的异常抛出，会走这个处理方法
     * @param exception
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResponse handleValidException(MethodArgumentNotValidException exception) {
        log.error("数据校验出现问题{}, 异常类型: {}", exception.getMessage(), exception.getClass());
        // 接收异常
        BindingResult bindingResult = exception.getBindingResult();
        Map<String, String> errMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return CommonResponse.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).setData(errMap);
    }

    /**
     * 通用错误异常处理
     * @param throwable
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public CommonResponse handleException(Throwable throwable) {
        log.error("错误：", throwable);
        return CommonResponse.error(BizCodeEnum.UNKOWN_EXCEPTION.getCode(), BizCodeEnum.UNKOWN_EXCEPTION.getMsg());
    }
}
