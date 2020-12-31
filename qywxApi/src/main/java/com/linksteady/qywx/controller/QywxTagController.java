package com.linksteady.qywx.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.QywxTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qywxtag")
public class QywxTagController {

    @Autowired
    private QywxTagService qywxTagService;

    @Autowired
    QywxService qywxService;

    /**
     * 分页查询标签组集合
     * @param request
     * @return
     */
    @RequestMapping("/getGroupTag")
    public ResponseBo getGroupTag(QueryRequest request){
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count=qywxTagService.getGroupTagCount();
        List<QywxTagGroup> list = getTagGroupList(limit, offset);
        return ResponseBo.okOverPaging(null,count,list);
    }

    /**
     * 根据标签组ID获取标签组下的所有标签
     * @param request
     * @return
     */
    @RequestMapping("/getTagGroupDetail")
    public ResponseBo getTagGroupDetail(QueryRequest request){
        int limit = request.getLimit();
        int offset = request.getOffset();
        String groupId = request.getParam().get("groupId");
        int count=qywxTagService.getTagCount(groupId);
        List<QywxTag> list = qywxTagService.getTagGroupDetail(limit, offset,groupId);
        return ResponseBo.okOverPaging(null,count,list);
    }


    /**
     * 获取标签群组
     * @return
     */
    @RequestMapping("/getTagList")
    @ResponseBody
    public ResponseBo getTagList(){
        return ResponseBo.okWithData(null,getTagGroupList(0,0));
    }

    /**
     * 公共方法，获取标签组的集合
     * @return
     */
    private List<QywxTagGroup> getTagGroupList(int limit,int offset){
        return qywxTagService.getTagList(limit,offset);
    }

    /**
     * 添加标签组
     * 企业微信API提示：在添加标签组的同时，必须添加一个标签
     * @param groupTagName   标签名称
     * @param groupName     标签组名称
     * @return
     * @throws WxErrorException
     */
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

    /**
     * 添加标签
     * @param tagName   标签名称
     * @param groupid   标签组ID
     * @return
     * @throws WxErrorException
     */
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

    /**
     *修改标签或者标签组名称，都可调用这一个方法
     * @param id   标签ID/标签组ID
     * @param name  新标签名称/新标签组名称
     * @param flag  G：修改标签组/T:修改标签
     * @return
     */
    @RequestMapping("/updateGroupTagName")
    @ResponseBody
    public ResponseBo updateGroupTagName(@RequestParam String id,@RequestParam String name,@RequestParam String flag ) throws Exception {
        if("G".equals(flag)){
            int count=qywxTagService.queryGroupTag(name);
            if(count>0){
                return ResponseBo.error("标签组名称，请重新填写！");
            }
        }else{
            int count = qywxTagService.queryTag(name, id);
            if(count>0){
                return ResponseBo.error("该名称已经存在该标签组下，请重新填写！");
            }
        }
        qywxTagService.updateGroupTagName(id,name,flag);
        return ResponseBo.ok();
    }

    /**
     * 删除标签组或者标签
     * @param id   标签组ID/标签ID
     * @param flag  G：修改标签组/T:修改标签
     * @return
     */
    @RequestMapping("/delGroupTag")
    public ResponseBo delGroupTag(@RequestParam String id,@RequestParam String flag) throws Exception {
        qywxTagService.delGroupTag(id,flag);
        return ResponseBo.ok();
    }

}