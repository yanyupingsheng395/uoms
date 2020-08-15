package com.linksteady.operate.thread;

import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.operate.dao.ActivityPushMapper;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.service.impl.ActivityPushServiceImpl;
import com.linksteady.operate.vo.ActivityContentVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class TransActivityContentThread  implements Callable {

    int limit;
    int offset;
    Long planId;
    Map<String,String> templateMap;

    public TransActivityContentThread(Long planId, int limit, int offset,Map<String,String> templateMap) {
        this.planId=planId;
        this.limit = limit;
        this.offset = offset;
        this.templateMap=templateMap;
    }

    @Override
    public List<ActivityContentVO> call(){

        List<ActivityDetail> list = null;
        try {
            ActivityPushMapper activityPushMapper= (ActivityPushMapper) SpringContextUtils.getBean("activityPushMapper");
            ActivityPushServiceImpl activityPushService = (ActivityPushServiceImpl) SpringContextUtils.getBean("activityPushServiceImpl");

            list = activityPushMapper.getPushList(limit,offset,planId);

            //转换文案
            List<ActivityContentVO> targetList = activityPushService.processVariable(list,templateMap);
            log.info("{}的从{}开始的{}条记录处理完成",planId,limit,offset);
            return targetList;
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换活动运营文案报错{}", e);
           return null;
        }
    }
}
