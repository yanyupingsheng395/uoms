package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.dao.QywxMdiaMapper;
import com.linksteady.operate.dao.QywxParamMapper;
import com.linksteady.operate.domain.QywxImage;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxMdiaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

/**
 * 企业微信素材
 */
@Service
@Slf4j
public class QywxMdiaServiceImpl implements QywxMdiaService {

    @Autowired
    QywxMdiaMapper qywxMdiaMapper;

    @Autowired
    ConfigService configService;

    @Autowired
    QywxParamMapper qywxParamMapper;

    @Override
    public int getImageCount() {
        return qywxMdiaMapper.getImageCount();
    }

    @Override
    public List<QywxImage> getImageList(int limit, int offset) {
        return qywxMdiaMapper.getImageList(limit,offset);
    }

    @Override
    public void uploadImage(String title, File file,String opUserName) throws Exception {
        //上传到企业微信
        String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String signature= SHA1.gen(timestamp);
        String qywxDomainUrl=configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode());
        //2.提交到企业微信端
        String url=qywxDomainUrl+"/api/uploadImg";

        Map<String,String> param= Maps.newHashMap();
        param.put("corpId",corpId);
        param.put("timestamp",timestamp);
        param.put("signature",signature);
        String result=OkHttpUtil.postFileAndData(url,param,file);

        JSONObject resultObject = JSON.parseObject(result);
        if (null==resultObject||resultObject.getIntValue("code")!= 200) {
            throw  new LinkSteadyException("上传素材(图片)到企业微信失败");
        }else
        {
            String imgUrl=resultObject.getString("data");
            qywxMdiaMapper.saveMediaImg(title,imgUrl,opUserName);
        }
    }

    @Override
    public String getMpMediaId(String productId) throws Exception{
        //获取当前企业微信API应用的地址
        String qywxApiUrl=configService.getValueByName(ConfigEnum.qywxApiUrl.getKeyCode());

        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String identityType="PRODUCT";
        String signature= SHA1.gen(timestamp,identityType,productId);
        //调用http请求
        String url=qywxApiUrl+"/wxMedia/getMpMediaId";

        Map<String,String> param= Maps.newHashMap();
        param.put("timestamp",timestamp);
        param.put("signature",signature);
        param.put("identityType",identityType);
        param.put("identityId",productId);
        String result=OkHttpUtil.postRequestByFormBody(url,param);

        JSONObject resultObject = JSON.parseObject(result);
        if (null==resultObject||resultObject.getIntValue("code")!= 200) {
            throw  new LinkSteadyException("获取素材失败");
        }else
        {
            String mediaId=resultObject.getString("data");
            return mediaId;
        }
    }

}
