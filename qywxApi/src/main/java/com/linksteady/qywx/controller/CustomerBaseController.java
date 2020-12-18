package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/qywxCustomer")
public class CustomerBaseController {

    /**
     * 获取客户群列表
     */
    @RequestMapping("/getContractList")
    @ResponseBody
    public ResponseBo getContractList(){
        List<QywxContractList> lists=new ArrayList<>();
        lists.add(new QywxContractList(1,"群1","群主1",5,0,0,new Date()));
        lists.add(new QywxContractList(2,"群2","群主2",5,0,0,new Date()));
        lists.add(new QywxContractList(3,"群3","群主3",5,0,0,new Date()));
        lists.add(new QywxContractList(4,"群4","群主4",5,0,0,new Date()));
        lists.add(new QywxContractList(5,"群5","群主5",5,0,0,new Date()));
        return ResponseBo.okOverPaging(null,5,lists);
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
        list.add(new QywxTagGroup(1,"任务1","群名称1","邀请成员1",new Date(),1,1,1,1));
        list.add(new QywxTagGroup(1,"任务2","群名称2","邀请成员2",new Date(),1,1,1,1));
        list.add(new QywxTagGroup(1,"任务3","群名称3","邀请成员3",new Date(),1,1,1,1));
        list.add(new QywxTagGroup(1,"任务4","群名称4","邀请成员4",new Date(),1,1,1,1));
        list.add(new QywxTagGroup(1,"任务5","群名称5","邀请成员5",new Date(),1,1,1,1));
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
        List<QywxContractList> lists=new ArrayList<>();
        lists.add(new QywxContractList(1,"群1","群主1",5,0,0,new Date()));
        lists.add(new QywxContractList(2,"群2","群主2",5,0,0,new Date()));
        lists.add(new QywxContractList(3,"群3","群主3",5,0,0,new Date()));
        lists.add(new QywxContractList(4,"群4","群主4",5,0,0,new Date()));
        lists.add(new QywxContractList(5,"群5","群主5",5,0,0,new Date()));
        return ResponseBo.okOverPaging(null,5,lists);
    }

}
