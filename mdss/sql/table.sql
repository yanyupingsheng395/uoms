-----------------------------------------------------
-- Export file for user GIMSPROMOTE_DEV@ALIYUN_201 --
-- Created by huang on 2020/3/20, 13:55:12 ----------
-----------------------------------------------------

set define off
spool mdss.log

prompt
prompt Creating table UO_COSTINFO_SET
prompt ==============================
prompt
create table UO_COSTINFO_SET
(
  year_id    NUMBER,
  month_id   NUMBER,
  cost_value NUMBER
)
;
comment on table UO_COSTINFO_SET
is '成本信息维护';
comment on column UO_COSTINFO_SET.year_id
is '年ID';
comment on column UO_COSTINFO_SET.month_id
is '月份ID';
comment on column UO_COSTINFO_SET.cost_value
is '成本信息';
create unique index UO_COSTINFO_SET_U1 on UO_COSTINFO_SET (MONTH_ID);

prompt
prompt Creating table UO_DAY_HISTORY
prompt =============================
prompt
create table UO_DAY_HISTORY
(
  day_id     NUMBER,
  month_id   NUMBER,
  year_id    NUMBER,
  gmv_value  NUMBER,
  gmv_tqz    NUMBER,
  gmv_hbz    NUMBER,
  gmv_tq_pct NUMBER,
  gmv_hb_pct NUMBER
)
;
comment on table UO_DAY_HISTORY
is 'GMV按天的快照表';
comment on column UO_DAY_HISTORY.day_id
is '天';
comment on column UO_DAY_HISTORY.month_id
is '月份';
comment on column UO_DAY_HISTORY.year_id
is '年';
comment on column UO_DAY_HISTORY.gmv_value
is 'GMV值';
comment on column UO_DAY_HISTORY.gmv_tqz
is 'GMV同期值';
comment on column UO_DAY_HISTORY.gmv_hbz
is 'GMV环比值';
comment on column UO_DAY_HISTORY.gmv_tq_pct
is 'GMV同比';
comment on column UO_DAY_HISTORY.gmv_hb_pct
is 'GMV环比';

prompt
prompt Creating table UO_DIAG_CONDITION
prompt ================================
prompt
create table UO_DIAG_CONDITION
(
  diag_id           NUMBER,
  node_id           NUMBER,
  dim_code          VARCHAR2(32),
  dim_values        VARCHAR2(2000),
  dim_value_display VARCHAR2(4000),
  inherit_flag      VARCHAR2(8) default 'N',
  dim_name          VARCHAR2(32),
  create_dt         DATE
)
;
comment on table UO_DIAG_CONDITION
is '诊断-节点条件表';
comment on column UO_DIAG_CONDITION.diag_id
is '诊断ID';
comment on column UO_DIAG_CONDITION.node_id
is '节点ID';
comment on column UO_DIAG_CONDITION.dim_code
is '维度编码';
comment on column UO_DIAG_CONDITION.dim_values
is '维度值';
comment on column UO_DIAG_CONDITION.dim_value_display
is '维度显示值';
comment on column UO_DIAG_CONDITION.inherit_flag
is '是否从父节点继承  Y表示是 N表示否';
comment on column UO_DIAG_CONDITION.dim_name
is '维度名称';
comment on column UO_DIAG_CONDITION.create_dt
is '创建日期';

prompt
prompt Creating table UO_DIAG_DETAIL
prompt =============================
prompt
create table UO_DIAG_DETAIL
(
  diag_id      NUMBER,
  node_id      NUMBER,
  parent_id    NUMBER,
  node_name    VARCHAR2(2000),
  kpi_code     VARCHAR2(32),
  kpi_level_id NUMBER,
  alarm_flag   VARCHAR2(2),
  kpi_name     VARCHAR2(32)
)
;
comment on table UO_DIAG_DETAIL
is '诊断-明细列表';
comment on column UO_DIAG_DETAIL.diag_id
is '诊断ID';
comment on column UO_DIAG_DETAIL.node_id
is '节点ID 如果为-1表示根节点';
comment on column UO_DIAG_DETAIL.parent_id
is '父节点ID';
comment on column UO_DIAG_DETAIL.node_name
is '节点名称';
comment on column UO_DIAG_DETAIL.kpi_code
is '指标编码';
comment on column UO_DIAG_DETAIL.kpi_level_id
is '指标等级ID';
comment on column UO_DIAG_DETAIL.alarm_flag
is '是否标记为问题';
comment on column UO_DIAG_DETAIL.kpi_name
is '指标名称';

