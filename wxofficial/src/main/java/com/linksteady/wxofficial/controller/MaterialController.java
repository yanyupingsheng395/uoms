package com.linksteady.wxofficial.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.wxofficial.common.wechat.entity.MaterialInfo;
import com.linksteady.wxofficial.common.wechat.enums.ResultCodeEnum;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.bo.MaterialBo;
import com.linksteady.wxofficial.entity.po.ImageText;
import com.linksteady.wxofficial.service.ImageTextService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@RestController
@RequestMapping("/material")
@Slf4j
public class MaterialController {
    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String type = request.getParam().get("type");
        Map<String, String> data = Maps.newHashMap();
        data.put("appId", wxProperties.getAppId());
        data.put("count", String.valueOf(limit));
        data.put("offset", String.valueOf(offset));
        data.put("type", type);
        MaterialBo materialBo = operateService.getMaterialList(data);
        return ResponseBo.okOverPaging(null, Integer.parseInt(materialBo.getItemCount()), materialBo.getItems());
    }

    @RequestMapping("/uploadMaterial")
    public ResponseBo uploadMaterial(MaterialInfo materialInfo, MultipartFile file) throws Exception {
        File newFile = null;
        if(null != file) {
            newFile = FileUtils.multipartFileToFile(file);
        }
        operateService.uploadMaterial(materialInfo, newFile);
        return ResponseBo.ok();
    }

    @RequestMapping("/deleteMaterial")
    public ResponseBo deleteMaterial(@RequestParam String id) throws Exception {
        Map<String, String> result = operateService.deleteMaterial(id);
        if(ResultCodeEnum.RES_200.code.equalsIgnoreCase(result.get("code"))) {
            return ResponseBo.ok();
        }else {
            return ResponseBo.error();
        }
    }

    @RequestMapping("/getMaterialOther")
    public ResponseBo getMaterialOther(String mediaId, String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        ResponseBody result = operateService.getMaterialOther(mediaId, fileName);
        response.setHeader("Content-Disposition", "inline;fileName=" + java.net.URLEncoder.encode(fileName, "utf-8"));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try (InputStream inputStream = result.byteStream(); OutputStream os = response.getOutputStream()) {
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
        }
        return ResponseBo.ok();
    }

    @RequestMapping("/getMaterialVideo")
    public ResponseBo getMaterialVideo(String mediaId) {
        return ResponseBo.okWithData(null, operateService.getMaterialVideo(mediaId));
    }
}
