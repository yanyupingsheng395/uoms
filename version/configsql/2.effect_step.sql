prompt Importing table uo_op_exec_steps...
set feedback off
set define off
insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'MERGE INTO UO_OP_PUSH_LIST_LARGE C USING
(SELECT T.PUSH_ID,
       ROW_NUMBER()OVER(PARTITION BY T.MSGID ORDER BY T.PUSH_ID DESC) RN
 FROM UO_OP_PUSH_LIST_LARGE T WHERE T.IS_PUSH=''1'' AND T.PUSH_STATUS!=''C'' AND T.MSGID IS NOT NULL) T1
ON (C.PUSH_ID=T1.PUSH_ID AND TRUNC(C.PUSH_DATE)>=TRUNC(SYSDATE-3))
WHEN MATCHED THEN
   UPDATE SET C.FINAL_MSG_ID=C.MSGID+(RN-1) ', null, null, '修复PUSH_LARGE中的FINAL_MSG_ID', '1', 'UPDATE', '修复PUSH_LARGE中的FINAL_MSG_ID(梦网已测试)');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'merge into uo_op_push_list_large t1
            using  uo_op_push_rpt t2
            on (t1.final_msg_id = t2.msgid and trunc(t1.push_date)>=trunc(sysdate-3))
        when matched then
        update set t1.push_status=t2.status', null, null, '更新PUSH_LARGE中的推送状态', '2', 'UPDATE', '根据运营商返回的状态报告，更新PUSH_LARGE中的推送状态');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'merge into UO_OP_ACTIVITY_DETAIL t3
using uo_op_push_list_large t4
on (t3.activity_detail_id = t4.source_id and t4.source_code = ''ACTIVITY'' and trunc(t3.insert_dt)>=trunc(sysdate-3))
when matched then
  update
     set t3.push_status = t4.push_status,
         t3.push_date   = t4.push_date,
         t3.is_push     = t4.is_push', null, null, '更新活动运营明细表的推送状态/推送日期', '3', 'UPDATE', '将PUSH_LIST_LARGE的推送状态/推送日期/是否推送同步回ACTIVITY_DETAIL');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'UPDATE UO_OP_ACTIVITY_PLAN T SET T.PLAN_STATUS=''3'' WHERE T.PLAN_DATE_WID>=TO_NUMBER(TO_CHAR(SYSDATE-5,''yyyymmdd'')) AND T.PLAN_STATUS=''2'' AND
            NOT EXISTS (
            SELECT 1 FROM uo_op_activity_detail AP1 WHERE AP1.HEAD_ID=T.HEAD_ID AND AP1.PLAN_DT=T.PLAN_DATE_WID AND AP1.IS_PUSH=''0'')', null, null, '更新活动计划表中已推送为已完成', '4', 'UPDATE', '更新活动计划表中已推送为已完成(对于最近5天 状态为执行中 如果在明细表中不存在未推送的记录，则将其状态更新为完成)');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'UPDATE uo_op_activity_header T
        SET T.PREHEAT_STATUS = ''done''
        where t.preheat_status = ''doing''
            and t.has_preheat=''1''
            and  trunc(sysdate) >t.preheat_end_dt
            and  exists (
            select p.head_id from uo_op_activity_plan p where p.head_id=t.head_id and p.plan_status in(''3'') and p.activity_stage=''preheat''
            )', null, null, '更新活动头表预售状态由已执行更新为完成', '5', 'UPDATE', '更新活动头表预售状态由已执行更新为完成(活动结束后有一条计划为完成)');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', ' UPDATE uo_op_activity_header T
        SET T.FORMAL_STATUS = ''done''
        where t.FORMAL_STATUS = ''doing'' and t.has_preheat=''0''
            and  trunc(sysdate) >t.Formal_End_Dt
            and  exists (
            select p.head_id from uo_op_activity_plan p where p.head_id=t.head_id and p.plan_status in(''3'') and p.activity_stage=''formal''
            )', null, null, '更新活动头表正式状态由已执行更新为完成', '6', 'UPDATE', '更新活动头表正式状态由已执行更新为完成(活动结束后有一条计划为完成)');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', 'merge into uo_op_push_list t1
            using  uo_op_push_rpt t2
            on (t1.msgid = t2.msgid and trunc(t1.push_date)>=trunc(sysdate-3))
        when matched then
        update set t1.push_status=t2.status', null, null, '更新推送状态', '1', 'UPDATE', '根据运营商返回的状态报告，将短信推送状态更新到PUSH_LIST');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', 'merge into uo_op_daily_detail t3
using uo_op_push_list t4
on (t3.daily_detail_id = t4.source_id and t4.source_code = ''D'' and trunc(t3.insert_dt)>trunc(sysdate-3))
when matched then
  update
     set t3.push_status = t4.push_status,
         t3.push_date   = t4.push_date,
         t3.is_push     = t4.is_push', null, null, '更新每日运营明细表的推送状态/推送日期', '2', 'UPDATE', '将PUSH_LIST的推送状态/推送日期/是否推送同步回DAILY_DETAIL');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', ' UPDATE UO_OP_DAILY_HEADER c SET c.STATUS=''finished'' WHERE c.STATUS=''done'' AND not exists
