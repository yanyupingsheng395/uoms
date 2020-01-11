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

comment on column UO_OP_SMS_TEMPLATE.IS_COUPON is '体现补贴链接与名称(1:是，0:否)'
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