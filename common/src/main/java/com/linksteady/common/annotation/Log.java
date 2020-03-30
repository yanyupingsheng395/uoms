package com.linksteady.common.annotation;

import com.linksteady.common.domain.LogTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value() default "";

    /**
     *  日志类型 如果为page,则即保存到数据库，又保存到日志文件，否则仅保存到日志文件，默认为page
     * @return
     */
    LogTypeEnum type() default LogTypeEnum.PAGE;

    /**
     * 日志的位置
     */
    String location() default "系统管理";
}