prompt
prompt Creating table UO_DIAG_LIST
prompt ===========================
prompt
create table UO_DIAG_LIST
(
  diag_id          NUMBER,
  diag_name        VARCHAR2(32),
  period_type      VARCHAR2(32),
  begin_dt         VARCHAR2(32),
  end_dt           VARCHAR2(32),
  create_dt        DATE,
  update_dt        DATE,
  create_by        VARCHAR2(32),
  update_by        VARCHAR2(32),
  kpi_code         VARCHAR2(32),
  dim_display_name VARCHAR2(4000)
)
;
comment on table UO_DIAG_LIST
is '诊断列表';
comment on column UO_DIAG_LIST.diag_id
is '主键ID';
comment on column UO_DIAG_LIST.diag_name
is '诊断名称';
comment on column UO_DIAG_LIST.period_type
is '周期类型 M表示按月 Y表示按天';
comment on column UO_DIAG_LIST.begin_dt
is '周期开始时间';
comment on column UO_DIAG_LIST.end_dt
is '周期结束时间';
comment on column UO_DIAG_LIST.create_dt
is '创建时间';
comment on column UO_DIAG_LIST.update_dt
is '更新时间';
comment on column UO_DIAG_LIST.create_by
is '创建人';
comment on column UO_DIAG_LIST.update_by
is '更新人';
comment on column UO_DIAG_LIST.kpi_code
is '指标编码';
comment on column UO_DIAG_LIST.dim_display_name
is '维度和值显示';
create unique index UO_DIAG_LIST_U1 on UO_DIAG_LIST (DIAG_ID);

prompt
prompt Creating table UO_DIM_JOINRELATION_CONFIG
prompt =========================================
prompt
create table UO_DIM_JOINRELATION_CONFIG
(
  dirver_table_name VARCHAR2(32),
  dim_code          VARCHAR2(32),
  dim_table         VARCHAR2(512),
  relation          VARCHAR2(256),
  dim_table_alias   VARCHAR2(32),
  dim_where         VARCHAR2(32),
  dim_where_type    VARCHAR2(32)
)
;
comment on table UO_DIM_JOINRELATION_CONFIG
is '维度表映射关系配置';
comment on column UO_DIM_JOINRELATION_CONFIG.dirver_table_name
is '驱动表名称';
comment on column UO_DIM_JOINRELATION_CONFIG.dim_code
is '维度编码';
comment on column UO_DIM_JOINRELATION_CONFIG.dim_table
is '维度编码对应的维度表名称';
comment on column UO_DIM_JOINRELATION_CONFIG.relation
is '关联关系';
comment on column UO_DIM_JOINRELATION_CONFIG.dim_table_alias
is '维度表别名';
comment on column UO_DIM_JOINRELATION_CONFIG.dim_where
is '维度进行过滤的列名称';
comment on column UO_DIM_JOINRELATION_CONFIG.dim_where_type
is '维度过滤列的类型';

prompt
prompt Creating table UO_DIM_LIST_CONFIG
prompt =================================
prompt
create table UO_DIM_LIST_CONFIG
(
  dim_code         VARCHAR2(32),
  dim_name         VARCHAR2(32),
  value_type       VARCHAR2(32),
  value_sql        VARCHAR2(512),
  is_all           VARCHAR2(2),
  reason_flag      VARCHAR2(32),
  diag_flag        VARCHAR2(2),
  order_no         NUMBER,
  rely_orderdetail VARCHAR2(32) default 'N'
)
;
comment on table UO_DIM_LIST_CONFIG
is '维度列表';
comment on column UO_DIM_LIST_CONFIG.dim_code
is '维度编码';
comment on column UO_DIM_LIST_CONFIG.dim_name
is '维度名称';
comment on column UO_DIM_LIST_CONFIG.value_type
is '维度值类型  L表示有限列表 S表示从SQL取值';
comment on column UO_DIM_LIST_CONFIG.value_sql
is 'SQL语句，默认第一列为CODE，第二列为显示名';
comment on column UO_DIM_LIST_CONFIG.is_all
is '是否需要还有所有选项';
comment on column UO_DIM_LIST_CONFIG.reason_flag
is '原因探究模块使用标记';
comment on column UO_DIM_LIST_CONFIG.diag_flag
is '诊断模型使用标记 Y表示使用 N表示未使用';
comment on column UO_DIM_LIST_CONFIG.order_no
is '排序序号';
comment on column UO_DIM_LIST_CONFIG.rely_orderdetail
is '是否依赖订单明细(只能关联到订单明细)';
create unique index UO_DIM_LIST_U1 on UO_DIM_LIST_CONFIG (DIM_CODE);

prompt
prompt Creating table UO_DIM_VALUES_CONFIG
prompt ===================================
prompt
create table UO_DIM_VALUES_CONFIG
(
  dim_code   VARCHAR2(32),
  value_code VARCHAR2(32),
  value_desc VARCHAR2(32),
  order_no   NUMBER
)
;
comment on table UO_DIM_VALUES_CONFIG
is '维度值列表配置';
comment on column UO_DIM_VALUES_CONFIG.dim_code
is '维度类型';
comment on column UO_DIM_VALUES_CONFIG.value_code
is '值编码';
comment on column UO_DIM_VALUES_CONFIG.value_desc
is '值类型';
comment on column UO_DIM_VALUES_CONFIG.order_no
is '排序号';

