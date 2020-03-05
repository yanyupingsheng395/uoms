alter table uo_op_activity_header add (PREHEAT_NOTIFY_DT date,FORMAL_NOTIFY_DT date);

alter table uo_op_activity_plan add (PLAN_TYPE varchar2(32),SUCCESS_NUM number,FAILD_NUM number,version number);
alter table uo_op_activity_plan add (PUSH_METHOD varchar2(32),PUSH_PERIOD varchar2(32));
ALTER TABLE UO_OP_ACTIVITY_PLAN DROP CONSTRAINT UO_OP_ACTIVITY_PLAN_PK;
create unique index UO_OP_ACTIVITY_PLAN_U1 on UO_OP_ACTIVITY_PLAN (HEAD_ID, PLAN_DATE_WID);

alter table UO_OP_ACTIVITY_PRODUCT ADD (INSERT_DT date,INSERT_BY varchar2(32),UPDATE_DT date,UPDATE_BY varchar2(32));

ALTER TABLE UO_OP_ACTIVITY_GROUP DROP COLUMN IN_GROWTH_PATH;
ALTER TABLE UO_OP_ACTIVITY_GROUP DROP COLUMN ACTIVE_LEVEL;
ALTER TABLE UO_OP_ACTIVITY_GROUP DROP COLUMN GROUP_USER_CNT;
ALTER TABLE UO_OP_ACTIVITY_GROUP DROP COLUMN GROWTH_USER_CNT;
ALTER TABLE UO_OP_ACTIVITY_GROUP DROP COLUMN ACTIVE_USER_CNT;
ALTER TABLE UO_OP_ACTIVITY_GROUP DROP COLUMN SMS_TEMPLATE_CODE;
alter table uo_op_activity_group add(SMS_TEMPLATE_CODE number,ACTIVITY_TYPE varchar2(32),user_num number);

alter table UO_OP_ACTIVITY_TEMPLATE drop column tmp_code;
alter table uo_op_activity_template drop column "LABEL";
alter table uo_op_activity_template drop column TMP_CONTENT_NORMAL;
alter table uo_op_activity_template add(tmp_code number,SCENE varchar2(64),IS_PERSONAL varchar2(32),RELATION varchar2(64));
alter table UO_OP_ACTIVITY_TEMPLATE add(TMP_name varchar2(64),IS_PROD_NAME VARCHAR2(2),IS_PROD_URL VARCHAR2(2),IS_PRICE VARCHAR2(2));

alter table t_dict modify value varchar2(256);

alter table uo_op_activity_detail drop column PUSH_ORDER_PERIOD;
alter table uo_op_activity_detail add(PUSH_SCHEDULING_DATE number);


comment on column uo_op_activity_plan.plan_type is '计划类型	NOTIFY 表示通知  DURING表示期间';
comment on column uo_op_activity_plan.SUCCESS_NUM is '成功推送人数';
comment on column uo_op_activity_plan.FAILD_NUM is '失败人数';
comment on column uo_op_activity_plan.VERSION is '版本号 乐观锁解决并发';

comment on column uo_op_activity_plan.PUSH_METHOD is '推送方式';
comment on column uo_op_activity_plan.PUSH_PERIOD is '推送时段';

comment on column uo_op_activity_group.SMS_TEMPLATE_CODE is '短信模板ID';
comment on column uo_op_activity_group.ACTIVITY_TYPE is '类型 NOTIFY 通知 DURING 期间';
comment on column uo_op_activity_group.user_num is '人数';

comment on column uo_op_activity_template.tmp_code is '短信模板ID 来自序列';
comment on column uo_op_activity_template.is_personal is '个性化	Y表示是  N表示否 ';
comment on column uo_op_activity_template.relation is '用户与商品关系	GROWTH 成长  LATENT 潜在 多个值用逗号分割';

