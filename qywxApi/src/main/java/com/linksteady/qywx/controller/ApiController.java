package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.Base64Img;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.qywx.constant.FilePathConsts;
import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.domain.QywxMediaImg;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.MediaService;
import com.linksteady.qywx.service.QywxGropMsgService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    QywxGropMsgService qywxGropMsgService;

    @Autowired
    QywxService qywxService;

    @Autowired
    MediaService mediaService;

    /**
     * 返回接口应用的状态
     */
    @RequestMapping("/status")
    public ResponseBo status() {
        return ResponseBo.ok("up");
    }

    @RequestMapping("/addMsgTemplate")
    public String addMsgTemplate(@RequestBody String data) {
        return qywxGropMsgService.addMsgTemplate( JSONObject.parseObject(data));
    }

    /**
     * 获取corpID
     */
    @RequestMapping("/getCorpId")
    public String getCorpId() {
        try {
            return qywxService.getRedisConfigStorage().getCorpId();
        } catch (Exception e) {
           log.error("获取企业微信所属公司出错，错误原因为{}",e);
            return "";
        }
    }

    /**
     * 获取mpAppId
     */
    @RequestMapping("/getMpAppId")
    public String getMpAppId() {
        try {
            return qywxService.getRedisConfigStorage().getMpAppId();
        } catch (Exception e) {
            log.error("获取企业微信关联的小程序出错，错误原因为{}",e);
            return "";
        }
    }

    @RequestMapping("/getMpMediaId")
    public ResponseBo getMpMediaId(HttpServletRequest request,
                                   @RequestParam("signature")String signature,
                                   @RequestParam("timestamp")String timestamp,
                                   @RequestParam("identityType")String identityType,
                                   @RequestParam("identityId") Long identityId){
        try {
            return ResponseBo.okWithData(null,mediaService.getMpMediaId(identityType, identityId));
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }


    /**
     * 获取临时素材图片列表(有效)
     * @param request
     * @return
     */
    @RequestMapping("/getValidMediaImgList")
    public ResponseBo getValidMediaImgList(HttpServletRequest request,
                                      @RequestParam("limit")int limit,
                                      @RequestParam("offset")int offset) {
        List<QywxMediaImg> qywxImageList = mediaService.getValidMediaImgList(limit,offset);
        try {
            return ResponseBo.okWithData(null,qywxImageList);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 获取临时素材数量(有效)
     * @return
     */
    @RequestMapping("/getValidMediaImgCount")
    public ResponseBo getMediaImgCount() {
        int count=mediaService.getValidMediaImgCount();
        try {
            return ResponseBo.okWithData(null,count);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 获取永久素材
     * @param request
     * @param limit
     * @param offset
     * @return
     */
    @RequestMapping("/getPermanentMediaImgList")
    public ResponseBo getPermanentMediaImgList(HttpServletRequest request,
                                               @RequestParam("limit")int limit,
                                               @RequestParam("offset")int offset){
        List<QywxImage> qywxImageList = mediaService.getImageList(limit,offset);
        try {
            return ResponseBo.okWithData(null,qywxImageList);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 获取临时素材数量
     * @return
     */
    @RequestMapping("/getPermanentMediaImgCount")
    public ResponseBo getPermanentMediaImgCount() {
        int count=mediaService.getImageCount();
        try {
            return ResponseBo.okWithData(null,count);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 上传图片(临时素材)
     * @param
     * @return
     */
    @PostMapping("/uploadPermanentMaterial")
    public ResponseBo uploadPermanentMaterial(HttpServletRequest request,
                                         @RequestParam("title") String title,
                                         @RequestParam("base64Code") String base64Code)  {
        try {
            String fileSuffix = base64Code.substring("data:image/".length(), base64Code.lastIndexOf(";base64,"));
            //生成文件名 md5(title+时间戳.fileSuffix)
            String timestamp= String.valueOf(System.currentTimeMillis());
            String fileName= MD5Utils.encrypt(title)+"_"+timestamp+"."+fileSuffix;
            File file = Base64Img.base64ToFile(base64Code, fileName, FilePathConsts.TEMP_IMAGE_PATH);
            //TODO 此处需要获取当前用户的名称，由调用方提供
            mediaService.uploadImage(title,file,"");
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传素材（图片）报错！");
            return ResponseBo.error();
        }
    }

    /**
     * 上传图片(临时素材)
     * @param
     * @return
     */
    @PostMapping("/uploadQywxMaterial")
    public ResponseBo uploadQywxMaterial(HttpServletRequest request,
                                         @RequestParam("title") String title,
                                         @RequestParam("base64Code") String base64Code)  {
        try {
            String fileSuffix = base64Code.substring("data:image/".length(), base64Code.lastIndexOf(";base64,"));
            //生成文件名 md5(title+时间戳.fileSuffix)
            String timestamp= String.valueOf(System.currentTimeMillis());
            String fileName= MD5Utils.encrypt(title)+"_"+timestamp+"."+fileSuffix;
            File file = Base64Img.base64ToFile(base64Code, fileName, FilePathConsts.TEMP_IMAGE_PATH);
            //TODO 此处需要获取当前用户的名称，由调用方提供
            mediaService.uploadQywxMaterial(title,file,"");
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传素材（图片）报错！");
            return ResponseBo.error();
        }
    }



    /**
     * 获取accessToken
     */
    @RequestMapping("/getAccessToken")
    public String getAccessToken() {
        try {
            return qywxService.getAccessToken();
        } catch (Exception e) {
            log.error("获取企业微信的AccessToken，错误原因为{}",e);
            return "";
        }
    }

}
