package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.linksteady.operate.dao.DailyPushMapper;
import com.linksteady.operate.domain.DailyPush;
import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import com.linksteady.operate.service.DailyPushService;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyPushServiceImpl implements DailyPushService {

    @Autowired
    private DailyPushMapper dailyPushMapper;

    private OkHttpClient okHttpClient = buildOkHttpClient();

    /**
     * 生成推送名单列表
     * @param headerId
     */
    @Override
    @SneakyThrows
    public void generatePushList(String headerId) {
          //根据headerID获取当前有多少人需要推送
          List<DailyPushQuery> list= dailyPushMapper.getDataList(headerId);

          //对list进行遍历 todo 此处要考虑如果list比较大的话要进行分页

        List<DailyPushInfo> targetList= Lists.newArrayList();
        DailyPushInfo dailyPushInfo=null;

          for(DailyPushQuery dailyPushQuery:list)
          {
              dailyPushInfo=new DailyPushInfo();
              String smsContent="";
              String smsCode="";
              String longUrl="";

              //判断是否有券的ID
              if(null!=dailyPushInfo.getCouponId()&&dailyPushInfo.getCouponId()!=-1L)
              {
                  smsContent=dailyPushQuery.getSmsContent();
                  smsCode=dailyPushQuery.getSmsCode();
                  longUrl=dailyPushQuery.getCouponUrl();
              }else
              {
                  smsContent=dailyPushQuery.getProdSmsContent();
                  smsCode=dailyPushQuery.getProdSmsCode();
                  longUrl=dailyPushQuery.getRecLastLongurl();
              }

//              String url="http://shorturl.growth-master.com/short_url/shorten?appid=1&uid=" +dailyPushQuery.getUserId()+
//                      "&longUrl="+ URLEncoder.encode(longUrl,"UTF-8");
//              //根据长链接生成短链接
//              String result=callTextPlain(url);
//              JSONObject rowData = JSONObject.parseObject(result);
//
//              String shortUrl="";
//              if(null!=rowData&&!StringUtils.isEmpty(rowData.getString("data")))
//              {
//                  shortUrl=rowData.getString("data");
//              }else
//              {
//                  //todo 此处应该抛出异常
//                  shortUrl="";
//              }
              String shortUrl="yhl.pub:81/n2e2ue";
//              //对短信模板中的内容进行替换
//              if(smsContent.indexOf("{PROD}")!=-1)
//              {
//                  smsContent=smsContent.replace("{PROD}",dailyPushQuery.getRecLastName());
//              }
//
//              if(smsContent.indexOf("{PROD_URL}")!=-1)
//              {
//                  smsContent=smsContent.replace("{PROD_URL}",shortUrl);
//              }
//
//              if(smsContent.indexOf("{CONPON_NAME}")!=-1)
//              {
//                  smsContent=smsContent.replace("{CONPON_NAME}",dailyPushQuery.getCouponName());
//              }
//
//              if(smsContent.indexOf("{CONPON_URL}")!=-1)
//              {
//                  smsContent=smsContent.replace("{CONPON_URL}",shortUrl);
 //             }

              dailyPushInfo.setHeadId(dailyPushQuery.getHeadId());
              dailyPushInfo.setDailyDetailId(dailyPushQuery.getDailyDetailId());
              dailyPushInfo.setUserId(dailyPushQuery.getUserId());
              dailyPushInfo.setPhoneNum(dailyPushQuery.getPhoneNum());

              dailyPushInfo.setSmsCode(smsCode);
              dailyPushInfo.setSmsContent(smsContent.replace("{","").replace("}",""));

              dailyPushInfo.setRecLastId(dailyPushQuery.getRecLastId());
              dailyPushInfo.setRecLastName(dailyPushQuery.getRecLastName());
              dailyPushInfo.setCouponId(dailyPushQuery.getCouponId());
              dailyPushInfo.setCouponName(dailyPushQuery.getCouponName());
              dailyPushInfo.setGrowthStreadyId(dailyPushQuery.getGrowthStreadyId());
              dailyPushInfo.setOrderPeriod(dailyPushQuery.getOrderPeriod());
              dailyPushInfo.setGroupId(dailyPushQuery.getGroupId());

              targetList.add(dailyPushInfo);
          }

          //对最终结果进行保存 如果list太大，需要对list分页保存
        int size=100;
        if(targetList.size()>0)
        {
            if(targetList.size()<=size)
            {
                dailyPushMapper.savePushInfo(targetList);
            }else //分页保存
            {
                //总记录数
                int subCount = targetList.size();
                //页数
                int subPageTotal = (subCount / size) + ((subCount % size > 0) ? 1 : 0);
                // 根据页码取数据
                for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
                    // 分页计算
                    int fromIndex = i * size;
                    int toIndex = ((i == len) ? subCount : ((i + 1) * size));
                    List<DailyPushInfo> temp = targetList.subList(fromIndex, toIndex);

                    //进行一次保存
                    dailyPushMapper.savePushInfo(temp);
                }
            }
        }


    }

    @Override
    public List<DailyPushInfo>  getSendSmsList() {
        return dailyPushMapper.getSendSmsList();
    }

    @Override
    public void updateSendStatus(List<DailyPushInfo> list,String status){
        dailyPushMapper.updateSendStatus(list,status);
    }

    @Override
    public void updateHeaderToDone() {
        dailyPushMapper.updateHeaderToDone();
    }

    @Override
    public void updateHeaderSendStatis() {
        dailyPushMapper.updateHeaderSendStatis();
    }

    @Override
    public List<DailyPush> getPushList(int start, int end, String headId) {
        return dailyPushMapper.getPushList(start, end, headId);
    }

    @Override
    public int getDataTotalCount(String headId) {
        return dailyPushMapper.getDataTotalCount(headId);
    }

    @SneakyThrows
    private String callTextPlain(String url) {
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("Content-Type", "text/plain")
//                .addHeader("Authorization", Credentials.basic(configManagerProperties.getUsername(), configManagerProperties.getPassword()))
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
