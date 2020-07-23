package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityGroup;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityUserGroupMapper {

    int getCount(Long headId, String stage);

    List<ActivityGroup> getUserGroupList(Long headId, String stage);

    void updateGroupTemplate(Long headId, Long groupId, Long code, String stage);

    void saveGroupData(List<ActivityGroup> dataList);

    void deleteData(Long headId);

    /**
     * 验证当前活动（某个阶段，类型） 活动商品对应的活动类型是否都配置了文案
     * @param headId
     * @param stage
     * @param type
     * @return
     */
    int validGroupTemplateWithGroup(Long headId, String stage, String type);
}
