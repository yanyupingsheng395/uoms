package com.linksteady.operate.util;

import java.util.Arrays;
import java.util.List;

/**
 * UOMS下定义的一些常量
 * @author huang
 */
public final class UomsConstants {

    //周期类型-月
    public static final String PERIOD_TYPE_MONTH="M";

     //周期类型-年
    public static final String PERIOD_TYPE_YEAR="Y";

    //周期类型-日
    public static final String PERIOD_TYPE_DAY="D";

    //乘法
    public static final String DIAG_OPERATION_TYPE_MULTI="M";

    //加法
    public static final String DIAG_OPERATION_TYPE_ADD="A";

    //过滤
    public static final String DIAG_OPERATION_TYPE_FILTER="F";

    public static final String DIAG_KPI_CODE_GMV="gmv";

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


    private UomsConstants()
    {

    }

}
