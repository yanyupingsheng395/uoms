package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.operate.constant.QywxApiPathConstants;
import com.linksteady.operate.domain.QywxMediaImg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wxMedia")
public class QywxChooseMediaController {
    @Autowired
    CommonFunService commonFunService;

    /**
     * 获取图片列表
     * @param request
     * @return
     */
    @RequestMapping("/getMediaImgList")
    public ResponseBo getMediaImgList(QueryRequest request) throws Exception {
        //获取当前企业微信API应用的地址
        SysInfoBo sysInfoBo=commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
        if(null==sysInfoBo|| StringUtils.isEmpty(sysInfoBo.getSysDomain()))
        {
            throw new Exception("企业微信模块尚未配置");
        }
        String qywxUrl=sysInfoBo.getSysDomain();
        String url=qywxUrl+ QywxApiPathConstants.GET_VALID_MEDIA_LIST;
        Map<String,String> param=new HashMap<>();
        param.put("limit",request.getLimit()+"");
        param.put("offset",request.getOffset()+"");
        String result= OkHttpUtil.postRequestByFormBody(url,param);
        JSONObject jsonObject = JSON.parseObject(result);
        List<QywxMediaImg> qywxImageList = new ArrayList<>();
        int count=0;
        String res = OkHttpUtil.getRequest(qywxUrl+QywxApiPathConstants.GET_VALID_MEDIA_COUNT);
        JSONObject jscount = JSON.parseObject(res);
        if(null==jsonObject||200!=jsonObject.getIntValue("code")||null==jscount||200!=jscount.getIntValue("code")){
            return ResponseBo.error();
        }else{
            String data = jsonObject.getString("data");
            count=jscount.getIntValue("data");
            if(!StringUtils.isEmpty(data)){
                qywxImageList = JSONObject.parseArray(data, QywxMediaImg.class);
            }
        }
        return ResponseBo.okOverPaging(null, count, qywxImageList);
    }

    /**
     * 上传图片(临时素材)
     * @param
     * @return
     */
    @PostMapping("/uploadQywxMaterial")
    public ResponseBo uploadQywxMaterial(String title,String base64Code)  {
        try {
            //获取当前企业微信API应用的地址
            SysInfoBo sysInfoBo=commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
            if(null==sysInfoBo|| StringUtils.isEmpty(sysInfoBo.getSysDomain())){
                throw new Exception("企业微信模块尚未配置");
            }
            String qywxUrl=sysInfoBo.getSysDomain();
            String url=qywxUrl+QywxApiPathConstants.UPDATE_QYWX_MEDIA;
            Map<String,String> param=new HashMap<>();
            param.put("title",title);
            param.put("base64Code",base64Code);
            String result= OkHttpUtil.postRequestByFormBody(url,param);
            JSONObject jsonObject = JSON.parseObject(result);
            if(null==jsonObject||200!=jsonObject.getIntValue("code")){
                return ResponseBo.error();
            }
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传素材（图片）报错！");
            return ResponseBo.error();
        }
    }

}
