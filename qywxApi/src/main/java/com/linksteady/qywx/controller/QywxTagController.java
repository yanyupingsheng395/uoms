package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.QywxTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/qywxtag")
public class QywxTagController {

    @Autowired
    private QywxTagService qywxTagService;

    @Autowired
    QywxService qywxService;

    /**
     * 获取标签群组
     * @return
     */
    @RequestMapping("/getTagList")
    @ResponseBody
    public ResponseBo getTagList(){
        List<QywxTagGroup> list=qywxTagService.getTagList();
        return ResponseBo.okWithData(null,list);
    }

    @RequestMapping("/addTagGroup")
    @ResponseBody
    public ResponseBo addTagGroup(@RequestParam String groupTagName,@RequestParam String groupName) throws WxErrorException {
       int count=qywxTagService.queryGroupTag(groupName);
       if(count>0){
           return ResponseBo.error("标签组名称，请重新填写！");
       }
       qywxTagService.addTagGroup(groupTagName,groupName);
        return ResponseBo.ok();
    }

    @RequestMapping("/addTag")
    @ResponseBody
    public ResponseBo addTag(@RequestParam String tagName,@RequestParam String groupid) throws WxErrorException {
        int count=qywxTagService.queryTag(tagName,groupid);
        if(count>0){
            return ResponseBo.error("该名称已经存在该标签组下，请重新填写！");
        }
        qywxTagService.addTag(tagName,groupid);
        return ResponseBo.ok();
    }

}
