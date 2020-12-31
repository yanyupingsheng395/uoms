package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.Base64Img;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.qywx.constant.FilePathConsts;
import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.domain.QywxMediaImg;
import com.linksteady.qywx.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/wxMedia")
@Slf4j
public class MediaController extends BaseController {

    @Autowired
    MediaService mediaService;

    /**
     * 获取图片列表(永久)
     * @param request
     * @return
     */
    @RequestMapping("/getImageList")
    public ResponseBo getImageList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count=mediaService.getImageCount();
        List<QywxImage> qywxImageList = mediaService.getImageList(limit,offset);
        return ResponseBo.okOverPaging(null, count, qywxImageList);
    }

    /**
     * 上传图片(永久)
     * @param
     * @return
     */
    @PostMapping("/uploadImage")
    public ResponseBo saveData(String title,String base64Code)  {
        try {
            String fileSuffix = base64Code.substring("data:image/".length(), base64Code.lastIndexOf(";base64,"));
            //生成文件名 md5(title+时间戳.fileSuffix)
            String timestamp= String.valueOf(System.currentTimeMillis());
            String fileName=MD5Utils.encrypt(title+"_"+timestamp+"."+fileSuffix);
            File file = Base64Img.base64ToFile(base64Code, fileName, FilePathConsts.FOREVER_IMAGE_PATH);
            mediaService.uploadImage(title,file,getCurrentUser().getUsername());
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传素材（图片）报错！");
            return ResponseBo.error();
        }
    }

    /**
     * 获取图片列表(临时素材)
     * @param request
     * @return
     */
    @RequestMapping("/getMediaImgList")
    public ResponseBo getMediaImgList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count=mediaService.getMediaImgCount();
        List<QywxMediaImg> qywxImageList = mediaService.getMediaImgList(limit,offset);
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
            String fileSuffix = base64Code.substring("data:image/".length(), base64Code.lastIndexOf(";base64,"));
            //生成文件名 md5(title+时间戳.fileSuffix)
            String timestamp= String.valueOf(System.currentTimeMillis());
            String fileName=MD5Utils.encrypt(title+"_"+timestamp+"."+fileSuffix);
            File file = Base64Img.base64ToFile(base64Code, fileName,FilePathConsts.TEMP_IMAGE_PATH);

            mediaService.uploadQywxMaterial(title,file,getCurrentUser().getUsername());
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传临时素材（图片）报错！");
            return ResponseBo.error();
        }
    }

    /**
     * 获取图片列表(临时素材 所有有效的)
     * @param request
     * @return
     */
    @RequestMapping("/getValidMediaImgList")
    public ResponseBo getValidMediaImgList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count=mediaService.getValidMediaImgCount();
        List<QywxMediaImg> qywxImageList = mediaService.getValidMediaImgList(limit,offset);
        return ResponseBo.okOverPaging(null, count, qywxImageList);
    }
}
