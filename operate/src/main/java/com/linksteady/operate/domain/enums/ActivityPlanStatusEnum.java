package com.linksteady.operate.domain.enums;

public enum ActivityPlanStatusEnum {

        NOT_CALCUATE("0","尚未计算"),
        WAIT_EXEC("1","待执行"),
        EXEC("2","执行中"),
        EXEC_FINISH("3","执行完"),
        CANCEL("4","未执行已过期"),
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
