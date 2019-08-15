package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import com.linksteady.operate.service.DailyPushService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransPushContentThread;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Service
@Slf4j
public class DailyPushServiceImpl implements DailyPushService {

    @Autowired
    private DailyPushMapper dailyPushMapper;

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    DailyProperties dailyProperties;

    @Autowired
    PushMessageServiceImpl pushMessageService;

    @Autowired
    PushAliSmsServiceImpl pushAliSmsService;

    @Autowired
    PushWxMessageServiceImpl pushWxMessageService;

    /**
     * 生成推送名单列表
     * @param headerId
     */
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void generatePushList(String headerId) {
        //根据headerID获取当前有多少人需要推送
        int pushUserCount= dailyPushMapper.getUserCount(headerId);

        int pageSize=100;
          //判断如果条数大于100 则进行分页
        if(pushUserCount<=pageSize)
        {
            List<DailyPushQuery> list= getUserList(headerId,1,pushUserCount);
            //填充模板 生成文案
            List<DailyPushInfo> targetList=transPushList(list);
            //保存要推送的文案
            updatePushContent(targetList);

            //更新当前任务状态为doing
            dailyMapper.updateStatus(headerId,"doing");
        }else
        {
            ExecutorService pool = null;
            try {
                //生成线程池 (线程数量为4)
                pool = new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());;

                //分页多线程处理

                int page=pushUserCount%pageSize==0?pushUserCount/pageSize:(pushUserCount/pageSize+1);

                CountDownLatch latch = new CountDownLatch(page);
                List taskList=Lists.newArrayList();
                //生成线程对象列表
                for(int i=0;i<page;i++)
                {
                    taskList.add(new TransPushContentThread(headerId,i*pageSize+1,(i+1)*pageSize,latch));
                }

                //放入线程池中
                pool.invokeAll(taskList);

                //等待执行结束
                latch.await();
            } catch (Exception e) {
                //错误日志上报

                e.printStackTrace();
            }finally {
                pool.shutdown();
            }

            //更新当前任务状态为doing
            dailyMapper.updateStatus(headerId,"doing");

        }
    }

    /**
     * 根据查询到的用户及其推荐信息，结合模板，生成用户的最终推送信息列表
     * @param list
     * @return
     */
    @SneakyThrows
    public List<DailyPushInfo> transPushList(List<DailyPushQuery> list)
    {
        int  hanleCoupon=0;

        List<DailyPushInfo> targetList= Lists.newArrayList();
        DailyPushInfo dailyPushInfo=null;

        for(DailyPushQuery dailyPushQuery:list)
        {
            dailyPushInfo=new DailyPushInfo();
            //文案内容
            String  smsContent=dailyPushQuery.getSmsContent();
            String longUrl="";

            //含券
            if(null!=dailyPushQuery.getCouponId()&&dailyPushQuery.getCouponId()!=-1L)
            {
                //券需要用户自行领用
                if(hanleCoupon==0)
                {
                    longUrl=dailyPushQuery.getCouponUrl();
                    String shortUrl=shortUrlService.produceShortUrl("1",dailyPushQuery.getUserId(),longUrl);
                    smsContent=smsContent.replace("{CONPON_URL}",shortUrl);
                    smsContent=smsContent.replace("{CONPON_NAME}",dailyPushQuery.getCouponName());

                    smsContent=smsContent.replace("{PROD}",dailyPushQuery.getRecLastName());
                    smsContent=smsContent.replace("{PROD_URL}",shortUrl);
                }
            }else
            {
                longUrl=dailyPushQuery.getRecLastLongurl();
                String shortUrl=shortUrlService.produceShortUrl("1",dailyPushQuery.getUserId(),longUrl);
                smsContent=smsContent.replace("{PROD}",dailyPushQuery.getRecLastName());
                smsContent=smsContent.replace("{PROD_URL}",shortUrl);
            }

            dailyPushInfo.setDailyDetailId(dailyPushQuery.getDailyDetailId());
            dailyPushInfo.setSmsContent(smsContent);

            targetList.add(dailyPushInfo);
        }

        return targetList;
    }

    /**
     * 分页获取选中的用户名单
     * @param headerId
     * @param start
     * @param end
     * @return
     */
    public List<DailyPushQuery>  getUserList(String headerId,int start,int end){
         return dailyPushMapper.getUserList(headerId,start,end);
    }

    /**
     * 保存文案信息
     * @param targetList
     */
    @Override
    public void updatePushContent( List<DailyPushInfo> targetList)
    {
         dailyPushMapper.updatePushContent(targetList);
    }

    /**
     * 获得当前要推送的最大的daily_detail_id
     * @return
     */
    @Override
    public int getPrePushUserMaxId(){
        return dailyPushMapper.getPrePushUserMaxId();
    }

    /**
     * 获取(当前时间节点)待推送的消息数量
     * @return
     */
    @Override
    public int  getPrePushUserCount(int dailyDetailId) {
        return dailyPushMapper.getPrePushUserCount(dailyDetailId);
    }

    /**
     * 获取(当前时间节点)待推送的消息列表
     * @return
     */
    @Override
    public List<DailyPushInfo>  getPrePushUserList(int dailyDetailId,int start,int end) {
        return dailyPushMapper.getPrePushUserList(dailyDetailId,start,end);
    }

    /**
     * 更新每条记录的推送状态
     * @param list
     * @param status
     */
    @Override
    public void updateSendStatus(List<DailyPushInfo> list,String status){
        dailyPushMapper.updateSendStatus(list,status);
    }

    /**
     * 更新日运营状态为 完成推送，统计效果中
     */
    @Override
    public void updateHeaderToDone() {
        dailyPushMapper.updateHeaderToDone();
    }

    /**
     * 更新头表上的 推送统计数据
     */
    @Override
    public void updateHeaderSendStatis() {
        dailyPushMapper.updateHeaderSendStatis();
    }

    /**
     * 更新统计表中的触达信息
     */
    @Override
    public void updatePushStatInfo(){
        dailyPushMapper.updatePushStatInfo();
    }

    @Override
    public void push(List<DailyPushInfo> list) {

        log.info("当前选择的触达方式为{}，本批次触达人数:{}",dailyProperties.getPushType(),list.size());
        if("SMS".equals(dailyProperties.getPushType()))
        {
            //短信触达
            pushAliSmsService.push(list);
        }else if("WX".equals(dailyProperties.getPushType()))
        {
            //微信消息
            pushWxMessageService.push(list);
        }else if("NONE".equals(dailyProperties.getPushType()))
        {
            //测试，打印
            pushMessageService.push(list);
        }
    }

}