(
select * from UO_OP_DAILY_DETAIL p where p.PUSH_STATUS=''P'' and p.head_id=c.head_id
) AND TRUNC(C.INSERT_DT)>TRUNC(SYSDATE-3) ', null, null, '将每日运营头表由已执行更新为已结束', '3', 'UPDATE', '将每日运营头表由已执行更新为已结束');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', 'update UO_OP_DAILY_HEADER t
        set (t.plan_num,t.faild_num,t.success_num)=
            (
             select
             SUM(case when c.PUSH_STATUS=''P'' then 1 else 0 end) plan_num,
             SUM(case when c.PUSH_STATUS=''F'' then 1 else 0 end) faild_num,
             SUM(case when c.PUSH_STATUS=''S'' then 1 else 0 end) success_num
             from UO_OP_DAILY_DETAIL c where c.head_id = t.head_id
            )where
            trunc(touch_dt)>trunc(sysdate-3) and status in (''done'',''finished'')', null, null, '更新每日运营头表的推送统计数字', '4', 'UPDATE', '更新每日运营头表的推送统计数字');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'N', 'MERGE INTO UO_OP_PUSH_LIST_LARGE C USING
(SELECT T.PUSH_ID,
       ROW_NUMBER()OVER(PARTITION BY T.MSGID ORDER BY T.PUSH_ID DESC) RN
 FROM UO_OP_PUSH_LIST_LARGE T WHERE T.IS_PUSH=''1'' AND T.PUSH_STATUS!=''C'' AND T.MSGID IS NOT NULL) T1
ON (C.PUSH_ID=T1.PUSH_ID AND TRUNC(C.PUSH_DATE)>=TRUNC(SYSDATE-3))
WHEN MATCHED THEN
   UPDATE SET C.FINAL_MSG_ID=C.MSGID+(RN-1) ', null, null, '修复PUSH_LARGE中的FINAL_MSG_ID', '1', 'UPDATE', '修复PUSH_LARGE中的FINAL_MSG_ID(梦网已测试)  活动运营中也存在');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'N', 'merge into uo_op_push_list_large t1
            using  uo_op_push_rpt t2
            on (t1.final_msg_id = t2.msgid and trunc(t1.push_date)>=trunc(sysdate-3))
        when matched then
        update set t1.push_status=t2.status', null, null, '更新PUSH_LARGE中的推送状态', '2', 'UPDATE', '根据运营商返回的状态报告，更新PUSH_LARGE中的推送状态');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', ' update uo_op_manual_header c set (c.success_num,c.faild_num,c.intercept_num)=(
            select
                sum(case when d.push_status=''S'' then 1 else 0 end)  success_num,
                sum(case when d.push_status=''F'' then 1 else 0 end)  faild_num,
                sum(case when d.push_status=''C'' then 1 else 0 end) intercept_num
            from uo_op_manual_detail d where d.head_id=c.head_id
        ) where trunc(c.SCHEDULE_DATE) >=trunc(sysdate-3)', null, null, '更新手工推送头表的推送统计字段', '4', 'UPDATE', '更新手工推送头表的推送统计字段');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', ' merge into uo_op_manual_detail t1
            using  uo_op_push_list_large t2
            on (t1.detail_id = t2.source_id and t2.source_code=''M'' and t1.head_id in (
                select h.head_id from uo_op_manual_header h where h.status in (''1'',''2'') and trunc(h.SCHEDULE_DATE)>=trunc(sysdate-3)
        ) )
        when matched then
        update set t1.push_status=t2.push_status,t1.push_date=t2.push_date', null, null, '更新手工推送明细的推送状态/推送时间', '3', 'UPDATE', '更新手工推送明细的推送状态/推送时间');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', ' update uo_op_manual_header c set c.status=''2'' where trunc(c.SCHEDULE_DATE) >= trunc(sysdate-3) and c.status=''1''
            and not exists (
        select 1 from uo_op_manual_detail d where d.head_id=c.head_id and d.push_status=''P'')', null, null, '更新手工推送头表由计划中待完成到推送完成', '5', 'UPDATE', '更新手工推送头表由计划中待完成到推送完成');

insert into uo_op_exec_steps (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', 'update uo_op_manual_header c set c.actual_push_date=(
            select
                max(d.push_date)
            from uo_op_manual_detail d where d.head_id=c.head_id
        ) where trunc(c.SCHEDULE_DATE) >= trunc(sysdate-3) and c.status=''2''', null, null, '更新手工推送表的实际推送时间', '6', 'UPDATE', '更新手工推送表的实际推送时间');

prompt Done.