prompt
prompt Creating table UO_KEYPOINT_HINTINFO
prompt ===================================
prompt
create table UO_KEYPOINT_HINTINFO
(
  year_id   NUMBER,
  month_id  NUMBER,
  hint_info VARCHAR2(2000),
  hint_type VARCHAR2(2),
  create_dt DATE,
  update_dt DATE,
  order_no  NUMBER
)
;
comment on table UO_KEYPOINT_HINTINFO
is '核心指标预警及合理化建议';
comment on column UO_KEYPOINT_HINTINFO.year_id
is '年份ID';
comment on column UO_KEYPOINT_HINTINFO.month_id
is '月份ID  如果此字段有值，表示为月份的提示，否则为年的提示；';
comment on column UO_KEYPOINT_HINTINFO.hint_info
is '内容文本';
comment on column UO_KEYPOINT_HINTINFO.hint_type
is '内容类型 A表示警告 P表示提示';
comment on column UO_KEYPOINT_HINTINFO.create_dt
is '创建时间';
comment on column UO_KEYPOINT_HINTINFO.update_dt
is '更新时间';
comment on column UO_KEYPOINT_HINTINFO.order_no
is '排序序号';

prompt
prompt Creating table UO_KPI_DISMANT_CONFIG
prompt ====================================
prompt
create table UO_KPI_DISMANT_CONFIG
(
  kpi_code           VARCHAR2(32),
  kpi_name           VARCHAR2(32),
  dismant_part1_code VARCHAR2(32),
  dismant_part1_name VARCHAR2(32),
  dismant_part2_code VARCHAR2(32),
  dismant_part2_name VARCHAR2(32)
)
;
comment on table UO_KPI_DISMANT_CONFIG
is '诊断-指标拆解明细';
comment on column UO_KPI_DISMANT_CONFIG.kpi_code
is '指标CODE';
comment on column UO_KPI_DISMANT_CONFIG.kpi_name
is '指标名称';
comment on column UO_KPI_DISMANT_CONFIG.dismant_part1_code
is '乘法第一部分指标code';
comment on column UO_KPI_DISMANT_CONFIG.dismant_part1_name
is '乘法第一部分指标名称';
comment on column UO_KPI_DISMANT_CONFIG.dismant_part2_code
is '乘法第二部分指标code';
comment on column UO_KPI_DISMANT_CONFIG.dismant_part2_name
is '乘法第二部分指标名称';

prompt
prompt Creating table UO_KPI_LIST_CONFIG
prompt =================================
prompt
create table UO_KPI_LIST_CONFIG
(
  kpi_code        VARCHAR2(32),
  kpi_name        VARCHAR2(500),
  reason_flag     VARCHAR2(2),
  dismant_flag    VARCHAR2(32),
  diag_flag       VARCHAR2(32),
  dismant_formula VARCHAR2(64),
  axis_name       VARCHAR2(32),
  value_format    VARCHAR2(32)
)
;
comment on table UO_KPI_LIST_CONFIG
is 'KPI列表';
comment on column UO_KPI_LIST_CONFIG.kpi_code
is 'KPI唯一标记';
comment on column UO_KPI_LIST_CONFIG.kpi_name
is 'KPI名称';
comment on column UO_KPI_LIST_CONFIG.reason_flag
is '标记1（原因探究列表）';
comment on column UO_KPI_LIST_CONFIG.dismant_flag
is '是否可拆解  Y表示是 N表示不可拆解';
comment on column UO_KPI_LIST_CONFIG.diag_flag
is '标记2 （诊断） Y表示可使用 N表示不可使用 （待删除，无实际意义）';
comment on column UO_KPI_LIST_CONFIG.dismant_formula
is '指标拆解公式';
comment on column UO_KPI_LIST_CONFIG.axis_name
is '坐标轴名称';
comment on column UO_KPI_LIST_CONFIG.value_format
is '指标值格式化类型 D表示整数 D后面如果有数字则表示保留小数位数  如果加F表示科学计数法';
create unique index UO_KPI_LIST_U1 on UO_KPI_LIST_CONFIG (KPI_CODE);

prompt
prompt Creating table UO_KPI_SQLTEMPLATE_CONFIG
prompt ========================================
prompt
create table UO_KPI_SQLTEMPLATE_CONFIG
(
  sql_template_code VARCHAR2(32),
  sql_template      VARCHAR2(4000),
  comments          VARCHAR2(256),
  result_type       VARCHAR2(256),
  result_mapping    VARCHAR2(256),
  driver_tables     VARCHAR2(2000)
)
;
comment on table UO_KPI_SQLTEMPLATE_CONFIG
is '指标的SQL模板信息';
comment on column UO_KPI_SQLTEMPLATE_CONFIG.sql_template_code
is 'SQL模板编码';
comment on column UO_KPI_SQLTEMPLATE_CONFIG.sql_template
is 'SQL模板内容';
comment on column UO_KPI_SQLTEMPLATE_CONFIG.comments
is '描述';
comment on column UO_KPI_SQLTEMPLATE_CONFIG.result_type
is '结果返回值';
comment on column UO_KPI_SQLTEMPLATE_CONFIG.result_mapping
is '结果映射类型';
comment on column UO_KPI_SQLTEMPLATE_CONFIG.driver_tables
is '驱动表(变量名：驱动表名称 格式，多个以 ； 分号分割)';

