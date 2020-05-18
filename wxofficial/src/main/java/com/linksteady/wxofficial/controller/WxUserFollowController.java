package com.linksteady.wxofficial.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.entity.po.WxFollowReply;
import com.linksteady.wxofficial.service.WxUserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author hxcao
 * @date 2020/4/24
 */
@RestController
@RequestMapping("/wxUserFollow")
public class WxUserFollowController {

    @Autowired
    private WxUserFollowService wxUserFollowService;

    /**
     * 保存数据
     * @return
     */
    @RequestMapping("/saveData")
    public ResponseBo saveData(WxFollowReply wxFollowReply) {
        wxUserFollowService.saveData(wxFollowReply);
        return ResponseBo.ok();
    }

    @RequestMapping("/getDataList")
    public ResponseBo getDataList() {
        return ResponseBo.okOverPaging(null, 0, wxUserFollowService.getDataList());
    }

    @RequestMapping("/getDataById")
    public ResponseBo getDataById(String id) throws UnsupportedEncodingException {
        return ResponseBo.okWithData(null, wxUserFollowService.getDataById(id));
    }

    @RequestMapping("/deleteById")
    public ResponseBo deleteById(String id) {
        wxUserFollowService.deleteById(id);
        return ResponseBo.ok();
    }

    @RequestMapping("/updateData")
    public ResponseBo updateData(WxFollowReply wxFollowReply) {
        wxUserFollowService.updateData(wxFollowReply);
        return ResponseBo.ok();
    }
}
