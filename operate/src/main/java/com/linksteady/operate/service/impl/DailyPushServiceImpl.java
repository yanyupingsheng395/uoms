package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.linksteady.operate.common.util.SpringContextUtils;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import com.linksteady.operate.service.DailyPushService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
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

    private OkHttpClient okHttpClient = buildOkHttpClient();

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    /**
     * 生成推送名单列表
     * @param headerId
     */
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void generatePushList(String headerId) {
        //根据headerID获取当前有多少人需要推送
        int pushUserCount= dailyPushMapper.getPushUserCount(headerId);

        int pageSize=100;
          //判断如果条数大于100 则进行分页
        if(pushUserCount<=pageSize)
        {
            List<DailyPushQuery> list= getPushUserList(headerId,1,pushUserCount);
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
                pool = new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                ;

                //分页多线程处理

                int page=pushUserCount%pageSize==0?pushUserCount/pageSize:(pushUserCount/pageSize+1);

                CountDownLatch latch = new CountDownLatch(page);
                List taskList=Lists.newArrayList();
                //生成线程对象列表
                for(int i=0;i<page;i++)
                {
                    taskList.add(new TransPushThread(headerId,i*pageSize+1,(i+1)*pageSize,latch));
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
     * 根据查询到的用户名单 生成推送列表
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
                    String shortUrl=produceShortUrl("1",dailyPushQuery.getUserId(),longUrl);
                    smsContent=smsContent.replace("{CONPON_URL}",shortUrl);
                    smsContent=smsContent.replace("{CONPON_NAME}",dailyPushQuery.getCouponName());

                    smsContent=smsContent.replace("{PROD}",dailyPushQuery.getRecLastName());
                    smsContent=smsContent.replace("{PROD_URL}",shortUrl);
                }
            }else
            {
                longUrl=dailyPushQuery.getRecLastLongurl();
                String shortUrl=produceShortUrl("1",dailyPushQuery.getUserId(),longUrl);
                smsContent=smsContent.replace("{PROD}",dailyPushQuery.getRecLastName());
                smsContent=smsContent.replace("{PROD_URL}",shortUrl);
            }

            dailyPushInfo.setDailyDetailId(dailyPushQuery.getDailyDetailId());
            dailyPushInfo.setSmsContent(smsContent);

            targetList.add(dailyPushInfo);
        }

        return targetList;
    }

    @SneakyThrows
    private String produceShortUrl(String appid,String userId,String longUrl)
    {
        //生成短链
        String url="http://shorturl.growth-master.com/short_url/shorten?appid="+appid+"&uid=" +userId+
                "&longUrl="+ URLEncoder.encode(longUrl,"UTF-8");
        //根据长链接生成短链接
        String result=callTextPlain(url);
        JSONObject rowData = JSONObject.parseObject(result);

        String shortUrl="";
        if(null!=rowData&&!StringUtils.isEmpty(rowData.getString("data")))
        {
            shortUrl=rowData.getString("data");
        }else
        {
            //todo 此处应该抛出异常
            shortUrl="";
        }
        return shortUrl;
    }

    /**
     * 分页获取待推送用户的名单
     * @param headerId
     * @param start
     * @param end
     * @return
     */
    List<DailyPushQuery>  getPushUserList(String headerId,int start,int end){
         return dailyPushMapper.getPushUserList(headerId,start,end);
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
     * 获取(当前时间节点)待推送的消息列表
     * @return
     */
    @Override
    public List<DailyPushInfo>  getSendSmsList() {
        return dailyPushMapper.getSendSmsList();
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

    /**
     * 发送消息
     * @return
     */
    @Override
    public int sendMessage(String userIdentify,String smsContent)
    {
        //判断近N天是否触达过
//        ValueOperations<String,String> operations=redisTemplate.opsForValue();
//        String value=operations.get("PUSH_"+userIdentify);
//
//        if(null!=value&&!"".equals(value))
//        {
//            log.error("用户{}存在被重复触达的风险！！",userIdentify);
//            return -1;
//        }else
//        {
            log.info("模拟触达给{}:{}",userIdentify,smsContent);
          //  operations.set("PUSH_"+userIdentify,userIdentify,604800);

            Random random = new Random();
            //模拟发送状态
            int rint=random.nextInt(100);
            return rint;
    //    }
    }


    @SneakyThrows
    private String callTextPlain(String url) {
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("Content-Type", "text/plain")
//                .addHeader("Authorization", Credentials.basic(username, password))
//                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    private OkHttpClient buildOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        return client;
    }


}

@Slf4j
class TransPushThread implements Callable{

    int start;
    int end;
    CountDownLatch latch;

    String headerId;

    public TransPushThread(String headerId,int start,int end,CountDownLatch latch)
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
            list = dailyPushService.getPushUserList(headerId,start,end);

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
