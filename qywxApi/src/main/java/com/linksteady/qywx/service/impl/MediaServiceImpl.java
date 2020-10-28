package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.dao.MediaMapper;
import com.linksteady.qywx.domain.QywxMediaImg;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.ApiService;
import com.linksteady.qywx.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@Slf4j
public class MediaServiceImpl implements MediaService {

    @Autowired
    MediaMapper mediaMapper;

    @Autowired
    ApiService apiService;

    /**
     * identityType 可选的值有 PRODUCT表示商品 COUPON表示优惠券
     * @param identityType
     * @param identityId
     * @return
     */
    @Override
    public String getMpMediaId(String identityType,Long identityId) {
        String mediaId="";
        QywxMediaImg qywxMediaImg=mediaMapper.getQywxMediaImg(identityId,identityType);

        if(null!=qywxMediaImg)
        {
            long now= Timestamp.valueOf(LocalDateTime.now()).getTime();
            // 判断media_expire_date是否失效，如果未失效，则直接返回找到的mediaId 如果失效，则调用生成的逻辑(内容来自于当前表)
            long expireTime= null==qywxMediaImg.getMediaExpireDate()?0l:Timestamp.valueOf(qywxMediaImg.getMediaExpireDate()).getTime();
            if(now>expireTime|| StringUtils.isEmpty(qywxMediaImg.getMediaId())){
                //如果失效，那么就重新生成。
                byte[] mediaContent = getMediaContent(identityType, identityId);
                if(mediaContent==null){
                    log.error("mediaContent未获取到值，请检查数据。");
                    return null;
                }
                JSONObject object = getMediaId(mediaContent);
                mediaId=(String)object.get("mediaId");
                LocalDateTime expreDt= (LocalDateTime) object.get("expreDt");
                mediaMapper.updateQywxMediaImgBymediaId(identityId,identityType,mediaId,expreDt,mediaContent);
            }else{
                mediaId=qywxMediaImg.getMediaId();
            }

        }else
        {
           //找不到，调用生成逻辑
            byte[] mediaContent = getMediaContent(identityType, identityId);
            JSONObject object = getMediaId(mediaContent);
            mediaId=(String)object.get("mediaId");
            LocalDateTime expreDt= (LocalDateTime) object.get("expreDt");
            LocalDateTime nowtime = LocalDateTime.now();//新增时间
            mediaMapper.saveQywxMediaImg(mediaContent,nowtime,mediaId,expreDt,identityId,identityType);

        }
        return mediaId;
    }

    /**
     * 获取小程序封面内容
     * 查找图片？ 商品根据商品ID到w_product_media中取 获取不到则取uo_qywx_param中的media_content
     *  优惠券则直接取 uo_qywx_param中的coupon_media_content
     *
     *  identityType 可选的值有 PRODUCT表示商品 COUPON表示优惠券
     */
    private synchronized byte[] getMediaContent(String identityType,Long identityId){
        if(StringUtils.isEmpty(identityType)||identityId==null){
            log.error("传入数据有误，请查看数据是否正常！");
            return null;
        }
        byte[] mediaContent=null;
        QywxParam qywxParam=null;
        if("PRODUCT".equals(identityType)){
            //获取商品对应的图片地址
            String url=mediaMapper.getProductMediaContent(identityId);
            if(StringUtils.isNoneEmpty(url)){
                mediaContent=getImageByte(url);
            }else{
                qywxParam=mediaMapper.getMediaContent("PRODUCT");
                mediaContent=qywxParam.getMediaContent();
            }
        }else if("COUPON".equals(identityType)){
            qywxParam=mediaMapper.getMediaContent("COUPON");
            mediaContent=qywxParam.getCouponMediaContent();
        }else{
            log.error("错误类型，请检查数据是否正确！");
            return null;
        }
        return  mediaContent;
    };

    /**
     * 拿到图片内容，调用企业微信媒体素材接口，接口会返回mediaId和expire_date, 拿到以后更新uo_qywx_media_img
     * @return
     */
    private  JSONObject  getMediaId(byte[] mediaContent){
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
                file = byteArrayToFile(mediaContent,"miniprogram_media_temp.png");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(file==null){
                return null;
            }

            String tempMediaResult= OkHttpUtil.postFileAndData(url,param,file);
            JSONObject resultObject = JSON.parseObject(tempMediaResult);
            if (null==resultObject||resultObject.getIntValue("code")!= 200) {
                log.error("上传小程序卡片图片到临时素材失败");
                return null;
            }
            //获取微信传过来的mediaId;
            String mediaId=resultObject.getString("data");
            JSONObject js=new JSONObject();
            LocalDateTime expreDt=LocalDateTime.now().plusDays(3);
            js.put("mediaId",mediaId);
            js.put("expreDt",expreDt);
            return js;
        }

    /**
     * base64字符串转化成图片
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

    /**
     * 通过url，获取图片的字节数组
     */
    public  byte[] getImageByte(String strUrl){
        ByteArrayOutputStream baos = null;
        try{
            URL u = new URL(strUrl);
            BufferedImage image = ImageIO.read(u);
            baos = new ByteArrayOutputStream();
            ImageIO.write( image, "jpg", baos);
            baos.flush();
            return baos.toByteArray();
        }catch (Exception e){
            log.error("通过url，将图片转字节数组错误，请检查！");
        }finally{
            if(baos != null){
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error("关闭流失败！");
                }
            }
        }
        return  null;
    }

}