prompt
prompt Creating table UO_MONTH_HISTORY
prompt ===============================
prompt
create table UO_MONTH_HISTORY
(
  month_id  NUMBER,
  year_id   NUMBER,
  gmv_value NUMBER,
  create_dt DATE,
  update_dt DATE
)
;
comment on table UO_MONTH_HISTORY
is 'GMV月度历史记录表';

prompt
prompt Creating table UO_MONTH_KEYPOINT
prompt ================================
prompt
create table UO_MONTH_KEYPOINT
(
  month_id           NUMBER,
  year_id            NUMBER,
  gmv_actual         NUMBER,
  gmv_target         NUMBER,
  gmv_cmp_rate       NUMBER,
  gmv_cmp_totalrate  NUMBER,
  time_rate          NUMBER,
  gmv_lastmonth      NUMBER,
  gmv_lastmonth_diff NUMBER,
  gmv_lastmonth_rate NUMBER,
  gmv_premonth       NUMBER,
  gmv_premonth_diff  NUMBER,
  gmv_premonth_rate  NUMBER,
  cost_month         NUMBER,
  income_month       NUMBER,
  profit_rate        NUMBER,
  profit_alert_rate  NUMBER,
  cost_health_flag   VARCHAR2(2),
  create_dt          DATE,
  update_dt          DATE
)
;
comment on table UO_MONTH_KEYPOINT
is '核心指标月度完成情况';
comment on column UO_MONTH_KEYPOINT.month_id
is '月份ID';
comment on column UO_MONTH_KEYPOINT.year_id
is '年ID';
comment on column UO_MONTH_KEYPOINT.gmv_actual
is 'GMV实际值';
comment on column UO_MONTH_KEYPOINT.gmv_target
is 'GMV目标值';
comment on column UO_MONTH_KEYPOINT.gmv_cmp_rate
is 'GMV达标率';
comment on column UO_MONTH_KEYPOINT.gmv_cmp_totalrate
is 'GMV累计达标率';
comment on column UO_MONTH_KEYPOINT.time_rate
is '时间进度';
comment on column UO_MONTH_KEYPOINT.gmv_lastmonth
is '去年同月GMV值';
comment on column UO_MONTH_KEYPOINT.gmv_lastmonth_diff
is '与去年同月GMV值差';
comment on column UO_MONTH_KEYPOINT.gmv_lastmonth_rate
is '与去年同月GMV值相比增长率';
comment on column UO_MONTH_KEYPOINT.gmv_premonth
is '上月GMV值';
comment on column UO_MONTH_KEYPOINT.gmv_premonth_diff
is '与上月GMV差值';
comment on column UO_MONTH_KEYPOINT.gmv_premonth_rate
is '相比上月GMV增长率';
comment on column UO_MONTH_KEYPOINT.cost_month
is '本月成本';
comment on column UO_MONTH_KEYPOINT.income_month
is '本月收入（GMV）';
comment on column UO_MONTH_KEYPOINT.profit_rate
is '利润率';
comment on column UO_MONTH_KEYPOINT.profit_alert_rate
is '利润率预警线';
comment on column UO_MONTH_KEYPOINT.cost_health_flag
is '利润率健康度  Y表示健康 N表示不健康 ';
comment on column UO_MONTH_KEYPOINT.create_dt
is '创建时间';
comment on column UO_MONTH_KEYPOINT.update_dt
is '更新时间';
create unique index UO_MONTH_KEYPOINT_U1 on UO_MONTH_KEYPOINT (MONTH_ID);

prompt
prompt Creating table UO_PROFIT_RATE_SET
prompt =================================
prompt
create table UO_PROFIT_RATE_SET
(
  year_id     NUMBER,
  profit_rate NUMBER
)
;
comment on table UO_PROFIT_RATE_SET
is '利润率预警线设置';
comment on column UO_PROFIT_RATE_SET.year_id
is '年份ID';
comment on column UO_PROFIT_RATE_SET.profit_rate
is '利润率预警线';
create unique index UO_PROFIT_RATE_SET_U1 on UO_PROFIT_RATE_SET (YEAR_ID);

prompt
prompt Creating table UO_REASON_CONCERN
prompt ================================
prompt
create table UO_REASON_CONCERN
(
  reason_id        NUMBER,
  reason_result_id NUMBER,
  create_dt        DATE
)
;
comment on table UO_REASON_CONCERN
is '原因详情-结果跟踪表';
comment on column UO_REASON_CONCERN.reason_id
is '原因ID';
comment on column UO_REASON_CONCERN.reason_result_id
is '原因结果ID';
comment on column UO_REASON_CONCERN.create_dt
is '插入时间';

