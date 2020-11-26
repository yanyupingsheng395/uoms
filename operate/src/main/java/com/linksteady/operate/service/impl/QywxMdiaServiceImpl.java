package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxMdiaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * 企业微信素材
 */
@Service
@Slf4j
public class QywxMdiaServiceImpl implements QywxMdiaService {

    @Autowired
    CommonFunService commonFunService;


    @Override
    public String getMpMediaId(String productId) throws Exception{
        //获取当前企业微信API应用的地址
        SysInfoBo sysInfoBo=commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
        if(null==sysInfoBo|| StringUtils.isEmpty(sysInfoBo.getSysDomain()))
        {
            throw new Exception("企业微信模块尚未配置");
        }
        String qywxUrl=sysInfoBo.getSysDomain();
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String identityType="PRODUCT";
        String signature= SHA1.gen(timestamp,identityType,productId);
        //调用http请求
        String url=qywxUrl+"/api/getMpMediaId";

        Map<String,String> param= Maps.newHashMap();
        param.put("timestamp",timestamp);
        param.put("signature",signature);
        param.put("identityType",identityType);
        param.put("identityId",productId);
        String result=OkHttpUtil.postRequestByFormBody(url,param);
        JSONObject resultObject = JSON.parseObject(result);
        if (null==resultObject||resultObject.getIntValue("code")!= 200) {
            log.error("获取素材失败，接口返回的结果:{}",result);
            throw  new LinkSteadyException("获取素材失败"+result);
        }else
        {
            String mediaId=resultObject.getString("data");
            return mediaId;
        }
    }

}
