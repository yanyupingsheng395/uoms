--date:2020.1.11
alter table UO_OP_SMS_TEMPLATE
    add SMS_NAME VARCHAR2(64)
/

alter table UO_OP_SMS_TEMPLATE
    add IS_PRODUCT_NAME VARCHAR2(8)
/

alter table UO_OP_SMS_TEMPLATE
    add IS_PRODUCT_URL VARCHAR2(8)
/

alter table UO_OP_SMS_TEMPLATE
    add REMARK VARCHAR2(128)
/

comment on column UO_OP_SMS_TEMPLATE.IS_COUPON_NAME is '体现补贴链接与名称(1:是，0:否)'
/

comment on column UO_OP_SMS_TEMPLATE.SMS_NAME is '文案名称'
/

comment on column UO_OP_SMS_TEMPLATE.IS_PRODUCT_NAME is '体现商品名称(1:是，0:否)'
/

comment on column UO_OP_SMS_TEMPLATE.IS_PRODUCT_URL is '体现商品详情页(1:是，0:否)'
/

comment on column UO_OP_SMS_TEMPLATE.REMARK is '其他说明'
/

create sequence SEQ_SMS_TEMPLATE_CODE;


alter table UO_OP_COUPON
    add COUPON_SOURCE VARCHAR2(2)
/

comment on column UO_OP_COUPON.COUPON_SOURCE is '优惠券类型（0：智能，1：手动）'
/

alter table UO_OP_SMS_TEMPLATE
    add USER_VALUE VARCHAR2(256)
/

comment on column UO_OP_SMS_TEMPLATE.USER_VALUE is '用户价值（多个数据之间逗号分隔）'
/

alter table UO_OP_SMS_TEMPLATE
    add LIFE_CYCLE VARCHAR2(256)
/

comment on column UO_OP_SMS_TEMPLATE.LIFE_CYCLE is '生命周期，多条数据之间逗号分隔'
/

alter table UO_OP_SMS_TEMPLATE
    add PATH_ACTIVE VARCHAR2(256)
/

comment on column UO_OP_SMS_TEMPLATE.PATH_ACTIVE is '用户活跃度，多条数据之间逗号分隔'
/

alter table UO_OP_COUPON
    add USER_VALUE VARCHAR2(256)
/

comment on column UO_OP_COUPON.USER_VALUE is '用户价值'
/

alter table UO_OP_COUPON
    add LIFE_CYCLE VARCHAR2(256)
/

comment on column UO_OP_COUPON.LIFE_CYCLE is '生命周期'
/

alter table UO_OP_COUPON
    add PATH_ACTIVE VARCHAR2(256)
/

comment on column UO_OP_COUPON.PATH_ACTIVE is '活跃度'
/

alter table UO_OP_COUPON
    add DISCOUNT_LEVEL NUMBER
/

comment on column UO_OP_COUPON.DISCOUNT_LEVEL is '折扣力度'
/

alter table UO_OP_COUPON
    add CHECK_FLAG VARCHAR2(2)
/

comment on column UO_OP_COUPON.CHECK_FLAG is '校验结果 0:不通过，1：通过'
/

alter table UO_OP_COUPON
    add CHECK_COMMENTS VARCHAR2(128)
/

comment on column UO_OP_COUPON.CHECK_COMMENTS is '校验备注'
/

alter table UO_OP_DAILY_HEADER
    add VALID_STATUS VARCHAR2(2)
/

comment on column UO_OP_DAILY_HEADER.VALID_STATUS is '配置校验状态'
/

alter table UO_OP_DAILY_TEMPLATE_CONFIG
    add GROUP_INFO VARCHAR2(256)
/

comment on column UO_OP_DAILY_TEMPLATE_CONFIG.GROUP_INFO is '群组理解'
/

update UO_OP_SMS_TEMPLATE set SMS_CODE = SEQ_SMS_TEMPLATE_CODE.nextval;

--2020.1.16
alter table UO_OP_SMS_TEMPLATE rename column IS_COUPON to IS_COUPON_NAME
/

comment on column UO_OP_SMS_TEMPLATE.IS_COUPON_NAME is '体现补贴名称(1:是，0:否)'
/

alter table UO_OP_SMS_TEMPLATE modify IS_COUPON_NAME VARCHAR2(2)
/

alter table UO_OP_SMS_TEMPLATE
    add IS_COUPON_URL VARCHAR2(2)
/

comment on column UO_OP_SMS_TEMPLATE.IS_COUPON_URL is '体现补贴链接(1:是，0:否)'
/

alter table UO_OP_DAILY_GROUP_COUPON add (insert_dt date default sysdate );

comment on column UO_OP_DAILY_GROUP_COUPON.insert_dt is '创建时间';

alter table uo_op_daily_header add (OP_CHANGED_TIME number default 0);
comment on column uo_op_daily_header.OP_CHANGED_TIME is '操作时间戳';

alter table uo_op_daily_header drop column valid_status;

alter table t_dict add (order_no number default 0);
comment on column t_dict.order_no is '排序号';

alter table t_config drop column module_type;


truncate table T_DICT;

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('15', '1', '新用户', 'LIFECYCLE', '用户生命周期', '1');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('14', '0', '复购用户', 'LIFECYCLE', '用户生命周期', '2');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('1', 'UAC_01', '活跃', 'PATH_ACTIVE', '用户活跃度', '0');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('2', 'UAC_02', '留存', 'PATH_ACTIVE', '用户活跃度', '1');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('3', 'UAC_03', '流失预警', 'PATH_ACTIVE', '用户活跃度', '2');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('4', 'UAC_04', '弱流失', 'PATH_ACTIVE', '用户活跃度', '3');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('5', 'UAC_05', '强流失', 'PATH_ACTIVE', '用户活跃度', '4');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('6', 'UAC_06', '沉睡', 'USER_ACTIVE', '用户活跃度', '5');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('10', 'ULC_01', '重要', 'USER_VALUE', '用户价值', '0');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('11', 'ULC_02', '主要', 'USER_VALUE', '用户价值', '1');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('12', 'ULC_03', '普通', 'USER_VALUE', '用户价值', '2');

insert into T_DICT (DICT_ID, CODE, VALUE, TYPE_CODE, TYPE_NAME, ORDER_NO)
values ('13', 'ULC_04', '长尾', 'USER_VALUE', '用户价值', '3');

truncate table T_CONFIG;

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('system.name', '用户成长系统', '登录页系统的名称', '1');

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('system.logo.url', '/images/logo.png', '系统logo的url', '2');

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('op.daily.pathactive.list', 'UAC_03,UAC_04', '每日运营开启的活跃度组', '3');

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('op.daily.sms.cunponurl', 'https://tb.cn.hn/t8n', '每日运营短信测试默认填充的优惠券链接', '0');

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('op.daily.sms.couponname', '5元券', '每日运营短信测试默认填充的优惠券名称', '0');

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('op.daily.sms.prodname', '测试商品', '每日运营短信测试默认填充的商品名称', '0');

insert into T_CONFIG (NAME, VALUE, COMMENTS, ORDER_NUM)
values ('op.daily.sms.produrl', 'https://tb.cn.hn/t8n', '每日运营短信测试默认填充的商品链接', '0');
