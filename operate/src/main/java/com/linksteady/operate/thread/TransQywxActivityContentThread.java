package com.linksteady.operate.thread;

import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.operate.dao.QywxActivityPushMapper;
import com.linksteady.operate.domain.QywxActivityDetail;
import com.linksteady.operate.service.impl.QywxActivityPushServiceImpl;
import com.linksteady.operate.vo.QywxActivityContentTmp;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class TransQywxActivityContentThread implements Callable {

    int limit;
    int offset;
    Long planId;
    Map<String,String> templateMap;
    Map<String,String> mediaMap;

    public TransQywxActivityContentThread(Long planId, int limit, int offset, Map<String,String> templateMap,Map<String,String> mediaMap) {
        this.planId=planId;
        this.limit = limit;
        this.offset = offset;
        this.templateMap=templateMap;
        this.mediaMap=mediaMap;
    }

    @Override
    public List<QywxActivityContentTmp> call(){

        List<QywxActivityDetail> list = null;
        try {
            QywxActivityPushMapper activityPushMapper= (QywxActivityPushMapper) SpringContextUtils.getBean("qywxActivityPushMapper");
            QywxActivityPushServiceImpl activityPushService = (QywxActivityPushServiceImpl) SpringContextUtils.getBean("qywxActivityPushServiceImpl");

            list = activityPushMapper.getPushList(limit,offset,planId);

            //转换文案
            List<QywxActivityContentTmp> targetList = activityPushService.processVariable(list,templateMap,mediaMap);
            log.info("{}的从{}开始的{}条记录处理完成",planId,limit,offset);
            return targetList;
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换活动运营文案报错{}", e);
           return null;
        }
    }
}