comment on column uo_op_activity_template.SCENE is '使用场景  NOTIFY表示通知 DURING表示期间  多个值用逗号分割';
comment on column UO_OP_ACTIVITY_TEMPLATE.TMP_name is '文案名称';
comment on column UO_OP_ACTIVITY_TEMPLATE.IS_PROD_NAME is '推荐商品名称（1：是，0：否）';
comment on column UO_OP_ACTIVITY_TEMPLATE.IS_PROD_URL is '推荐商品短链（1：是，0：否）';
comment on column UO_OP_ACTIVITY_TEMPLATE.IS_PRICE is '推荐商品活动期间最低单价(1：是，0：否)';


comment on column UO_OP_ACTIVITY_PRODUCT.INSERT_DT is '创建时间';
comment on column UO_OP_ACTIVITY_PRODUCT.INSERT_BY is '创建人';
comment on column UO_OP_ACTIVITY_PRODUCT.UPDATE_DT is '更新时间';
comment on column UO_OP_ACTIVITY_PRODUCT.UPDATE_BY is '更新人';

comment on column uo_op_activity_header.activityflag is '活动类型 B表示大促 S表示普通 默认为普通';
comment on column uo_op_activity_header.preheat_notify_dt is '预售提醒日期';
comment on column uo_op_activity_header.formal_notify_dt is '正式提醒日期';

comment on column uo_op_activity_plan.plan_status is '计划状态  尚未计算0   待执行1   执行中2   执行完3  已过期4'


comment on column uo_op_activity_detail.PUSH_SCHEDULING_DATE is '计划推送时间';
comment on column UO_OP_PUSH_LIST_LARGE.PUSH_SCHEDULING_DATE is '应推送日期 YYYYMMDDHHmm格式';

-- Create table
create table UO_OP_ACTIVITY_COV_LIST
(
  cov_list_id    NUMBER,
  cov_rate       NUMBER,
  expect_pushnum NUMBER,
  expect_covnum  NUMBER,
  is_default     VARCHAR2(2)
);
  -- Add comments to the table
  comment on table UO_OP_ACTIVITY_COV_LIST
  is '转化率预估人数列表';
-- Add comments to the columns
comment on column UO_OP_ACTIVITY_COV_LIST.cov_list_id
is '主键ID';
comment on column UO_OP_ACTIVITY_COV_LIST.cov_rate
is '转化率';
comment on column UO_OP_ACTIVITY_COV_LIST.expect_pushnum
is '期望推送人数';
comment on column UO_OP_ACTIVITY_COV_LIST.expect_covnum
is '期望转化人数';
comment on column UO_OP_ACTIVITY_COV_LIST.is_default
is '是否默认 Y表示是 N表示否 默认此表中只有一条是Y';


-- Create table
create table UO_OP_ACTIVITY_COVINFO
(
  activity_head_id       NUMBER,
  preheat_notify_covid   NUMBER,
  preheat_notify_cov     NUMBER,
  preheat_notify_pushnum NUMBER,
  preheat_notify_covnum  NUMBER,
  normal_notify_covid    NUMBER,
  normal_notify_cov      NUMBER,
  normal_notify_pushnum  NUMBER,
  normal_notify_covnum   NUMBER
);
-- Add comments to the table
comment on table UO_OP_ACTIVITY_COVINFO
is '活动设置的转化率信息';
-- Add comments to the columns
comment on column UO_OP_ACTIVITY_COVINFO.activity_head_id
is '活动头表ID';
comment on column UO_OP_ACTIVITY_COVINFO.preheat_notify_covid
is '预售-提醒-转化率ID';
comment on column UO_OP_ACTIVITY_COVINFO.preheat_notify_cov
is '预售-提醒-转化率';
comment on column UO_OP_ACTIVITY_COVINFO.preheat_notify_pushnum
is '预售-提醒-预期推送人数';
comment on column UO_OP_ACTIVITY_COVINFO.preheat_notify_covnum
is '预售-提醒-预期转化人数';
comment on column UO_OP_ACTIVITY_COVINFO.normal_notify_covid
is '正式-提醒-转化率ID';
comment on column UO_OP_ACTIVITY_COVINFO.normal_notify_cov
is '正式-提醒-转化率';
comment on column UO_OP_ACTIVITY_COVINFO.normal_notify_pushnum
is '正式-提醒-预期推送人数';
comment on column UO_OP_ACTIVITY_COVINFO.normal_notify_covnum
is '正式-提醒-预期转化人数';
-- Create/Recreate indexes
create unique index UO_OP_ACTIVITY_COVINFO_U1 on UO_OP_ACTIVITY_COVINFO (ACTIVITY_HEAD_ID);


