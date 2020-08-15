package com.linksteady.operate.thread;

import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.service.impl.DailyDetailServiceImpl;
import com.linksteady.operate.service.impl.QywxDailyDetailServiceImpl;
import com.linksteady.operate.vo.GroupCouponVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 根据模板填充变量，生成实际文案的多线程类
 */
@Slf4j
public class TransQywxDailyContentThread implements Callable {
    int offset;
    int limit;
    Long headerId;
    Map<String,List<GroupCouponVO>> groupCouponList;

    public TransQywxDailyContentThread(Long headerId, int limit,int offset,  Map<String,List<GroupCouponVO>> groupCouponList) {
        this.headerId = headerId;
        this.limit = limit;
        this.offset = offset;
        this.groupCouponList=groupCouponList;
    }

    @Override
    public  List<QywxDailyDetail> call(){
        List<QywxDailyDetail> list = null;
        try {
            QywxDailyDetailServiceImpl qywxDailyDetailService = (QywxDailyDetailServiceImpl) SpringContextUtils.getBean("qywxDailyDetailServiceImpl");
            list = qywxDailyDetailService.getUserList(headerId,  limit, offset);
            //转换文案
            List<QywxDailyDetail> targetList = qywxDailyDetailService.transContent(list,groupCouponList);
            log.info("{}的从{}开始的{}条记录处理完成",headerId,limit,offset);
            return targetList;
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换每日运营[企业微信]文案报错{}", e);
           return null;
        }
    }
}
