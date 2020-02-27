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
comment on column uo_op_activity_template.group_id is '使用场景 存储GROUP_ID';
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


/**
* 要删除的表
*/
DROP TABLE UO_OP_ACTIVITY_SUMMARY;
DROP TABLE UO_OP_ACTIVITY_PREDICT;
DROP TABLE UO_OP_ACTIVITY_BIG_CONFIG;

