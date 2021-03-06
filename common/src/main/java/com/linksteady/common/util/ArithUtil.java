package com.linksteady.common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 针对double类型的加、减、乘、除运算提供支持，并提供double类型格式化的方法
 * @author huang
 */
@Slf4j
public class ArithUtil {

    /**
     * 提供精确加法计算的add方法
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的除法运算方法div
     * @param value1 被除数
     * @param value2 除数
     * @param scale 精确范围
     * @return 两个参数的商
     * @throws IllegalAccessException
     */
    public static double div(double value1,double value2,int scale){
        //如果精确范围小于0，抛出异常信息
        if(scale<0){
            //throw new IllegalAccessException("精确度不能小于0");
            log.error("精确度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.divide(b2, scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 保留scale位小数 四舍五入
     * @param doubleVal
     * @return 返回一个double类型的scale 位小数
     */
    public static Double formatDouble(Double doubleVal,int scale){
        if(null == doubleVal) {
            doubleVal = new Double(0);
        }
        return new BigDecimal(doubleVal).setScale(scale,RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 保留scale位小数 传入模式
     * @param doubleVal
     * @return 返回一个double类型的scale 位小数
     */
    public static Double formatDoubleByMode(Double doubleVal,int scale,RoundingMode model){
        if(null == doubleVal) {
            doubleVal = new Double(0);
        }
        return new BigDecimal(doubleVal).setScale(scale,model).doubleValue();
    }

}
