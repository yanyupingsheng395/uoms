package com.linksteady.wxofficial.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.vo.WxTagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/4/20
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @GetMapping("/getDataListPage")
    public List<WxTagVo> getDataListPage() {
        String url = wxProperties.getServiceDomain() + wxProperties.getTagListUrl();
        String result = operateService.getDataList(url);
        List<WxTagVo> dataList = JSON.parseArray(JSON.parseObject(result).getString("msg"), WxTagVo.class);
        return dataList;
    }

    @PostMapping("/saveData")
    public ResponseBo saveData(String name) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("name", name);
        String url = wxProperties.getServiceDomain() + wxProperties.getAddTagUrl();
        operateService.saveData(url, param);
        return ResponseBo.ok();
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(String name, String id) {
        Map<String, String> param = Maps.newHashMap();
        param.put("name", name);
        param.put("tagId", id);
        String url = wxProperties.getServiceDomain() + wxProperties.getUpdateTagUrl();
        operateService.updateData(url, param);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteData")
    public ResponseBo deleteData(@RequestParam String id) {
        String url = wxProperties.getServiceDomain() + wxProperties.getDeleteTagUrl();
        operateService.deleteById(url, id);
        return ResponseBo.ok();
    }
}
