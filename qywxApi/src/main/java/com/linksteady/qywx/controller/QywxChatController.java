package com.linksteady.qywx.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/qywxCustomer")
public class QywxChatController {

    @Autowired
    private QywxChatService qywxChatService;

    /**
     * 获取客户群列表
     *
     */
    @GetMapping("/getContractList")
    @ResponseBody
    public ResponseBo getContractList(QueryRequest request){
        int limit = request.getLimit();
        int offset = request.getOffset();
        String owner = request.getParam().get("owner");
        String status = request.getParam().get("status");
        int count = qywxChatService.getCount(owner,status);
        List<QywxChatBase> lists= qywxChatService.getDataList(limit, offset,owner,status);
        return ResponseBo.okOverPaging(null,count,lists);
    }

    /**
     * 根据群聊ID获取七天内群人数的详细信息
     * @param chatId
     * @return
     */
    @RequestMapping("/getDetailData")
    public ResponseBo getDetailData(@RequestParam String chatId){
        List<QywxChatStatistics> list= qywxChatService.getDetailData(chatId);
        return ResponseBo.okWithData(null,list);
    }

    /**
     * 获取添加好友数量和未添加好友数量
     * @param chatId
     * @return
     */
    @RequestMapping("/getFriendsNum")
    public ResponseBo getFriendsNum(@RequestParam String chatId){
        FriendsNumVO friendsNum=qywxChatService.getFriendsNum(chatId);
        return ResponseBo.okWithData(null,friendsNum);
    }


    @RequestMapping("/getCustomerList")
    @ResponseBody
    public ResponseBo getCustomerList(QueryRequest request, @RequestParam String chatId){
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = qywxChatService.getCustomerListCount(chatId);
        List<QywxChatDetail> lists= qywxChatService.getCustomerList(limit, offset,chatId);
        return ResponseBo.okOverPaging(null,count,lists);
    }

    /**
     * 获取群聊的名称信息
     * @param chatId
     * @return
     */
    @RequestMapping("/getChatBaseDetail")
    @ResponseBody
    public ResponseBo getChatBaseDetail(@RequestParam String chatId){
        QywxChatBase chatBase= qywxChatService.getChatBaseDetail(chatId);
        if(StringUtils.isEmpty( chatBase.getGroupName())){
            chatBase.setGroupName("群聊");
        }
        if(StringUtils.isEmpty(chatBase.getNotice())){
            chatBase.setNotice("无");
        }
        return ResponseBo.okWithData(null,chatBase);
    }


    /**
     * 从微信段获取所有群聊，并存入数据库
     */
    @RequestMapping("/syncQywxChatList")
    public ResponseBo syncQywxChatList() {
        try {
            qywxChatService.syncQywxChatList();
            return ResponseBo.ok();
        } catch (WxErrorException e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    @RequestMapping("/synQywxChatStatistics")
    public ResponseBo synQywxChatStatistics(){
        qywxChatService.syncChatStatistics();
        return ResponseBo.ok();
    }



    /**
     * 获取SOP规则列表内容
     */
    @RequestMapping("/getSopList")
    public ResponseBo getSopList(){
        List<QywxGroupSop> list=new ArrayList<>();
        list.add(new QywxGroupSop(1,"规则1","创建人1",new Date(),"Y"));
        list.add(new QywxGroupSop(2,"规则2","创建人2",new Date(),"Y"));
        list.add(new QywxGroupSop(3,"规则3","创建人3",new Date(),"Y"));
        list.add(new QywxGroupSop(4,"规则4","创建人4",new Date(),"Y"));
        list.add(new QywxGroupSop(5,"规则5","创建人5",new Date(),"Y"));
        return ResponseBo.okOverPaging(null,5,list);
    }

    @RequestMapping("/getPullGroupList")
    public ResponseBo getPullGroupList(){
        List<QywxAutoPullGroup> list=new ArrayList<>();
        list.add(new QywxAutoPullGroup(1,"https://wework.qpic.cn/wwpic/873871_XchdE8dYRJu4t1E_1606227987/0","群1","用户1",1,new Date()));
        list.add(new QywxAutoPullGroup(1,"https://wework.qpic.cn/wwpic/873871_XchdE8dYRJu4t1E_1606227987/0","群2","用户2",1,new Date()));
        list.add(new QywxAutoPullGroup(1,"https://wework.qpic.cn/wwpic/873871_XchdE8dYRJu4t1E_1606227987/0","群3","用户3",1,new Date()));
        list.add(new QywxAutoPullGroup(1,"https://wework.qpic.cn/wwpic/873871_XchdE8dYRJu4t1E_1606227987/0","群4","用户4",1,new Date()));
        list.add(new QywxAutoPullGroup(1,"https://wework.qpic.cn/wwpic/873871_XchdE8dYRJu4t1E_1606227987/0","群5","用户5",1,new Date()));
        return ResponseBo.okOverPaging(null,5,list);
    }

    @RequestMapping("/getTagGroupList")
    public ResponseBo getTagGroupList(){
        List<QywxTagGroup> list=new ArrayList<>();
        return ResponseBo.okOverPaging(null,5,list);
    }

    @RequestMapping("/getGroupCalenDarList")
    public ResponseBo getGroupCalenDarList(){
        List<QywxGroupCalenDar> list=new ArrayList<>();
        list.add(new QywxGroupCalenDar(1,"群1","创建人1",new Date(),"Y"));
        list.add(new QywxGroupCalenDar(1,"群2","创建人2",new Date(),"Y"));
        list.add(new QywxGroupCalenDar(1,"群3","创建人3",new Date(),"Y"));
        list.add(new QywxGroupCalenDar(1,"群4","创建人4",new Date(),"Y"));
        list.add(new QywxGroupCalenDar(1,"群5","创建人5",new Date(),"Y"));
        return ResponseBo.okOverPaging(null,5,list);
    }

    @RequestMapping("/getGroupChat")
    public ResponseBo getGroupChat(){
        List<QywxChatBase> lists=new ArrayList<>();
        return ResponseBo.okOverPaging(null,5,lists);
    }

}
