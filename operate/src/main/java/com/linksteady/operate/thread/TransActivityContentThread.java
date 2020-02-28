package com.linksteady.operate.thread;

import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.service.impl.ActivityDetailServiceImpl;
import com.linksteady.operate.service.impl.ActivityPlanServiceImpl;
import com.linksteady.operate.util.SpringContextUtils;
import com.linksteady.operate.vo.ActivityContentVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class TransActivityContentThread  implements Callable {

    int start;
    int end;

    String headId;
    String planDtWid;
    Map<String,String> templateMap;

    public TransActivityContentThread(String planDtWid,String headId, int start, int end,Map<String,String> templateMap) {
        this.planDtWid=planDtWid;
        this.headId = headId;
        this.start = start;
        this.end = end;
        this.templateMap=templateMap;
    }

    @Override
    public List<ActivityContentVO> call(){

        List<ActivityDetail> list = null;
        try {
            ActivityDetailServiceImpl activityDetailService=(ActivityDetailServiceImpl) SpringContextUtils.getBean("activityDetailServiceImpl");
            ActivityPlanServiceImpl activityPlanService = (ActivityPlanServiceImpl) SpringContextUtils.getBean("activityPlanServiceImpl");

            list = activityDetailService.getPageList(start, end,headId,planDtWid,"-1");

            //转换文案
            List<ActivityContentVO> targetList = activityPlanService.processVariable(list,templateMap);
            log.info("{}的第{}-{}调记录处理完成",headId,start,end);
            return targetList;
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换活动运营文案报错{}", e);
           return null;
        }
    }
}