prompt
prompt Creating table UO_REASON_DETAIL
prompt ===============================
prompt
create table UO_REASON_DETAIL
(
  reason_id         NUMBER,
  dim_code          VARCHAR2(32),
  dim_values        VARCHAR2(2000),
  dim_display_value VARCHAR2(4000)
)
;
comment on table UO_REASON_DETAIL
is '原因探究-已选择维度';
comment on column UO_REASON_DETAIL.reason_id
is '模板ID';
comment on column UO_REASON_DETAIL.dim_code
is '维度编码';
comment on column UO_REASON_DETAIL.dim_values
is '维度值';
comment on column UO_REASON_DETAIL.dim_display_value
is '显示信息';
create unique index UO_REASON_DETAIL_U1 on UO_REASON_DETAIL (REASON_ID, DIM_CODE);

prompt
prompt Creating table UO_REASON_KPIS_SNP
prompt =================================
prompt
create table UO_REASON_KPIS_SNP
(
  reason_id        NUMBER,
  template_code    VARCHAR2(32),
  reason_kpi_code  VARCHAR2(32),
  reason_kpi_name  VARCHAR2(256),
  create_dt        DATE,
  update_dt        DATE,
  template_name    VARCHAR2(32),
  relate_value     NUMBER,
  template_order   NUMBER,
  reason_kpi_order NUMBER
)
;
comment on table UO_REASON_KPIS_SNP
is '原因探究-原因KPI列表';
comment on column UO_REASON_KPIS_SNP.reason_id
is '原因探究ID';
comment on column UO_REASON_KPIS_SNP.template_code
is '模板code';
comment on column UO_REASON_KPIS_SNP.reason_kpi_code
is '原因KPI编码';
comment on column UO_REASON_KPIS_SNP.reason_kpi_name
is '原因KPI名称';
comment on column UO_REASON_KPIS_SNP.create_dt
is '创建时间';
comment on column UO_REASON_KPIS_SNP.update_dt
is '结束时间';
comment on column UO_REASON_KPIS_SNP.template_name
is '模板名称';
comment on column UO_REASON_KPIS_SNP.relate_value
is '相关系数值';
comment on column UO_REASON_KPIS_SNP.template_order
is '模板排序号';
comment on column UO_REASON_KPIS_SNP.reason_kpi_order
is '原因KPI排序号';

prompt
prompt Creating table UO_REASON_LIST
prompt =============================
prompt
create table UO_REASON_LIST
(
  reason_id   NUMBER,
  reason_name VARCHAR2(32),
  status      VARCHAR2(32),
  progress    NUMBER,
  begin_dt    VARCHAR2(32),
  end_dt      VARCHAR2(32),
  create_dt   DATE,
  update_dt   DATE,
  create_by   VARCHAR2(32),
  update_by   VARCHAR2(32),
  kpi_code    VARCHAR2(32),
  period_type VARCHAR2(32),
  source      VARCHAR2(256)
)
;
comment on table UO_REASON_LIST
is '原因探究列表';
comment on column UO_REASON_LIST.reason_id
is '原因ID';
comment on column UO_REASON_LIST.reason_name
is '原因名称';
comment on column UO_REASON_LIST.status
is '状态  A草稿 R计算中 F完成 E错误';
comment on column UO_REASON_LIST.progress
is '进度';
comment on column UO_REASON_LIST.begin_dt
is '开始日期';
comment on column UO_REASON_LIST.end_dt
is '结束日期';
comment on column UO_REASON_LIST.create_dt
is '创建时间';
comment on column UO_REASON_LIST.update_dt
is '结束时间';
comment on column UO_REASON_LIST.create_by
is '创建人';
comment on column UO_REASON_LIST.update_by
is '更新人';
comment on column UO_REASON_LIST.kpi_code
is '选择的KPI编码';
comment on column UO_REASON_LIST.period_type
is '周期粒度 D表示按天 M表示按月';
comment on column UO_REASON_LIST.source
is '来源';
create unique index OU_REASON_LIST_U1 on UO_REASON_LIST (REASON_ID);

prompt
prompt Creating table UO_REASON_REL_MATRIX
prompt ===================================
prompt
create table UO_REASON_REL_MATRIX
(
  reason_id    NUMBER,
  f_code       VARCHAR2(32),
  f_name       VARCHAR2(256),
  rf_code      VARCHAR2(32),
  rf_name      VARCHAR2(256),
  relate_value NUMBER,
  create_dt    DATE,
  f_orderno    NUMBER,
  rf_order_no  NUMBER
)
;
comment on table UO_REASON_REL_MATRIX
is '原因探究因子矩阵表';
comment on column UO_REASON_REL_MATRIX.reason_id
is '诊断ID';
comment on column UO_REASON_REL_MATRIX.f_code
is '因子CODE';
comment on column UO_REASON_REL_MATRIX.f_name
is '因子名称';
comment on column UO_REASON_REL_MATRIX.rf_code
is '相关因子CODE';
comment on column UO_REASON_REL_MATRIX.rf_name
is '相关因子名称';
comment on column UO_REASON_REL_MATRIX.relate_value
is '相关系数';
comment on column UO_REASON_REL_MATRIX.create_dt
is '创建日期';
comment on column UO_REASON_REL_MATRIX.f_orderno
is '因子排序号';
comment on column UO_REASON_REL_MATRIX.rf_order_no
is '相关因子排序号';

