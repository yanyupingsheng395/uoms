1.2019.12.13

版本更新：

新增：
活动运营-查看效果
用户成长洞察

版本SQL：
活动运营-查看效果

用户成长洞察：
create table UO_INSIGHT_GROWTH_PATH
(
  COPS_VALUE       NUMBER,
  INCOME_VALUE     NUMBER,
  STEP_VALUE       NUMBER,
  UNIVERS_VALUE    NUMBER,
  PURCH1_SPU_ID    VARCHAR2(64),
  PURCH2_SPU_ID    VARCHAR2(64),
  PURCH3_SPU_ID    VARCHAR2(64),
  PURCH4_SPU_ID    VARCHAR2(64),
  PURCH5_SPU_ID    VARCHAR2(64),
  PURCH6_SPU_ID    VARCHAR2(64),
  PURCH7_SPU_ID    VARCHAR2(64),
  PURCH8_SPU_ID    VARCHAR2(64),
  PURCH9_SPU_ID    VARCHAR2(64),
  PURCH10_SPU_ID   VARCHAR2(64),
  PURCH1_SPU_NAME  VARCHAR2(64),
  PURCH2_SPU_NAME  VARCHAR2(64),
  PURCH3_SPU_NAME  VARCHAR2(64),
  PURCH4_SPU_NAME  VARCHAR2(64),
  PURCH5_SPU_NAME  VARCHAR2(64),
  PURCH6_SPU_NAME  VARCHAR2(64),
  PURCH7_SPU_NAME  VARCHAR2(64),
  PURCH8_SPU_NAME  VARCHAR2(64),
  PURCH9_SPU_NAME  VARCHAR2(64),
  PURCH10_SPU_NAME VARCHAR2(64),
  SPU_PATH         VARCHAR2(512)
)
/

comment on table UO_INSIGHT_GROWTH_PATH is '成长洞察-成长旅程表'
/

comment on column UO_INSIGHT_GROWTH_PATH.COPS_VALUE is '综合价值'
/

comment on column UO_INSIGHT_GROWTH_PATH.INCOME_VALUE is '收入价值'
/

comment on column UO_INSIGHT_GROWTH_PATH.STEP_VALUE is '步长价值'
/

comment on column UO_INSIGHT_GROWTH_PATH.UNIVERS_VALUE is '普适性价值'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH1_SPU_ID is '1购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH2_SPU_ID is '2购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH3_SPU_ID is '3购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH4_SPU_ID is '4购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH5_SPU_ID is '5购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH6_SPU_ID is '6购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH7_SPU_ID is '7购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH8_SPU_ID is '8购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH9_SPU_ID is '9购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH10_SPU_ID is '10购SPU ID'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH1_SPU_NAME is '1购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH2_SPU_NAME is '2购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH3_SPU_NAME is '3购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH4_SPU_NAME is '4购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH5_SPU_NAME is '5购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH6_SPU_NAME is '6购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH7_SPU_NAME is '7购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH8_SPU_NAME is '8购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH9_SPU_NAME is '9购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.PURCH10_SPU_NAME is '10购SPU名称'
/

comment on column UO_INSIGHT_GROWTH_PATH.SPU_PATH is 'SPU购买路径'
/

create table UO_INSIGHT_IMPORT_SPU
(
  SPU_ID            VARCHAR2(16),
  SPU_NAME          VARCHAR2(32),
  PURCH_ORDER       NUMBER,
  CONTRIBUTE_RATE   NUMBER,
  NEXT_PURCH_PROBAL NUMBER,
  SAME_SPU_PROBAL   NUMBER,
  OTHER_SPU_PROBAL  NUMBER,
  COMPUTE_DT        NUMBER,
  USER_CNT          NUMBER
)
/

comment on table UO_INSIGHT_IMPORT_SPU is '成长洞察-重要SPU表'
/

comment on column UO_INSIGHT_IMPORT_SPU.SPU_ID is 'SPU ID'
/

