package com.linksteady.operate.dao;


import com.linksteady.operate.domain.ExecSteps;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author huang
 * @date 2019-10-23
 */
public interface ExecStepsMapper {

   List<ExecSteps> selctStepList(String keyName);

   int execCommonUpdateSqls(@Param("sql")  String sql);

   int execCommonDeleteSqls(@Param("sql") String sql);

   void  execCommonInsertSqls(@Param("sql") String sql);
}