-- Create table
create table UO_OP_ACTIVITY_CONTENT_TMP
(
  activity_detail_id NUMBER,
  head_id         NUMBER,
  sms_content     VARCHAR2(512)
);
-- Add comments to the table
comment on table UO_OP_ACTIVITY_CONTENT_TMP
is '活动运营生成文案的临时表';
-- Add comments to the columns
comment on column UO_OP_ACTIVITY_CONTENT_TMP.activity_detail_id
is '行表ID';
comment on column UO_OP_ACTIVITY_CONTENT_TMP.head_id
is '头表ID';
comment on column UO_OP_ACTIVITY_CONTENT_TMP.sms_content
is '消息内容';
-- Create/Recreate indexes
create unique index UO_OP_ACTIVITY_CONTENT_TMP_U1 on UO_OP_ACTIVITY_CONTENT_TMP (activity_detail_id);


create sequence SEQ_UO_OP_ACTIVITY_TEMPLATE
minvalue 1
maxvalue 9999999999999999999999999999
start with 21
increment by 1
cache 20;


insert into t_dict (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('16', '1', '用户成长旅程的商品参与本次活动', 'ACTIVITY_GROUP', '活动运营用户组', '1');

insert into t_dict (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('17', '2', '用户成长旅程的商品没有参与本次活动', 'ACTIVITY_GROUP', '活动运营用户组', '2');

insert into t_dict (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('18', '3', '用户成长旅程的商品没有参与本次活动，但有可能成为活动商品潜在用户', 'ACTIVITY_GROUP', '活动运营用户组', '3');

INSERT INTO T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2) VALUES ('op.activity.sms.prodUrl', 'https://tb.cn.hn/t8n', '活动运营短信测试默认填充的商品链接', 199, null, null);
INSERT INTO T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2) VALUES ('op.activity.sms.price', '10元', '活动运营短信测试默认填充的商品单价', 199, null, null);
INSERT INTO T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2) VALUES ('op.activity.sms.prodName', '测试商品', '活动运营短信测试默认填充的商品名称', 199, null, null);

alter table UO_OP_ACTIVITY_GROUP modify GROUP_NAME VARCHAR2(256);
  /

/**
* 要删除的表
*/
DROP TABLE UO_OP_ACTIVITY_SUMMARY;
DROP TABLE UO_OP_ACTIVITY_PREDICT;
DROP TABLE UO_OP_ACTIVITY_BIG_CONFIG;

/**
  * 修复每日运营，增加效果天数字段
 */

alter table UO_OP_DAILY_HEADER add(effect_days number default 5);
comment on column UO_OP_DAILY_HEADER.effect_days is '效果累计天数';

alter table UO_OP_DAILY_HEADER add(push_method varchar2(32),push_period varchar2(32));
comment on column UO_OP_DAILY_HEADER.push_method is '推送方式';
comment on column UO_OP_DAILY_HEADER.push_period is '推送时段';

alter table uo_op_daily_detail add(insert_dt date default sysdate);
comment on column uo_op_daily_detail.insert_dt is '插入时间';

