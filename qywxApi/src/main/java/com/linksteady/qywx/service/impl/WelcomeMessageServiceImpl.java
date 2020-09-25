package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.dao.WelcomeMessageMapper;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.domain.WelcomeMpParam;
import com.linksteady.qywx.service.ApiService;
import com.linksteady.qywx.service.WelcomeMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Slf4j
@Service
public class WelcomeMessageServiceImpl implements WelcomeMessageService {

    @Autowired
    WelcomeMessageMapper welcomeMessageMapper;

    @Autowired
    ApiService apiService;

    /**
     * 获取小程序卡片封面的mediaId
     */
    @Override
    public synchronized String getWelcomeMpMediaId() {
        //获取欢迎语小程序卡片的media_id及media_id的失效时间
        WelcomeMpParam welcomeMpParam=welcomeMessageMapper.getWelcomeMpParam();

        long expireTime= null==welcomeMpParam.getWcMediaExpireDate()?0l: Timestamp.valueOf(welcomeMpParam.getWcMediaExpireDate()).getTime();
        long now= Timestamp.valueOf(LocalDateTime.now()).getTime();

        //如果失效时间<当前时间 则重新上传
        if(now>expireTime|| StringUtils.isEmpty(welcomeMpParam.getWcMediaId()))
        {
            //调用企业微信接口，完成临时素材的上传
            String corpId=apiService.getQywxCorpId();
            String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
            String signature= SHA1.gen(timestamp);
            String qywxDomainUrl=apiService.getQywxDomainUrl();

            String url=qywxDomainUrl+"/api/uploadTempMedia";

            Map<String,String> param= Maps.newHashMap();
            param.put("corpId",corpId);
            param.put("timestamp",timestamp);
            param.put("signature",signature);
            param.put("fileType","image");
            File file= null;
            try {
                file = byteArrayToFile(welcomeMpParam.getWcMediaContent(),"wc_miniprogram_media_temp.png");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(file==null)
            {
                return "";
            }

            String tempMediaResult= OkHttpUtil.postFileAndData(url,param,file);
            JSONObject resultObject = JSON.parseObject(tempMediaResult);
            if (null==resultObject||resultObject.getIntValue("code")!= 200) {
                log.error("上传小程序卡片图片到临时素材失败");
                return "";
            }else
            {
                String mediaId=resultObject.getString("data");
                //更新到param表中
                LocalDateTime expreDt=LocalDateTime.now().plusDays(3);
                welcomeMessageMapper.updateWcMediaExpireInfo(mediaId,expreDt);
                return mediaId;
            }
        }else
        {
            return welcomeMpParam.getWcMediaId();
        }
    }

    /**
     * 获取有效的企业微信欢迎语消息
     * @return
     */
    @Override
    public QywxMessage getValidWelcomeMessage() {
        //获取一个有效的欢迎语方案
        QywxWelcome qywxWelcome=welcomeMessageMapper.getValidWelcome();
        if(qywxWelcome==null)
        {
            return null;
        }else
        {
            QywxMessage qywxMessage=new QywxMessage();
            //文本
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getContent()))
            {
                qywxMessage.setText(qywxWelcome.getContent());
            }
            //图片
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getPicUrl()))
            {
                qywxMessage.setImgPicUrl(qywxWelcome.getPicUrl());
            }
            //链接
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getLinkTitle()))
            {
                qywxMessage.setLinkTitle(qywxWelcome.getLinkTitle());
                qywxMessage.setLinkPicUrl(qywxWelcome.getLinkPicurl());
                qywxMessage.setLinkDesc(qywxWelcome.getLinkDesc());
                qywxMessage.setLinkUrl(qywxWelcome.getLinkUrl());
            }
            //小程序
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getMiniprogramTitle()))
            {
                qywxMessage.setMpAppid(apiService.getMpAppId());
                qywxMessage.setMpPicMediaId(getWelcomeMpMediaId());
                qywxMessage.setMpTitle(qywxWelcome.getMiniprogramTitle());
                qywxMessage.setMpPage(qywxWelcome.getMiniprogramPage());
            }
            return qywxMessage;
        }
    }

    /**
     * base64字符串转化成图片
     * @param content
     * @param fileName
     * @return
     * @throws Exception
     */
    private File byteArrayToFile(byte[] content, String fileName) throws Exception {
        File file = null;
        //创建文件目录
        String filePath = "file/";
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            file = new File(filePath + File.separator + fileName);
            OutputStream out = new FileOutputStream(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(content);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
}
