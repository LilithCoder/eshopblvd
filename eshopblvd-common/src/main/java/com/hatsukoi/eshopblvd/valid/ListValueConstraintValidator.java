package com.hatsukoi.eshopblvd.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义校验器
 * @author gaoweilin
 * @date 2022/03/18 Fri 3:02 AM
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Byte> {

    private Set<Byte> set;

    @Override
    public void initialize(ListValue listValue) {
        set = new HashSet<>();
        byte[] vals = listValue.vals();
        for (byte val: vals) {
            set.add(val);
        }
    }

    /**
     * 判断是否校验成功
     * @param aByte 需要校验的值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Byte aByte, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(aByte);
    }
}
