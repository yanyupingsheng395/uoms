package com.linksteady.operate.thread;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.service.impl.DailyDetailServiceImpl;
import com.linksteady.operate.util.SpringContextUtils;
import com.linksteady.operate.vo.GroupCouponVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 根据模板填充变量，生成实际文案的多线程类
 */
@Slf4j
public class TransDailyContentThread implements Callable {
    int start;
    int end;
    String headerId;
    Map<String,List<GroupCouponVO>> groupCouponList;

    public TransDailyContentThread(String headerId, int start, int end, Map<String,List<GroupCouponVO>> groupCouponList) {
        this.headerId = headerId;
        this.start = start;
        this.end = end;
        this.groupCouponList=groupCouponList;
    }

    @Override
    public  List<DailyDetail> call(){
        List<DailyDetail> list = null;
        try {
            DailyDetailServiceImpl dailyDetailService = (DailyDetailServiceImpl) SpringContextUtils.getBean("dailyDetailServiceImpl");
            list = dailyDetailService.getUserList(headerId,  end - start + 1, start - 1);
            //转换文案
            List<DailyDetail> targetList = dailyDetailService.transPushList(list,groupCouponList);
            log.info("{}的第{}-{}调记录处理完成",headerId,start,end);
            return targetList;
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换日运营文案报错{}", e);
           return null;
        }
    }
}
