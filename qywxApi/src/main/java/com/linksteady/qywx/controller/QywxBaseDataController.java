package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.qywx.domain.QywxDeptUser;
import com.linksteady.qywx.service.QywxBaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hxcao
 * @date 2020/8/27
 */
@Controller
@RequestMapping("/qywxBaseData")
public class QywxBaseDataController {

    @Autowired
    private QywxBaseDataService qywxBaseDataService;

    @RequestMapping("/getFollowUserList")
    @ResponseBody
    public ResponseBo getFollowUserList(Integer limit, Integer offset) {
        int count = qywxBaseDataService.getFollowUserCount();
        return ResponseBo.okOverPaging(null, count, qywxBaseDataService.getFollowUserList(limit, offset));
    }

    @RequestMapping("/getDeptList")
    @ResponseBody
    public ResponseBo getDeptList(Integer limit, Integer offset) {
        int count = qywxBaseDataService.getDeptCount();
        return ResponseBo.okOverPaging(null, count, qywxBaseDataService.getDeptList(limit, offset));
    }
}