-- Create table
create table UO_OP_EXEC_STEPS
(
  key_name    VARCHAR2(32),
  is_valid    VARCHAR2(2),
  step_type   VARCHAR2(32),
  sql_content VARCHAR2(512),
  bean_name   VARCHAR2(512),
  method_name VARCHAR2(256),
  step_name   VARCHAR2(256),
  order_no    NUMBER,
  sql_type    VARCHAR2(32),
  comments    VARCHAR2(512)
);
-- Add comments to the table
comment on table UO_OP_EXEC_STEPS
is '计算效果的步骤及每一步的操作';
-- Add comments to the columns
comment on column UO_OP_EXEC_STEPS.key_name
is '业务关键字  daily 每日运营  activity活动运营 manual 手工推送';
comment on column UO_OP_EXEC_STEPS.is_valid
is '是否有效 Y 有效 N无效';
comment on column UO_OP_EXEC_STEPS.step_type
is '业务单元类型  SQL 表示执行SQL  BEAN表示调用方法';
comment on column UO_OP_EXEC_STEPS.sql_content
is '要执行的SQL片段';
comment on column UO_OP_EXEC_STEPS.bean_name
is '要执行的spring bean名称';
comment on column UO_OP_EXEC_STEPS.method_name
is '要执行的java方法名称（和bean名称配合使用）';
comment on column UO_OP_EXEC_STEPS.step_name
is '步骤名称';
comment on column UO_OP_EXEC_STEPS.order_no
is '排序号';
comment on column UO_OP_EXEC_STEPS.sql_type
is 'SQL语句的类型  分别有DELETE  UPDATE INSERT';
comment on column UO_OP_EXEC_STEPS.comments
is '描述';

prompt Importing table UO_OP_EXEC_STEPS...
set feedback off
set define off
insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'MERGE INTO UO_OP_PUSH_LIST_LARGE C USING
(SELECT T.PUSH_ID,
       ROW_NUMBER()OVER(PARTITION BY T.MSGID ORDER BY T.PUSH_ID DESC) RN
 FROM UO_OP_PUSH_LIST_LARGE T WHERE T.IS_PUSH=''1'' AND T.PUSH_STATUS!=''C'' AND T.MSGID IS NOT NULL) T1
ON (C.PUSH_ID=T1.PUSH_ID AND TRUNC(C.PUSH_DATE)>=TRUNC(SYSDATE-5))
WHEN MATCHED THEN
   UPDATE SET C.FINAL_MSG_ID=C.MSGID+(RN-1) ', null, null, '修复PUSH_LARGE中的FINAL_MSG_ID', '1', 'UPDATE', '修复PUSH_LARGE中的FINAL_MSG_ID(梦网已测试)');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'merge into uo_op_push_list_large t1
            using  uo_op_push_rpt t2
            on (t1.final_msg_id = t2.msgid and trunc(t1.push_date)=trunc(sysdate-3))
        when matched then
        update set t1.push_status=t2.status', null, null, '更新PUSH_LARGE中的推送状态', '2', 'UPDATE', '根据运营商返回的状态报告，更新PUSH_LARGE中的推送状态');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'MERGE INTO UO_OP_PUSH_LIST_LARGE C USING
(SELECT T.PUSH_ID,
       ROW_NUMBER()OVER(PARTITION BY T.MSGID ORDER BY T.PUSH_ID DESC) RN
 FROM UO_OP_PUSH_LIST_LARGE T WHERE T.IS_PUSH=''1'' AND T.PUSH_STATUS!=''C'' AND T.MSGID IS NOT NULL) T1
