package com.linksteady.wxofficial.controller;

import com.alibaba.fastjson.JSON;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.po.WxFollowReply;
import com.linksteady.wxofficial.entity.po.WxMsgReply;
import com.linksteady.wxofficial.entity.vo.WxTagVo;
import com.linksteady.wxofficial.service.WxMsgReplyService;
import com.linksteady.wxofficial.service.WxUserFollowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/4/24
 */
@RestController
@RequestMapping("/wxMsgReply")
public class WxMsgReplyController {

    @Autowired
    private WxMsgReplyService wxMsgReplyService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    /**
     * 保存数据
     * @return
     */
    @RequestMapping("/saveData")
    public ResponseBo saveData(WxMsgReply wxMsgReply) {
        wxMsgReplyService.saveData(wxMsgReply);
        return ResponseBo.ok();
    }

    @RequestMapping("/getDataList")
    public ResponseBo getDataList() {
        List<WxMsgReply> dataList = wxMsgReplyService.getDataList();
        List<WxTagVo> tagList = getTagList();
        Map<Long, String> tagMap = tagList.stream().collect(Collectors.toMap(WxTagVo::getId, WxTagVo::getName));
        dataList.stream().filter(v -> StringUtils.isNotEmpty(v.getTagId())).forEach(x -> {
            List<Long> tagIds = Arrays.asList(x.getTagId().split(",")).stream().map(Long::valueOf).collect(Collectors.toList());
            List<String> tagNames = tagIds.stream().map(tagMap::get).collect(Collectors.toList());
            x.setTagNames(StringUtils.join(tagNames, ","));
        });
        return ResponseBo.okOverPaging(null, 0, dataList);
    }

    private List<WxTagVo> getTagList() {
        String url = wxProperties.getServiceDomain() + wxProperties.getTagListUrl();
        String result = operateService.getDataList(url);
        return JSON.parseArray(JSON.parseObject(result).getString("msg"), WxTagVo.class);
    }

    @RequestMapping("/getDataById")
    public ResponseBo getDataById(String id) throws UnsupportedEncodingException {
        return ResponseBo.okWithData(null, wxMsgReplyService.getDataById(id));
    }

    @RequestMapping("/deleteById")
    public ResponseBo deleteById(String id) {
        wxMsgReplyService.deleteById(id);
        return ResponseBo.ok();
    }

    @RequestMapping("/updateData")
    public ResponseBo updateData(WxMsgReply wxMsgReply) {
        wxMsgReplyService.updateData(wxMsgReply);
        return ResponseBo.ok();
    }
}
