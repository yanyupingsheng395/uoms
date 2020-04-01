package com.linksteady.operate.domain.enums;

/**
 * @author huang
 * 活动运营执行计划状态的枚举类
 */
public enum ActivityPlanStatusEnum {

    /**
     * 活动运营执行计划 尚未进行计算
     */
    NOT_CALCUATE("0","尚未计算"),
    /**
     * 活动运营执行计划已经计算完成，待进行推送
     */
    WAIT_EXEC("1","待执行"),

    /**
     * 执行中
     */
    EXEC("2","执行中"),

    /**
     * 已执行完
     */
    EXEC_FINISH("3","执行完"),

    /**
     * 过期未执行
     */
    CANCEL("4","未执行已过期"),

    /**
     * 终止
     */
    STOP("5","终止");

        private String statusCode;

        private String statusDesc;

    ActivityPlanStatusEnum(String statusCode, String statusDesc) {
            this.statusCode = statusCode;
            this.statusDesc = statusDesc;
        }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    // 普通方法
    public static String getPlanStatusDesc(String planStatusCode) {
        for (ActivityPlanStatusEnum c : ActivityPlanStatusEnum.values()) {
            if (c.getStatusCode().equals(planStatusCode)) {
                return c.getStatusDesc();
            }
        }
        return "";
    }
}
