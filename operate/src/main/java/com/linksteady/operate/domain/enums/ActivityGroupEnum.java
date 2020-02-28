package com.linksteady.operate.domain.enums;

public enum ActivityGroupEnum {
    GROUP_1("1","用户成长旅程的商品参与本次活动"),GROUP_2("2","用户成长旅程的商品没有参与本次活动"),GROUP_3("3","用户成长旅程的商品没有参与本次活动，但有可能成为活动商品潜在用户");


    private String groupId;

    private String groupName;

    ActivityGroupEnum(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public static String getGroupNameById(String groupId) {
        for (ActivityGroupEnum c : ActivityGroupEnum.values()) {
            if (c.getGroupId().equals(groupId)) {
                return c.getGroupName();
            }
        }
        return "";
    }
}
