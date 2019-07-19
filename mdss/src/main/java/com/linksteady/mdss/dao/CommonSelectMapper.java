package com.linksteady.mdss.dao;

import com.linksteady.mdss.domain.DiagAddDataCollector;
import com.linksteady.mdss.domain.DiagComnCollector;
import com.linksteady.mdss.domain.DiagFilterDataCollector;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 通用的查询Mapper
 * @author
 */
@Mapper
public interface CommonSelectMapper {

    DiagFilterDataCollector selectOnlyDoubleValue(@Param("sql") String sql);

    List<DiagComnCollector> selectCollectorDataBySql(@Param("sql") String sql);

    List<String> selectStringBySql(@Param("sql") String sql);

    List<DiagAddDataCollector> selectAddData(@Param("sql") String sql);

}