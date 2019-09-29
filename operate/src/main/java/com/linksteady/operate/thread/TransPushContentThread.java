package com.linksteady.operate.thread;

import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.service.impl.DailyDetailServiceImpl;
import com.linksteady.operate.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 根据模板填充变量，生成实际文案的多线程类
 */
@Slf4j
public class TransPushContentThread implements Callable {
    int start;
    int end;
    CountDownLatch latch;

    String headerId;

    public TransPushContentThread(String headerId, int start, int end, CountDownLatch latch) {
        this.headerId = headerId;
        this.start = start;
        this.end = end;
        this.latch = latch;
    }

    @Override
    public Integer call() {
        List<DailyDetail> list = null;
        try {
            DailyDetailServiceImpl dailyDetailService = (DailyDetailServiceImpl) SpringContextUtils.getBean("dailyDetailServiceImpl");
            //查询对应的数据，然后进行转换 转换完成后将latch减1
            list = dailyDetailService.getUserList(headerId, start, end);

            //转换文案
            List<DailyDetail> targetList = dailyDetailService.transPushList(list);

            //保存文案
            dailyDetailService.updatePushContent(targetList);

            log.info("{}的第{}-{}调记录处理完成",headerId,start,end);
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换日运营文案报错{}", e);
        } finally {
            latch.countDown();
        }
        return list.size();
    }
}