comment on column UO_INSIGHT_IMPORT_SPU.SPU_NAME is 'SPU名称'
/

comment on column UO_INSIGHT_IMPORT_SPU.PURCH_ORDER is '购买次序'
/

comment on column UO_INSIGHT_IMPORT_SPU.CONTRIBUTE_RATE is '本次购买的用户贡献率（%）'
/

comment on column UO_INSIGHT_IMPORT_SPU.NEXT_PURCH_PROBAL is '本购spu后再购概率（%）'
/

comment on column UO_INSIGHT_IMPORT_SPU.SAME_SPU_PROBAL is '本购spu后再购同spu概率（%）'
/

comment on column UO_INSIGHT_IMPORT_SPU.OTHER_SPU_PROBAL is '本购spu后购其他spu概率（%）'
/

comment on column UO_INSIGHT_IMPORT_SPU.COMPUTE_DT is '计算日期'
/

comment on column UO_INSIGHT_IMPORT_SPU.USER_CNT is '用户数'
/

create table UO_INSIGHT_SANKEY
(
  SOURCE_SPU_ID VARCHAR2(32),
  TARGET_SPU_ID VARCHAR2(32),
  SOURCE_NAME   VARCHAR2(64),
  TARGET_NAME   VARCHAR2(64),
  USER_CNT      NUMBER,
  DATE_RANGE    NUMBER,
  COMPUT_DT     NUMBER,
  T_RN          NUMBER,
  S_RN          NUMBER,
  RN            NUMBER,
  ID            NUMBER,
  DATA_TYPE     VARCHAR2(64)
)
/

comment on table UO_INSIGHT_SANKEY is '成长洞察-SPU桑基图'
/

comment on column UO_INSIGHT_SANKEY.SOURCE_SPU_ID is '源SPU ID'
/

comment on column UO_INSIGHT_SANKEY.TARGET_SPU_ID is '目标SPU ID'
/

comment on column UO_INSIGHT_SANKEY.SOURCE_NAME is '源SPU名称'
/

comment on column UO_INSIGHT_SANKEY.TARGET_NAME is '目标SPU名称'
/

comment on column UO_INSIGHT_SANKEY.USER_CNT is '用户数'
/

comment on column UO_INSIGHT_SANKEY.DATE_RANGE is '时间范围（1：1月，3：3月，6：6月，12：12月）'
/

comment on column UO_INSIGHT_SANKEY.COMPUT_DT is '计算日期'
/

comment on column UO_INSIGHT_SANKEY.T_RN is '源顺序'
/

comment on column UO_INSIGHT_SANKEY.S_RN is '目标顺序'
/

comment on column UO_INSIGHT_SANKEY.RN is '排序'
/

comment on column UO_INSIGHT_SANKEY.DATA_TYPE is 'C:当前数据,B:30天前的数据'
/

create table UO_INSIGHT_USER_CNT
(
  PURCH_ORDER       NUMBER,
  CURRENT_USER_CNT  NUMBER,
  BEFORE30_USER_CNT NUMBER,
  DATE_RANGE        NUMBER,
  COMPUTE_DT        NUMBER
)
/

comment on table UO_INSIGHT_USER_CNT is '成长洞察-用户数随购买次序表'
/

comment on column UO_INSIGHT_USER_CNT.PURCH_ORDER is '购买次序（1购到8购）'
/

comment on column UO_INSIGHT_USER_CNT.CURRENT_USER_CNT is '当前用户数（人次）'
/

comment on column UO_INSIGHT_USER_CNT.BEFORE30_USER_CNT is '30天前用户数（人次）'
/

comment on column UO_INSIGHT_USER_CNT.DATE_RANGE is '时间范围（1：1月，3：3月，6：6月，12：12月）'
/

comment on column UO_INSIGHT_USER_CNT.COMPUTE_DT is '计算日期'
/

#