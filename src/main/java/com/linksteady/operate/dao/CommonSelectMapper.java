package com.linksteady.operate.dao;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 通用的查询Mapper
 * @author
 */
@Mapper
public interface CommonSelectMapper {

    Double selectOnlyDoubleValue(@Param("sql") String sql);

}