ON (C.PUSH_ID=T1.PUSH_ID AND TRUNC(C.PUSH_DATE)>=TRUNC(SYSDATE-5))
WHEN MATCHED THEN
   UPDATE SET C.FINAL_MSG_ID=C.MSGID+(RN-1) ', null, null, '更新活动运营明细表的推送状态/推送日期', '3', 'UPDATE', '将PUSH_LIST_LARGE的推送状态/推送日期/是否推送同步回ACTIVITY_DETAIL');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'UPDATE UO_OP_ACTIVITY_PLAN T SET T.PLAN_STATUS=''3'' WHERE T.PLAN_DATE_WID>=TO_NUMBER(TO_CHAR(SYSDATE-5,''yyyymmdd'')) AND T.PLAN_STATUS=''2'' AND
            NOT EXISTS (
            SELECT 1 FROM uo_op_activity_detail AP1 WHERE AP1.HEAD_ID=T.HEAD_ID AND AP1.PLAN_DT=T.PLAN_DATE_WID AND AP1.IS_PUSH=''0'')', null, null, '更新活动计划表中已推送为已完成', '4', 'UPDATE', '更新活动计划表中已推送为已完成(对于最近5天 状态为执行中 如果在明细表中不存在未推送的记录，则将其状态更新为完成)');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', 'UPDATE uo_op_activity_header T
        SET T.PREHEAT_STATUS = ''done''
        where t.preheat_status = ''doing''
            and t.has_preheat=''1''
            and  trunc(sysdate) >t.preheat_end_dt
            and  exists (
            select p.head_id from uo_op_activity_plan p where p.head_id=t.head_id and p.plan_status in(''3'') and p.activity_stage=''preheat''
            )', null, null, '更新活动头表预售状态由已执行更新为完成', '5', 'UPDATE', '更新活动头表预售状态由已执行更新为完成(活动结束后有一条计划为完成)');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('activity', 'Y', 'SQL', ' UPDATE uo_op_activity_header T
        SET T.FORMAL_STATUS = ''done''
        where t.FORMAL_STATUS = ''doing'' and t.has_preheat=''0''
            and  trunc(sysdate) >t.Formal_End_Dt
            and  exists (
            select p.head_id from uo_op_activity_plan p where p.head_id=t.head_id and p.plan_status in(''3'') and p.activity_stage=''formal''
            )', null, null, '更新活动头表正式状态由已执行更新为完成', '6', 'UPDATE', '更新活动头表正式状态由已执行更新为完成(活动结束后有一条计划为完成)');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', 'merge into uo_op_push_list t1
            using  uo_op_push_rpt t2
            on (t1.msgid = t2.msgid and trunc(t1.push_date)=trunc(sysdate-3))
        when matched then
        update set t1.push_status=t2.status', null, null, '更新推送状态', '1', 'UPDATE', '根据运营商返回的状态报告，将短信推送状态更新到PUSH_LIST');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', 'merge into uo_op_daily_detail t3
using uo_op_push_list t4
on (t3.daily_detail_id = t4.source_id and t4.source_code = ''D'' and trunc(t3.insert_dt)>trunc(sysdate-3))
when matched then
  update
     set t3.push_status = t4.push_status,
         t3.push_date   = t4.push_date,
         t3.is_push     = t4.is_push', null, null, '更新每日运营明细表的推送状态/推送日期', '2', 'UPDATE', '将PUSH_LIST的推送状态/推送日期/是否推送同步回DAILY_DETAIL');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('daily', 'Y', 'SQL', ' UPDATE UO_OP_DAILY_HEADER c SET c.STATUS=''finished'' WHERE c.STATUS=''done'' AND not exists
(
select * from UO_OP_DAILY_DETAIL p where p.PUSH_STATUS=''P'' and p.head_id=c.head_id
) AND TRUNC(C.INSERT_DT)>TRUNC(SYSDATE-3) ', null, null, '将每日运营头表由已执行更新为已结束', '3', 'UPDATE', '将每日运营头表由已执行更新为已结束');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
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

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'N', 'SQL', 'MERGE INTO UO_OP_PUSH_LIST_LARGE C USING
(SELECT T.PUSH_ID,
       ROW_NUMBER()OVER(PARTITION BY T.MSGID ORDER BY T.PUSH_ID DESC) RN
 FROM UO_OP_PUSH_LIST_LARGE T WHERE T.IS_PUSH=''1'' AND T.PUSH_STATUS!=''C'' AND T.MSGID IS NOT NULL) T1
