package com.linksteady.operate.util;

import java.util.Arrays;
import java.util.List;

/**
 * UOMS下定义的一些常量
 * @author huang
 */
public final class UomsConstants {
    /**
     * ********************* 同期群常量 *********************
     */

    /**
     * 间隔月
     */
    public static final String PERIOD_TYPE_INTERVAL_MONTH = "dmonth";

    /**
     * 自然月
     */
    public static final String PERIOD_TYPE_NOMAL_MONTH = "month";

    /**
     * 同期群 - 留存列
     */
    public static final String[] D_MONTH_COLS = {"MONTH_ID", "TOTAL_USER", "MONTH1", "MONTH2", "MONTH3", "MONTH4", "MONTH5", "MONTH6", "MONTH7", "MONTH8", "MONTH9", "MONTH10", "MONTH11", "MONTH12"};

    /**
     * 同期群 - 指标列
     */
    public static final String[] D_MONTH_KPI_COLS = {"MONTH_ID", "TOTAL_USER", "CURRENT_MONTH", "MONTH1", "MONTH2", "MONTH3", "MONTH4", "MONTH5", "MONTH6", "MONTH7", "MONTH8", "MONTH9", "MONTH10", "MONTH11", "MONTH12"};

    /**
     * ********************* 同期群常量 *********************
     */

    /**
     * *********************用户运营监控的常量******************
     */
    public static final String OP_DATA_GMV = "gmv";

    /**
     * 用户数
     */
    public static final String OP_DATA_USER_CNT = "userCnt";

    /**
     * 客单价
     */
    public static final String OP_DATA_USER_PRICE = "userPrice";

    /**
     * 订单数
     */
    public static final String OP_DATA_ORDER_CNT = "orderCnt";

    /**
     * 订单价
     */
    public static final String OP_DATA_ORDER_PRICE = "orderPrice";

    /**
     *连带率
     */
    public static final String OP_DATA_ORDER_JOINRATE= "jointRate";

    /**
     * 件单价
     */
    public static final String OP_DATA_SPRICE = "unitPrice";

    /**
     * *********************用户运营监控的常量******************
     */

    /**
     * 周期类型-月
     */
    public static final String PERIOD_TYPE_MONTH="M";

    /**
     * 周期类型-年
     */
    public static final String PERIOD_TYPE_YEAR="Y";

    /**
     * 周期类型-日
     */
    public static final String PERIOD_TYPE_DAY="D";

    /**
     * 乘法
     */
    public static final String DIAG_OPERATION_TYPE_MULTI="M";

    /**
     * 加法
     */
    public static final String DIAG_OPERATION_TYPE_ADD="A";

    /**
     * 过滤
     */
    public static final String DIAG_OPERATION_TYPE_FILTER="F";

    public static final String DIAG_KPI_CODE_TSPAN="tspan";

    /**
     * 可被用于目标的指标
     */
    public static final List<String> TARGET_KPI_LIST= Arrays.asList("gmv");

    /**
     * 可被用于指标拆解的指标
     */
    public static final List<String> DIAG_KPI_LIST= Arrays.asList("gmv");

    /**
     *可被用于目标的维度
     */
    public static final List<String> TARGET_DIM_LIST= Arrays.asList("neworold","source");

    /**
     *可被用于指标拆解的维度
     */
    public static final List<String> DIAG_DIM_LIST= Arrays.asList("neworold","source");
}
