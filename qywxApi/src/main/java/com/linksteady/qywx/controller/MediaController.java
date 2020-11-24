package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.Base64Img;
import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/wxMedia")
@Slf4j
public class MediaController extends BaseController {

    @Autowired
    MediaService mediaService;

    /**
     * 获取图片列表
     * @param request
     * @return
     */
    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count=mediaService.getImageCount();
        List<QywxImage> qywxImageList = mediaService.getImageList(limit,offset);
        return ResponseBo.okOverPaging(null, count, qywxImageList);
    }

    /**
     * 上传图片
     * @param
     * @return
     */
    @PostMapping("/uploadMaterial")
    public ResponseBo saveData(String title,String base64Code)  {
        try {
            String fileSuffix = base64Code.substring("data:image/".length(), base64Code.lastIndexOf(";base64,"));
            String fileName="tmp." + fileSuffix;
            File file = Base64Img.base64ToFile(base64Code, fileName);

            mediaService.uploadImage(title,file,getCurrentUser().getUsername());
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传素材（图片）报错！");
            return ResponseBo.error();
        }
    }
}
