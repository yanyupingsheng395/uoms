package com.linksteady.operate.dao;


import com.linksteady.operate.domain.ExecSteps;

import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2019-10-23
 */
public interface ExecStepsMapper {

   List<ExecSteps> selctStepList(String keyName);

   int execCommonUpdateSqls(String sqls);

   int execCommonDeleteSqls(String sqls);

   void  execCommonInsertSqls(String sqls);
}
