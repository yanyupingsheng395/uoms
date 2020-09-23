package com.linksteady.operate.controller;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.operate.domain.QywxDeptAndUser;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxDeptAndUserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hxcao
 * @date 2020/8/27
 */
@Controller
@RequestMapping("/qywxDeptAndUser")
public class QywxDeptAndUserController {

    @Autowired
    private QywxDeptAndUserService qywxDeptAndUserService;

    @RequestMapping("/uploadData")
    @ResponseBody
    public ResponseBo uploadData(@RequestParam("file") MultipartFile file) {
        String userName = ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername();
        qywxDeptAndUserService.uploadData(file, userName);
        return ResponseBo.ok();
    }

    @RequestMapping("/getUserTableData")
    @ResponseBody
    public ResponseBo getUserTableData(Integer limit, Integer offset) {
        int count = qywxDeptAndUserService.getUserTableCount();
        return ResponseBo.okOverPaging(null, count, qywxDeptAndUserService.getUserTableData(limit, offset));
    }

    @RequestMapping("/getDeptTableData")
    @ResponseBody
    public ResponseBo getDeptTableData(Integer limit, Integer offset) {
        int count = qywxDeptAndUserService.getDeptTableCount();
        return ResponseBo.okOverPaging(null, count, qywxDeptAndUserService.getDeptTableData(limit, offset));
    }

    /**
     * 获取组织架构数据树
     * @return
     */
    @RequestMapping("/getDeptAndUserTree")
    @ResponseBody
    public ResponseBo getDeptAndUserTree() {
        Tree<QywxDeptAndUser> deptAndUserTree = null;
        try {
            deptAndUserTree = qywxDeptAndUserService.getDeptAndUserTree();
        } catch (LinkSteadyException e) {
            return ResponseBo.error("未获取到可联系成员，请先完成组织架构数据的上传！");
        }
        return ResponseBo.okWithData(null, deptAndUserTree);
    }
}