prompt
prompt Creating table UO_REASON_RESULT
prompt ===============================
prompt
create table UO_REASON_RESULT
(
  reason_id        NUMBER,
  reason_code      VARCHAR2(512),
  formula_desc     VARCHAR2(512),
  formula          VARCHAR2(512),
  business         VARCHAR2(512),
  create_dt        DATE,
  reason_result_id NUMBER
)
;
comment on table UO_REASON_RESULT
is '原因诊断结果表';
comment on column UO_REASON_RESULT.reason_id
is '原因ID';
comment on column UO_REASON_RESULT.reason_code
is '原因编码列表';
comment on column UO_REASON_RESULT.formula_desc
is '公式变量说明';
comment on column UO_REASON_RESULT.formula
is '公式';
comment on column UO_REASON_RESULT.business
is '业务描述';
comment on column UO_REASON_RESULT.create_dt
is '创建日期';
comment on column UO_REASON_RESULT.reason_result_id
is '主键ID';

prompt
prompt Creating table UO_REASON_TEMPLATE_CONFIG
prompt ========================================
prompt
create table UO_REASON_TEMPLATE_CONFIG
(
  template_code    VARCHAR2(32),
  template_name    VARCHAR2(32),
  reason_kpi_code  VARCHAR2(32),
  reason_kpi_name  VARCHAR2(256),
  reason_kpi_order NUMBER,
  template_order   NUMBER
)
;
comment on table UO_REASON_TEMPLATE_CONFIG
is '原因探究-模板及其对应指标配置信息';
comment on column UO_REASON_TEMPLATE_CONFIG.template_code
is '模板CODE';
comment on column UO_REASON_TEMPLATE_CONFIG.template_name
is '模板名称';
comment on column UO_REASON_TEMPLATE_CONFIG.reason_kpi_code
is '模板指标编码';
comment on column UO_REASON_TEMPLATE_CONFIG.reason_kpi_name
is '模板指标名称';
comment on column UO_REASON_TEMPLATE_CONFIG.reason_kpi_order
is '模板指标排序';
comment on column UO_REASON_TEMPLATE_CONFIG.template_order
is '模板排序';
create unique index UO_REASON_TEMPLATE_CONFIG_U1 on UO_REASON_TEMPLATE_CONFIG (TEMPLATE_CODE, REASON_KPI_CODE);

prompt
prompt Creating table UO_REASON_TEMPLATE_POOL
prompt ======================================
prompt
create table UO_REASON_TEMPLATE_POOL
(
  template_code    VARCHAR2(32),
  template_name    VARCHAR2(32),
  reason_kpi_code  VARCHAR2(32),
  reason_kpi_name  VARCHAR2(256),
  reason_kpi_order NUMBER,
  template_order   NUMBER
)
;
comment on table UO_REASON_TEMPLATE_POOL
is '原因探究-模板及其对应指标配置信息';
comment on column UO_REASON_TEMPLATE_POOL.template_code
is '模板CODE';
comment on column UO_REASON_TEMPLATE_POOL.template_name
is '模板名称';
comment on column UO_REASON_TEMPLATE_POOL.reason_kpi_code
is '模板指标编码';
comment on column UO_REASON_TEMPLATE_POOL.reason_kpi_name
is '模板指标名称';
comment on column UO_REASON_TEMPLATE_POOL.reason_kpi_order
is '模板指标排序';
comment on column UO_REASON_TEMPLATE_POOL.template_order
is '模板排序';
create unique index UO_REASON_TEMPLATE_POOL_U1 on UO_REASON_TEMPLATE_POOL (TEMPLATE_CODE, REASON_KPI_CODE);

prompt
prompt Creating table UO_TGT_DIMENSION
prompt ===============================
prompt
create table UO_TGT_DIMENSION
(
  id                 NUMBER,
  tgt_id             NUMBER,
  dimension_code     VARCHAR2(32),
  dimension_name     VARCHAR2(32),
  dimension_val_code VARCHAR2(32),
  dimension_val_name VARCHAR2(32)
)
;
comment on table UO_TGT_DIMENSION
is '目标维度表';
comment on column UO_TGT_DIMENSION.id
is 'ID';
comment on column UO_TGT_DIMENSION.tgt_id
is '目标D';
comment on column UO_TGT_DIMENSION.dimension_code
is '维度编码';
comment on column UO_TGT_DIMENSION.dimension_name
is '维度名称';
comment on column UO_TGT_DIMENSION.dimension_val_code
is '维度值编码';
comment on column UO_TGT_DIMENSION.dimension_val_name
is '维度值名称';

