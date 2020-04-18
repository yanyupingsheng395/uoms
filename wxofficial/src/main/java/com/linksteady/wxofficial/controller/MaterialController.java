package com.linksteady.wxofficial.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.bo.MaterialBo;
import com.linksteady.wxofficial.entity.po.ImageText;
import com.linksteady.wxofficial.service.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@RestController
@RequestMapping("/material")
public class MaterialController {
    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Map<String, String> data = Maps.newHashMap();
        data.put("appId", wxProperties.getAppId());
        data.put("count", String.valueOf(limit));
        data.put("offset", String.valueOf(offset));
        data.put("type", "image");
        MaterialBo materialBo = operateService.getMaterialList(data);
        return ResponseBo.okOverPaging(null, Integer.parseInt(materialBo.getItemCount()), materialBo.getItems());
    }
}
