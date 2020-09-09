package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.dao.QywxMdiaMapper;
import com.linksteady.operate.dao.QywxParamMapper;
import com.linksteady.operate.domain.QywxMediaImage;
import com.linksteady.operate.domain.QywxParam;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxMdiaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
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
    public int getMediaImageCount() {
        return qywxMdiaMapper.getMediaImageCount();
    }

    @Override
    public List<QywxMediaImage> getMediaImageList(int limit, int offset) {
        return qywxMdiaMapper.getMediaImageList(limit,offset);
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
    public synchronized String getMminiprogramMediaId() {
        //获取当前小程序卡片的media_id及media_id的失效时间
        QywxParam qywxParam=qywxParamMapper.getQywxParam();

        long expireTime= null==qywxParam.getMediaExpireDate()?0l:Timestamp.valueOf(qywxParam.getMediaExpireDate()).getTime();
        long now= Timestamp.valueOf(LocalDateTime.now()).getTime();

        //如果失效时间<当前时间 则重新上传
        if(now>expireTime|| StringUtils.isEmpty(qywxParam.getMediaId()))
        {
           //调用企业微信接口，完成临时素材的上传
            String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
            String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
            String signature= SHA1.gen(timestamp);
            String qywxDomainUrl=configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode());

            String url=qywxDomainUrl+"/api/uploadTempMedia";

            Map<String,String> param= Maps.newHashMap();
            param.put("corpId",corpId);
            param.put("timestamp",timestamp);
            param.put("signature",signature);
            param.put("fileType","image");
            File file= null;
            try {
                file = byteArrayToFile(qywxParam.getMediaContent(),"miniprogram_media_temp.png");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(file==null)
            {
                return null;
            }

            String tempMediaResult=OkHttpUtil.postFileAndData(url,param,file);
            JSONObject resultObject = JSON.parseObject(tempMediaResult);
            if (null==resultObject||resultObject.getIntValue("code")!= 200) {
                log.error("上传小程序卡片图片到临时素材失败");
                return null;
            }else
            {
                String mediaId=resultObject.getString("data");
                //更新到param表中
                LocalDateTime expreDt=LocalDateTime.now().plusDays(3);
                qywxParamMapper.updateMediaExpireInfo(mediaId,expreDt);
                return mediaId;
            }

        }else
        {
            return qywxParam.getMediaId();
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