prompt
prompt Creating table UO_TGT_DISMANT
prompt =============================
prompt
create table UO_TGT_DISMANT
(
  id              NUMBER,
  period_type     VARCHAR2(32),
  period_date     VARCHAR2(32),
  actual_val      NUMBER default 0,
  compute_dt      DATE,
  tgt_id          NUMBER,
  tgt_val         NUMBER,
  tgt_percent     NUMBER,
  tgt_weight_idx  NUMBER,
  actual_val_last NUMBER default 0,
  growth_rate     NUMBER,
  past_flag       VARCHAR2(2) default 'N',
  finish_flag     VARCHAR2(2) default 'N'
)
;
comment on table UO_TGT_DISMANT
is '目标拆解表';
comment on column UO_TGT_DISMANT.id
is 'ID';
comment on column UO_TGT_DISMANT.period_type
is '目标周期';
comment on column UO_TGT_DISMANT.period_date
is '具体周期日期';
comment on column UO_TGT_DISMANT.actual_val
is '实际值';
comment on column UO_TGT_DISMANT.compute_dt
is '计算日期';
comment on column UO_TGT_DISMANT.tgt_id
is '目标ID';
comment on column UO_TGT_DISMANT.tgt_val
is '目标值';
comment on column UO_TGT_DISMANT.tgt_percent
is '目标占比';
comment on column UO_TGT_DISMANT.tgt_weight_idx
is '目标权重指数';
comment on column UO_TGT_DISMANT.actual_val_last
is '去年指标值';
comment on column UO_TGT_DISMANT.growth_rate
is '环比增长率';
comment on column UO_TGT_DISMANT.past_flag
is '是否已经过去 Y表示是 N表示否 默认为否';
comment on column UO_TGT_DISMANT.finish_flag
is '是否完成 Y表示完成 N表示未完成 此字段需要搭配PAST_FLAG 使用 ';

prompt
prompt Creating table UO_TGT_LIST
prompt ==========================
prompt
create table UO_TGT_LIST
(
  id                 NUMBER,
  name               VARCHAR2(64),
  period_type        VARCHAR2(8),
  start_dt           VARCHAR2(16),
  end_dt             VARCHAR2(16),
  kpi_code           VARCHAR2(32),
  create_dt          DATE,
  update_dt          DATE,
  create_by          VARCHAR2(32),
  update_by          VARCHAR2(32),
  status             VARCHAR2(4),
  target_val         NUMBER,
  compute_dt         DATE,
  actual_val         NUMBER,
  actual_val_rate    NUMBER,
  actual_val_last    NUMBER,
  finish_rate        NUMBER,
  finish_rate_differ NUMBER,
  finish_rate_last   NUMBER,
  kpi_unit           VARCHAR2(4),
  remain_tgt         NUMBER,
  remain_count       NUMBER,
  remain_list        VARCHAR2(256),
  vary_idx           NUMBER,
  vary_idx_last      NUMBER,
  finish_differ      NUMBER
)
;
comment on table UO_TGT_LIST
is '设定目标表';
comment on column UO_TGT_LIST.id
is '目标ID';
comment on column UO_TGT_LIST.name
is '目标名称';
comment on column UO_TGT_LIST.period_type
is '周期类型';
comment on column UO_TGT_LIST.start_dt
is '开始时间';
comment on column UO_TGT_LIST.end_dt
is '结束时间';
comment on column UO_TGT_LIST.kpi_code
is '指标编码';
comment on column UO_TGT_LIST.create_dt
is '创建时间';
comment on column UO_TGT_LIST.update_dt
is '修改时间';
comment on column UO_TGT_LIST.create_by
is '创建人';
comment on column UO_TGT_LIST.update_by
is '修改人';
comment on column UO_TGT_LIST.status
is '状态';
comment on column UO_TGT_LIST.target_val
is '目标值';
comment on column UO_TGT_LIST.compute_dt
is '计算日期';
comment on column UO_TGT_LIST.actual_val
is '实际值';
comment on column UO_TGT_LIST.actual_val_rate
is '实际值去年同比';
comment on column UO_TGT_LIST.actual_val_last
is '实际值去年同期';
comment on column UO_TGT_LIST.finish_rate
is '完成率';
comment on column UO_TGT_LIST.finish_rate_differ
is '完成率差';
comment on column UO_TGT_LIST.finish_rate_last
is '完成率去年同期';
comment on column UO_TGT_LIST.kpi_unit
is '指标度量单位';
comment on column UO_TGT_LIST.remain_tgt
is '待完成业绩（总目标-已完成）';
comment on column UO_TGT_LIST.remain_count
is '未完成的周期目标个数';
comment on column UO_TGT_LIST.remain_list
is '剩余目标的日期，多值以逗号隔开 (无用，待删除)';
comment on column UO_TGT_LIST.vary_idx
is '本年迄今为止的变异系数';
comment on column UO_TGT_LIST.vary_idx_last
is '去年同期变异系数';
comment on column UO_TGT_LIST.finish_differ
is '业绩缺口';