ON (C.PUSH_ID=T1.PUSH_ID AND TRUNC(C.PUSH_DATE)>=TRUNC(SYSDATE-5))
WHEN MATCHED THEN
   UPDATE SET C.FINAL_MSG_ID=C.MSGID+(RN-1) ', null, null, '修复PUSH_LARGE中的FINAL_MSG_ID', '1', 'UPDATE', '修复PUSH_LARGE中的FINAL_MSG_ID(梦网已测试)  活动运营中也存在');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'N', 'SQL', 'merge into uo_op_push_list_large t1
            using  uo_op_push_rpt t2
            on (t1.final_msg_id = t2.msgid and trunc(t1.push_date)>trunc(sysdate-3))
        when matched then
        update set t1.push_status=t2.status', null, null, '更新PUSH_LARGE中的推送状态', '2', 'UPDATE', '根据运营商返回的状态报告，更新PUSH_LARGE中的推送状态');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', ' update uo_op_manual_header c set (c.success_num,c.faild_num,c.intercept_num)=(
            select
                sum(case when d.push_status=''S'' then 1 else 0 end)  success_num,
                sum(case when d.push_status=''F'' then 1 else 0 end)  faild_num,
                sum(case when d.push_status=''C'' then 1 else 0 end) intercept_num
            from uo_op_manual_detail d where d.head_id=c.head_id
        ) where trunc(c.SCHEDULE_DATE) >=trunc(sysdate-3)', null, null, '更新手工推送头表的推送统计字段', '4', 'UPDATE', '更新手工推送头表的推送统计字段');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', ' merge into uo_op_manual_detail t1
            using  uo_op_push_list_large t2
            on (t1.detail_id = t2.source_id and t2.source_code=''M'' and t1.head_id in (
                select h.head_id from uo_op_manual_header h where h.status in (''1'',''2'') and trunc(h.SCHEDULE_DATE)>=trunc(sysdate-3)
        ) )
        when matched then
        update set t1.push_status=t2.push_status,t1.push_date=t2.push_date', null, null, '更新手工推送明细的推送状态/推送时间', '3', 'UPDATE', '更新手工推送明细的推送状态/推送时间');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', ' update uo_op_manual_header c set c.status=''2'' where trunc(c.SCHEDULE_DATE) >= trunc(sysdate-3) and c.status=''1''
            and not exists (
        select 1 from uo_op_manual_detail d where d.head_id=c.head_id and d.push_status=''P'')', null, null, '更新手工推送头表由计划中待完成到推送完成', '5', 'UPDATE', '更新手工推送头表由计划中待完成到推送完成');

insert into UO_OP_EXEC_STEPS (KEY_NAME, IS_VALID, STEP_TYPE, SQL_CONTENT, BEAN_NAME, METHOD_NAME, STEP_NAME, ORDER_NO, SQL_TYPE, COMMENTS)
values ('manual', 'Y', 'SQL', 'update uo_op_manual_header c set c.actual_push_date=(
            select
                max(d.push_date)
            from uo_op_manual_detail d where d.head_id=c.head_id
        ) where trunc(c.SCHEDULE_DATE) >= trunc(sysdate-3) and c.status=''2''', null, null, '更新手工推送表的实际推送时间', '6', 'UPDATE', '更新手工推送表的实际推送时间');

prompt Done.


insert into t_config (NAME, VALUE, COMMENTS, ORDER_NUM, TYPE_CODE1, TYPE_CODE2)
values ('op.daily.default.effectDays', '3', '每日运营效果统计默认天数', '0', 'APPS', null);

ALTER TABLE t_config MODIFY type_code1 not  NULL;
alter table t_config modify value VARCHAR2(256);


