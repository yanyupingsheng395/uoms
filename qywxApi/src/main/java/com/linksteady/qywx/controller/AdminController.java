package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理面板的controller
 */
@Controller
@Slf4j
public class AdminController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    ExternalContactService externalContactService;

    @Autowired
    QywxService qywxService;

    /**
     * 获取管理员列表
     */
    @RequestMapping("/admin/getAdminList")
    @ResponseBody
    public ResponseBo getAdminList(HttpServletRequest request)
    {
        String corpId=qywxService.getCorpId();
        List<QywxUser> list=userService.getAdminList(corpId);
        return ResponseBo.okWithData("",list);
    }

    /**
     * 获取用户手机号维护情况
     */
    @RequestMapping("/admin/phoneFixStatis")
    @ResponseBody
    public ResponseBo getPhoneFixStatis(HttpServletRequest request)
    {
        String corpId=qywxService.getCorpId();
        List<PhoneFixStatis> list=externalContactService.getPhoneFixStatis(corpId);
        return ResponseBo.okWithData("",list);
    }

    /**
     * 获取重复外部联系人情况及匹配情况
     */
    @RequestMapping("/admin/repeatStatis")
    @ResponseBody
    public ResponseBo repeatStatis(HttpServletRequest request)
    {
        String corpId=qywxService.getCorpId();
        List<RepeatStatis> list=externalContactService.getRepeatStatis(corpId);
        MappingStatis mappingStatis = externalContactService.getMappingStatis(corpId);
        return ResponseBo.okWithData(mappingStatis,list);
    }

    /**
     * 更新管理员
     */
    @RequestMapping("/admin/updateAdmin")
    @ResponseBody
    public ResponseBo updateAdmin(HttpServletRequest request)
    {
        String corpId=qywxService.getCorpId();
        try {
            List<ApplicationAdmin> applicationAdminList=qywxService.getAdminList(corpId);
            //对管理员信息进行保存
            userService.saveAdminInfo(corpId,applicationAdminList);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("更新管理员信息失败，原因为{}",e);
            return ResponseBo.error();
        }


    }

}
