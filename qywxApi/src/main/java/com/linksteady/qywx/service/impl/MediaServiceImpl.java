package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.FilePathConsts;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.MediaMapper;
import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.domain.QywxMediaImg;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.MediaService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MediaServiceImpl implements MediaService {

    @Autowired(required = false)
    MediaMapper mediaMapper;
    @Autowired
    private QywxService qywxService;


    @Override
    public int getImageCount() {
        return mediaMapper.getImageCount();
    }

    @Override
    public List<QywxImage> getImageList(int limit, int offset) {
        return mediaMapper.getImageList(limit,offset);
    }

    @Override
    @Transactional
    public void uploadImage(String title, File file,String opUserName) throws Exception {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.Media.IMG_UPLOAD));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        log.info("上传图片的请求url:{}", requestUrl.toString());
        String result = OkHttpUtil.postUploadImg(requestUrl.toString(), file);
        log.info("上传图片返回的结果为{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        if (null==jsonObject||jsonObject.getIntValue("errcode")!= 0) {
            throw  new Exception("上传素材(图片)到企业微信失败");
        }else
        {
            String imgUrl=jsonObject.getString("url");
            mediaMapper.saveMediaImg(title,imgUrl,opUserName,file.getName());
        }
    }



    /**
     * identityType 可选的值有 PRODUCT表示商品 COUPON表示优惠券
     * @param identityType
     * @param identityId
     * @return
     */
    @Override
    @Transactional
    public String getMpMediaId(String identityType,Long identityId) throws Exception{
        String mediaId="";
        QywxMediaImg qywxMediaImg=mediaMapper.getQywxMediaImg(identityId,identityType);

        //根据标记类型、标记ID 判断是否能找到文件内容
        if(null!=qywxMediaImg)
        {
            long now= Timestamp.valueOf(LocalDateTime.now()).getTime();
            // 判断media_expire_date是否失效，如果未失效，则直接返回找到的mediaId 如果失效，则调用生成的逻辑(内容来自于当前表)
            long expireTime= null==qywxMediaImg.getMediaExpireDate()?0l:Timestamp.valueOf(qywxMediaImg.getMediaExpireDate()).getTime();
            if(now>expireTime|| StringUtils.isEmpty(qywxMediaImg.getMediaId())){
                //如果失效，那么就重新生成。
               File mediaFile = getMediaContent(identityType, identityId);
                if(mediaFile==null){
                    log.error("mediaContent未获取到值，请检查数据。");
                    return null;
                }
                JSONObject object = getMediaId(mediaFile);
                mediaId=(String)object.get("mediaId");
                LocalDateTime expreDt= (LocalDateTime) object.get("expreDt");
                mediaMapper.updateQywxMediaImgBymediaId(mediaFile.getName(),identityId,identityType,mediaId,expreDt);
            }else{
                mediaId=qywxMediaImg.getMediaId();
            }

        }else
        {
           //找不到，调用生成逻辑
            File mediaFile = getMediaContent(identityType, identityId);
            JSONObject object = getMediaId(mediaFile);
            mediaId=(String)object.get("mediaId");
            LocalDateTime expreDt= (LocalDateTime) object.get("expreDt");
            LocalDateTime nowtime = LocalDateTime.now();//新增时间
            mediaMapper.saveQywxMediaImg(mediaFile.getName(),"",nowtime,mediaId,expreDt,identityId,identityType);

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
    private synchronized File getMediaContent(String identityType,Long identityId) throws Exception{
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
                //todo 此处需要重构
                mediaContent=getImageByte(url);
            }else{
                //从默认的配置表获取图片内容
                qywxParam=mediaMapper.getMediaContent("PRODUCT");
                mediaContent=qywxParam.getMediaContent();
            }
        }else if("COUPON".equals(identityType)){
            //从默认的配置表获取
            qywxParam=mediaMapper.getMediaContent("COUPON");
            mediaContent=qywxParam.getCouponMediaContent();
        }else{
            log.error("错误类型，请检查数据是否正确！");
            return null;
        }

        String fileName=identityType+"_"+identityId+"_"+System.currentTimeMillis()+".png";
        //生成文件
        File file = byteArrayToFile(mediaContent,fileName, FilePathConsts.TEMP_IMAGE_PATH);
        return  file;
    };

    /**
     * 拿到图片内容，调用企业微信媒体素材接口，接口会返回mediaId和expire_date, 拿到以后更新uo_qywx_media_img
     * @return
     */
    private  JSONObject  getMediaId(File mediaFile){
            //调用企业微信接口，完成临时素材的上传
            String token="";
            try {
                token=qywxService.getAccessToken();
            }catch ( WxErrorException e){
                log.error("企业token未获取到！");
            }
            String type="image";
            StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.Media.MEDIA_UPLOAD));
            requestUrl.append(type);
            requestUrl.append("&access_token=" + token);
            log.info("上传临时素材的请求url:{}", requestUrl.toString());

            String tempMediaResult= OkHttpUtil.postUploadMedia(requestUrl.toString(),mediaFile);
            JSONObject resultObject = JSON.parseObject(tempMediaResult);
            if (null==resultObject||resultObject.getIntValue("errcode")!= 0) {
                log.error("上传小程序卡片图片到临时素材失败");
                return null;
            }
            //获取微信传过来的mediaId;
            String mediaId=resultObject.getString("media_id");
            JSONObject js=new JSONObject();
            LocalDateTime expreDt=LocalDateTime.now().plusDays(3);
            js.put("mediaId",mediaId);
            js.put("expreDt",expreDt);
            return js;
        }

    /**
     * base64字符串转化成图片
     */
    private File byteArrayToFile(byte[] content, String fileName,String filePath) throws Exception {
        File file = null;
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

    @Override
    public int getMediaImageCount() {
        return mediaMapper.getMediaImageCount();
    }

    @Override
    public List<QywxMediaImg> getMediaImgList(int limit, int offset) {

        return mediaMapper.getMediaImgList(limit,offset);
    }

    /**
     * 手工上传临时素材图片
     * @param title
     * @param file
     * @param username
     */
    @Override
    public void uploadQywxMaterial(String title, File file, String username) {
        //生成文件名
        String fileName="MANUAL_-1_"+System.currentTimeMillis()+".png";
        JSONObject object = getMediaId(file);
        String mediaId=(String)object.get("mediaId");
        LocalDateTime expreDt= (LocalDateTime) object.get("expreDt");
        LocalDateTime nowtime = LocalDateTime.now();
        mediaMapper.saveQywxMediaImg(fileName,title,nowtime,mediaId,expreDt,-1l,"MANUAL");
    }
}
