package com.linksteady.operate.dao;

import com.linksteady.operate.domain.PushListLarge;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-10-23
 */
public interface PushLargeListMapper {

    /**
     * 获取待推送的名单数
     * @return
     */
    int getPushLargeListCount();

    /**
     * 分页获取待推送的名单
     * @return
     */
    List<PushListLarge> getPushLargeList(int limit, int offset);


    /**
     * 手动导入短信
     * @param headId
     */
    void insertLargeDataByManual(Long headId);

}
