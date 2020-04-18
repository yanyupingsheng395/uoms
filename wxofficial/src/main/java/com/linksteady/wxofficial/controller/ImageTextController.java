package com.linksteady.wxofficial.controller;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.entity.ImageTextInfo;
import com.linksteady.wxofficial.common.wechat.entity.MaterialInfo;
import com.linksteady.wxofficial.common.wechat.enums.ResultCodeEnum;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.bo.MaterialBo;
import com.linksteady.wxofficial.entity.po.ImageText;
import com.linksteady.wxofficial.service.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@RestController
@RequestMapping("/imageText")
public class ImageTextController {

    @Autowired
    private ImageTextService imageTextService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest queryRequest) {
        int limit = queryRequest.getLimit();
        int offset = queryRequest.getOffset();
        Map<String, String> data = Maps.newHashMap();
        data.put("appId", wxProperties.getAppId());
        data.put("count", String.valueOf(limit));
        data.put("offset", String.valueOf(offset));
        data.put("type", "news");
        MaterialBo materialBo = operateService.getMaterialList(data);
        return ResponseBo.okOverPaging(null, Integer.parseInt(materialBo.getItemCount()), materialBo.getArticles());
    }

    /**
     * 添加图文消息
     * @return
     */
    @RequestMapping("/addImageText")
    public ResponseBo addImageText(ImageText imageText) throws Exception {
        ImageTextInfo imageTextInfo = new ImageTextInfo();
        imageTextInfo.setAuthor(imageText.getAuthor());
        imageTextInfo.setContent(new String(imageText.getContent(), StandardCharsets.UTF_8));
        imageTextInfo.setDigest(imageText.getWxAbstract());
        imageTextInfo.setTitle(imageText.getTitle());
        imageTextInfo.setThumbMediaId(new String(imageText.getCover(), StandardCharsets.UTF_8));
        Map<String, String> result = operateService.addNews(imageTextInfo);
        if(ResultCodeEnum.RES_200.code.equalsIgnoreCase(result.get("code"))) {
            return ResponseBo.ok("保存成功！");
        }else {
            return ResponseBo.error("保存失败！");
        }
    }

    @RequestMapping("/getImageText")
    public ResponseBo getImageText(@RequestParam("id") int id) {
        return ResponseBo.okWithData(null, imageTextService.getImageText(id));
    }

    @RequestMapping("/deleteImageText")
    public ResponseBo deleteImageText(@RequestParam String id) throws Exception {
        Map<String, String> result = operateService.deleteMaterial(id);
        if(ResultCodeEnum.RES_200.code.equalsIgnoreCase(result.get("code"))) {
            return ResponseBo.ok();
        }else {
            return ResponseBo.error();
        }
    }

    @RequestMapping("/updateImageText")
    public ResponseBo updateImageText(ImageText imageText) {
        ImageTextInfo imageTextInfo = new ImageTextInfo();
        imageTextInfo.setAuthor(imageText.getAuthor());
        imageTextInfo.setContent(new String(imageText.getContent(), StandardCharsets.UTF_8));
        imageTextInfo.setDigest(imageText.getWxAbstract());
        imageTextInfo.setTitle(imageText.getTitle());
        imageTextInfo.setThumbMediaId(new String(imageText.getCover(), StandardCharsets.UTF_8));
        operateService.editMaterial(imageTextInfo);
        return ResponseBo.ok();
    }
}
