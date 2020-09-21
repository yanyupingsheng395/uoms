package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityTemplate;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public interface QywxActivityTemplateMapper {

    void saveTemplate(ActivityTemplate activityTemplate);

    void deleteActivityTemplate(Long code);

    ActivityTemplate getTemplate(Long code);

    void update(ActivityTemplate activityTemplate);

    List<ActivityTemplate> getTemplateTableData(Long headId);

    void setSmsCode(Long groupId, Long tmpCode, Long headId,String type, String currentUserName);

    /**
     * 返回一个文案 在传入的活动阶段之外被引用的次数
     * @param templateCode
     * @param headId
     * @param stage
     * @return
     */
    int checkTemplateUsed(Long templateCode, Long headId, String type);

    /**
     * 对当前活动 stage、type上设置的文案进行校验
     * @param headId
     * @param stage
     * @param type
     */
    void validUserGroup(Long headId, String type);

    void removeSmsSelected(String type, Long headId, Long groupId, String currentUserName);

    /**
     * 判断活动文案是否发生改变
     */
    int templateContentChanged(Long templateCode, String content);
}
