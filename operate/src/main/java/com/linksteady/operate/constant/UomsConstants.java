package com.linksteady.operate.constant;

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
    public static final String[] D_MONTH_COLS = {"month_id", "total_user", "month1", "month2", "month3", "month4", "month5", "month6", "month7", "month8", "month9", "month10", "month11", "month12"};

    /**
     * 同期群 - 指标列
     */
    public static final String[] D_MONTH_KPI_COLS = {"month_id", "total_user", "current_month", "month1", "month2", "month3", "month4", "month5", "month6", "month7", "month8", "month9", "month10", "month11", "month12"};

    /**
     * 同期群 - 间隔月并集列
     */
    public static final String[] D_MONTH_COMMONS_COLS = {"month_id", "total_user", "current_month"};

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

}