prompt
prompt Creating table UO_WEIGHT_INDEX_CONFIG
prompt =====================================
prompt
create table UO_WEIGHT_INDEX_CONFIG
(
  period_id   NVARCHAR2(32),
  index_value NUMBER,
  index_type  VARCHAR2(32),
  period_type VARCHAR2(32)
)
;
comment on table UO_WEIGHT_INDEX_CONFIG
is '权重指数';
comment on column UO_WEIGHT_INDEX_CONFIG.period_id
is '周期ID 如果为MM格式 表示月的权重指数XX值为01-12 如果为X格式，表示天的权重指数 X值为1-7';
comment on column UO_WEIGHT_INDEX_CONFIG.index_value
is '权重指数值';
comment on column UO_WEIGHT_INDEX_CONFIG.index_type
is '权重指数类型 GMV表示GMV的权重指数';
comment on column UO_WEIGHT_INDEX_CONFIG.period_type
is '周期类型 M表示月权重指数 D表示天权重指数';
create unique index UO_WI_CONFIG_U1 on UO_WEIGHT_INDEX_CONFIG (PERIOD_ID, PERIOD_TYPE, INDEX_TYPE);

prompt
prompt Creating table UO_YEAR_HISTORY
prompt ==============================
prompt
create table UO_YEAR_HISTORY
(
  year_id      NUMBER,
  gmv_value    NUMBER,
  gmv_rate     NUMBER,
  cur_year_cnt NUMBER,
  year_fqy     NUMBER,
  year_joint   NUMBER,
  year_mpp     NUMBER,
  year_opp     NUMBER,
  year_cpp     NUMBER,
  create_dt    DATE,
  update_dt    DATE
)
;
comment on table UO_YEAR_HISTORY
is '年度快照';
comment on column UO_YEAR_HISTORY.year_id
is '年';
comment on column UO_YEAR_HISTORY.gmv_value
is 'GMV值';
comment on column UO_YEAR_HISTORY.gmv_rate
is 'GMV相比上年增长率';

prompt
prompt Creating table UO_YEAR_KEYPOINT
prompt ===============================
prompt
create table UO_YEAR_KEYPOINT
(
  year_id           NUMBER,
  gmv_target        NUMBER,
  gmv_actual        NUMBER,
  gmv_cmp_rate      NUMBER,
  gmv_cmp_totalrate NUMBER,
  time_rate         NUMBER,
  gmv_preyear       NUMBER,
  gmv_preyear_diff  NUMBER,
  gmv_preyear_rate  NUMBER,
  cost_year         NUMBER,
  income_year       NUMBER,
  profit_rate       NUMBER,
  profit_alert_rate NUMBER,
  cost_health_flag  VARCHAR2(2),
  create_dt         DATE,
  update_dt         DATE,
  volatility        NUMBER,
  reach_num         NUMBER,
  unreach_num       NUMBER,
  ongoing_num       NUMBER,
  nostart_num       NUMBER
)
;
comment on table UO_YEAR_KEYPOINT
is '核心指标年度完成情况';
comment on column UO_YEAR_KEYPOINT.year_id
is '年份ID';
comment on column UO_YEAR_KEYPOINT.gmv_target
is 'GMV目标值';
comment on column UO_YEAR_KEYPOINT.gmv_actual
is 'GMV实际值';
comment on column UO_YEAR_KEYPOINT.gmv_cmp_rate
is 'GMV达标率';
comment on column UO_YEAR_KEYPOINT.gmv_cmp_totalrate
is 'GMV累计达标率';
comment on column UO_YEAR_KEYPOINT.time_rate
is '时间进度';
comment on column UO_YEAR_KEYPOINT.gmv_preyear
is '上一年GMV值';
comment on column UO_YEAR_KEYPOINT.gmv_preyear_diff
is '与上一年GMV值差值';
comment on column UO_YEAR_KEYPOINT.gmv_preyear_rate
is '与上一年GMV增长比';
comment on column UO_YEAR_KEYPOINT.cost_year
is '本年成本';
comment on column UO_YEAR_KEYPOINT.income_year
is '本年收入(GMV)';
comment on column UO_YEAR_KEYPOINT.profit_rate
is '利润率      (收入-成本）/成本';
comment on column UO_YEAR_KEYPOINT.profit_alert_rate
is '利润率预警值';
comment on column UO_YEAR_KEYPOINT.cost_health_flag
is '利润率健康度  Y表示健康 N表示不健康 ';
comment on column UO_YEAR_KEYPOINT.create_dt
is '创建日期';
comment on column UO_YEAR_KEYPOINT.update_dt
is '更新日期';
comment on column UO_YEAR_KEYPOINT.volatility
is '本年各月GMV的波动率';
comment on column UO_YEAR_KEYPOINT.reach_num
is '本年达标月份数量';
comment on column UO_YEAR_KEYPOINT.unreach_num
is '本年未达标月份数量';
comment on column UO_YEAR_KEYPOINT.ongoing_num
is '本年进行中月份数量';
comment on column UO_YEAR_KEYPOINT.nostart_num
is '本年未开始月份数量';
create unique index UO_YEAR_KEYPOINT_U1 on UO_YEAR_KEYPOINT (YEAR_ID);


spool off
