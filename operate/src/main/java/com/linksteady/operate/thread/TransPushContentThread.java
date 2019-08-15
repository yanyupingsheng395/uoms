package com.linksteady.operate.thread;

import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import com.linksteady.operate.service.impl.DailyPushServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 根据模板填充变量，生成实际文案的多线程类
 */
@Slf4j
public class TransPushContentThread  implements Callable {
    int start;
    int end;
    CountDownLatch latch;

    String headerId;

    public TransPushContentThread(String headerId,int start,int end,CountDownLatch latch)
    {
        this.headerId=headerId;
        this.start=start;
        this.end=end;
        this.latch=latch;
    }

    @Override
    public Integer call() {
        List<DailyPushQuery> list= null;
        try {
            DailyPushServiceImpl dailyPushService= (DailyPushServiceImpl) SpringContextUtils.getBean("dailyPushServiceImpl");
            //查询对应的数据，然后进行转换 转换完成后将latch减1
            list = dailyPushService.getUserList(headerId,start,end);

            //转换文案
            List<DailyPushInfo> targetList=dailyPushService.transPushList(list);

            //保存文案
            dailyPushService.updatePushContent(targetList);
        } catch (Exception e) {
            //错误日志上报
            log.error("多线程转换日运营文案报错{}",e);
        }finally {
            latch.countDown();
        }

        return list.size();

    }
}
