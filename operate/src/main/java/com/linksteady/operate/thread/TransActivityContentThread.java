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

    int start;
    int end;

    Long planId;
    Map<String,String> templateMap;
    Map<String,Double> prodPriceMap;

    public TransActivityContentThread(Long planId, int start, int end,Map<String,String> templateMap,Map<String,Double> prodPriceMap) {
        this.planId=planId;
        this.start = start;
        this.end = end;
        this.templateMap=templateMap;
        this.prodPriceMap=prodPriceMap;
    }

    @Override
    public List<ActivityContentVO> call(){

        List<ActivityDetail> list = null;
        try {
            ActivityPushMapper activityPushMapper= (ActivityPushMapper) SpringContextUtils.getBean("activityPushMapper");
            ActivityPushServiceImpl activityPushService = (ActivityPushServiceImpl) SpringContextUtils.getBean("activityPushServiceImpl");

            list = activityPushMapper.getPushList(end - start + 1,start-1,planId);

            //转换文案
            List<ActivityContentVO> targetList = activityPushService.processVariable(list,templateMap,prodPriceMap);
            log.info("{}的第{}-{}调记录处理完成",planId,start, end);
            return targetList;
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换活动运营文案报错{}", e);
           return null;
        }
    }
}
