-----------------------------------------------------
-- Export file for user GIMSPROMOTE_DEV@ALIYUN_201 --
-- Created by huang on 2020/3/20, 16:54:37 ----------
-----------------------------------------------------

set define off
spool seq.log

prompt
prompt Creating sequence SEQ_DIAG_ID
prompt =============================
prompt
create sequence SEQ_DIAG_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 2221
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_FUN_SYS_LOG_ID
prompt ====================================
prompt
create sequence SEQ_FUN_SYS_LOG_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_JOB_HISTORY_ID
prompt ====================================
prompt
create sequence SEQ_JOB_HISTORY_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 182
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_JOB_ID
prompt ============================
prompt
create sequence SEQ_JOB_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 87
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_JOB_LOG_ID
prompt ================================
prompt
create sequence SEQ_JOB_LOG_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 8320
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_NODE_ID
prompt =============================
prompt
create sequence SEQ_NODE_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 3481
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_REASON_RESULT
prompt ===================================
prompt
create sequence SEQ_REASON_RESULT
minvalue 1
maxvalue 9999999999999999999999999999
start with 21
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_TGT_DIMENSION_ID
prompt ======================================
prompt
create sequence SEQ_TGT_DIMENSION_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 61
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_TGT_DISMANT_ID
prompt ====================================
prompt
create sequence SEQ_TGT_DISMANT_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 201
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_TGT_LIST_ID
prompt =================================
prompt
create sequence SEQ_TGT_LIST_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 181
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_UO_PLAN_DETAIL_ID
prompt =======================================
prompt
create sequence SEQ_UO_PLAN_DETAIL_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 2681
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_UO_PLAN_ID
prompt ================================
prompt
create sequence SEQ_UO_PLAN_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 381
increment by 1
cache 20;

prompt
prompt Creating sequence SEQ_W_CMPN_ORDER_DETAILS_ID
prompt =============================================
prompt
create sequence SEQ_W_CMPN_ORDER_DETAILS_ID
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 10;

prompt
prompt Creating sequence UO_GMV_PLAN_DETAIL_SEQ
prompt ========================================
prompt
create sequence UO_GMV_PLAN_DETAIL_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;

prompt
prompt Creating sequence UO_GMV_PLAN_SEQ
prompt =================================
prompt
create sequence UO_GMV_PLAN_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;

prompt
prompt Creating sequence UO_OP_PERIOD_SEQ
prompt ==================================
prompt
create sequence UO_OP_PERIOD_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 41
increment by 1
cache 20;

prompt
prompt Creating sequence UO_REASON_LIST_SEQ
prompt ====================================
prompt
create sequence UO_REASON_LIST_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 301
increment by 1
cache 20;

prompt
prompt Creating sequence W_ETL_LOG_SEQ
prompt ===============================
prompt
create sequence W_ETL_LOG_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 321
increment by 1
cache 20;


spool off
