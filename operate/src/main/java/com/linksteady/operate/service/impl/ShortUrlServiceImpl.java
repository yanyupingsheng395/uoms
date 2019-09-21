package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.util.OkHttpUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.stream.IntStream;

/**
 * 根据长链接生成短链接的服务
 */
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    @Override
    @SneakyThrows
    public String produceShortUrl(String appid,String userId,String longUrl) {
        //生成短链
        String url="http://shorturl.growth-master.com/short_url/shorten?appid="+appid+"&uid=" +userId+
                "&longUrl="+ URLEncoder.encode(longUrl,"UTF-8");
        //根据长链接生成短链接
        String result= OkHttpUtil.callTextPlain(url);
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
}
