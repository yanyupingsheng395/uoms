package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.DailyDetailMapper;
import com.linksteady.operate.domain.DailyDetail;
import com.linksteady.operate.service.DailyDetailService;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.thread.TransPushContentThread;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 群组用户
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class DailyDetailServiceImpl implements DailyDetailService {

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Autowired
    ShortUrlService shortUrlService;

    /**
     * 每日运营用户列表分页
     * @param start
     * @param end
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    @Override
    public List<DailyDetail> getPageList(int start, int end, String headId, String userValue, String pathActive) {
        return dailyDetailMapper.getPageList(start, end, headId, userValue, pathActive);
    }

    /**
     * 每日运营明细记录数
     * @param headId
     * @param userValue
     * @param pathActive
     * @return
     */
    @Override
    public int getDataCount(String headId, String userValue, String pathActive) {
        return dailyDetailMapper.getDataCount(headId, userValue, pathActive);
    }

    /**
     * 根据选择的状态拼接SQL where条件
     * @param userValue
     * @param pathActive
     * @return
     */
    private String getWhereInfo(String userValue, String pathActive, String isConvert) {
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isNotEmpty(isConvert)) {
            sb.append(" and is_conversion = '" + isConvert + "'");
        }
        if(StringUtils.isNotEmpty(userValue)) {
            sb.append(" and user_value = '" + userValue + "'");
        }
        if(StringUtils.isNotEmpty(pathActive)) {
            sb.append(" and path_active = '" + pathActive + "'");
        }
        return sb.toString();
    }


    /**
     * 策略列表用户数
     * @param start
     * @param end
     * @param headId
     * @return
     */
    @Override
    public List<DailyDetail> getStrategyPageList(int start, int end, String headId) {
        return dailyDetailMapper.getStrategyPageList(start, end, headId);
    }

    /**
     * 策略列表记录数
     * @param headId
     * @return
     */
    @Override
    public int getStrategyCount(String headId) {
        return dailyDetailMapper.getStrategyCount(headId);
    }

    /**
     * 效果评估-个体效果分页
     * @param headId
     * @param start
     * @param end
     * @param userValue
     * @param pathActive
     * @param status
     * @return
     */
    @Override
    public List<DailyDetail> getUserEffect(String headId, int start, int end, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        List<DailyDetail> dataList = dailyDetailMapper.getUserEffect(headId, start, end, whereInfo);
        return dataList;
    }

    /**
     * 效果评估记录数
     * @param headId
     * @param userValue
     * @param pathActive
     * @param status
     * @return
     */
    @Override
    public int getDataListCount(String headId, String userValue, String pathActive, String status) {
        String whereInfo = getWhereInfo(userValue, pathActive, status);
        return dailyDetailMapper.getDataListCount(headId, whereInfo);
    }

    /**
     * 获取headId对应的短信内容列表
     * @param headId
     * @return
     */
    @Override
    public List<Map<String, Object>> getContentList(String headId) {
        return dailyDetailMapper.getContentList(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePushOrderPeriod(String headId, String pushOrderPeriod) {
        dailyDetailMapper.updatePushOrderPeriod(headId, pushOrderPeriod);
    }

    @Override
    public void copyToPushList(String headId) {
        dailyDetailMapper.copyToPushList(headId);
    }

    /**
     * 生成推送名单列表
     * @param headerId
     */
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void generatePushList(String headerId) {
        //根据headerID获取当前有多少人需要推送
        int pushUserCount= dailyDetailMapper.getUserCount(headerId);
        int pageSize=100;
        //判断如果条数大于100 则进行分页
        if(pushUserCount<=pageSize)
        {
            List<DailyDetail> list = getUserList(headerId,1,pushUserCount);
            //填充模板 生成文案
            List<DailyDetail> targetList=transPushList(list);
            //保存要推送的文案
            updatePushContent(targetList);

        }else
        {
            ExecutorService pool = null;
            try {
                //生成线程池 (线程数量为4)
                pool = new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());;

                //分页多线程处理

                int page=pushUserCount%pageSize==0?pushUserCount/pageSize:(pushUserCount/pageSize+1);

                CountDownLatch latch = new CountDownLatch(page);
                List taskList= Lists.newArrayList();
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

        }
    }

    /**
     * 根据查询到的用户及其推荐信息，结合模板，生成用户的最终推送信息列表
     * @param list
     * @return
     */
    @SneakyThrows
    public List<DailyDetail> transPushList(List<DailyDetail> list)
    {
        int  hanleCoupon=0;

        List<DailyDetail> targetList= Lists.newArrayList();
        DailyDetail dailyDetailTemp=null;

        for(DailyDetail dailyDetail1:list)
        {
            dailyDetailTemp=new DailyDetail();
            //文案内容
            String  smsContent=dailyDetail1.getSmsContent();
            String longUrl="";

            //含券
            if(null!=dailyDetail1.getCouponId()&&!"-1".equals(dailyDetail1.getCouponId()))
            {
                //券需要用户自行领用
                if(hanleCoupon==0)
                {
                    longUrl=dailyDetail1.getCouponUrl();
                    String shortUrl=shortUrlService.produceShortUrl("1",dailyDetail1.getUserId(),longUrl);
                    smsContent=smsContent.replace("${CONPON_URL}",shortUrl);
                    smsContent=smsContent.replace("${CONPON_NAME}",dailyDetail1.getCouponName());

                    smsContent=smsContent.replace("${PROD}",dailyDetail1.getRecProdName());
                    smsContent=smsContent.replace("${PROD_URL}",shortUrl);
                }else {
                    System.out.println(1);
                }
            }else
            {
                longUrl=dailyDetail1.getRecProdLongUrl();
                String shortUrl=shortUrlService.produceShortUrl("1",dailyDetail1.getUserId(),longUrl);
                smsContent=smsContent.replace("${PROD}",dailyDetail1.getRecProdName());
                smsContent=smsContent.replace("${PROD_URL}",shortUrl);
            }

            dailyDetailTemp.setDailyDetailId(dailyDetail1.getDailyDetailId());
            dailyDetailTemp.setSmsContent(smsContent);

            targetList.add(dailyDetailTemp);
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
    public List<DailyDetail>  getUserList(String headerId,int start,int end){
        return dailyDetailMapper.getUserList(headerId,start,end);
    }

    /**
     * 保存文案信息
     * @param targetList
     */
    @Override
    public void updatePushContent( List<DailyDetail> targetList)
    {
        dailyDetailMapper.updatePushContent(targetList);
    }
}
