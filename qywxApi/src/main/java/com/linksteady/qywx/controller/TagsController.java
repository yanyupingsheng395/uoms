package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxUser;
import com.linksteady.qywx.domain.TagGroup;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 客户标签相关的API
 */
@RestController
public class TagsController extends BaseController {

    @Autowired
    TagService tagService;

    @Autowired
    QywxService qywxService;

    /**
     * 同步所有的部门信息
     * @return
     */
    @RequestMapping("/tag/selectTags")
    @ResponseBody
    public ResponseBo selectTags(HttpServletRequest request) {
        //获取当前用户的corpId
        String corpId=qywxService.getCorpId();
        //获取所有的group列表
        List<TagGroup> tagGroupList=tagService.selectTagGroupList(corpId);

        List<QywxTag> qywxTagList=tagService.selectTagList(corpId);

        for(TagGroup tagGroup:tagGroupList)
        {
            tagGroup.setQywxTagList(qywxTagList.stream().filter(t->t.getGroupId().equals(tagGroup.getGroupId())).collect(Collectors.toList()));
        }

        return ResponseBo.okWithData(null,tagGroupList);
    }

    @RequestMapping("/tag/syncTags")
    @ResponseBody
    public ResponseBo syncTags(HttpServletRequest request) {
        //获取当前用户的corpId
        String corpId=qywxService.getCorpId();
        try {
            tagService.syncTags(corpId);
            return ResponseBo.ok();
        } catch (WxErrorException e) {
            return ResponseBo.error();
        }
    }
}
