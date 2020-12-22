package com.linksteady.operate.dao;


import org.apache.ibatis.annotations.Param;

/**
 * @author huang
 * @date 2019-10-23
 */
public interface DailyCalculateMapper {

   int execCommonUpdateSqls(@Param("sql")  String sql);

   int execCommonDeleteSqls(@Param("sql") String sql);

   void  execCommonInsertSqls(@Param("sql") String sql);

   void execUpdatePushList();

   void execUpdatePushListLarge();

   void execUpdateDailyDetail();

   void execUpdateActivityDetail();

   void execUpdateManualDetail();

   void updateDailyHeader();

   void updateActivityPreheat();

   void updateActivityPreheatNotify();

   void updateActivityFormal();

   void updateActivityFormalNotify();

   void updateActivityPlan();

   void updateManualHeader();

   void updateManualActualPushDate();

   void updateDailyPushStatistics();

   void updateActivityPushStatistics();

   void updateManualPushStatistics();

   void updateQywxDailyHeader();

   void updateQywxActivityFormal();

   void updateQywxActivityFormalNotify();

   void updateQywxDailyPushStatistics();

   void updateQywxManualPushStatistics();


}
