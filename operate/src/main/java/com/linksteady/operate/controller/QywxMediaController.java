package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.Base64Img;
import com.linksteady.operate.domain.QywxMediaImage;
import com.linksteady.operate.service.QywxMdiaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@RestController
@RequestMapping("/qywxMedia")
@Slf4j
public class QywxMediaController {

    @Autowired
    private QywxMdiaService qywxMdiaService;

    /**
     * 获取图片列表
     * @param request
     * @return
     */
    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count=qywxMdiaService.getMediaImageCount();
        List<QywxMediaImage> qywxMediaImageList = qywxMdiaService.getMediaImageList(limit,offset);
        return ResponseBo.okOverPaging(null, count, qywxMediaImageList);
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

            qywxMdiaService.uploadImage(title,file);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("上传素材（图片）报错！");
            return ResponseBo.error();
        }
    }

}
