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

    @RequestMapping("/getUserTableData")
    @ResponseBody
    public ResponseBo getUserTableData(Integer limit, Integer offset) {
        int count = qywxBaseDataService.getUserTableCount();
        return ResponseBo.okOverPaging(null, count, qywxBaseDataService.getUserTableData(limit, offset));
    }

    @RequestMapping("/getDeptTableData")
    @ResponseBody
    public ResponseBo getDeptTableData(Integer limit, Integer offset) {
        int count = qywxBaseDataService.getDeptTableCount();
        return ResponseBo.okOverPaging(null, count, qywxBaseDataService.getDeptTableData(limit, offset));
    }

    /**
     * 获取组织架构数据树
     * @return
     */
    @RequestMapping("/getDeptAndUserTree")
    @ResponseBody
    public ResponseBo getDeptAndUserTree() {
        Tree<QywxDeptUser> deptAndUserTree = null;
        try {
            deptAndUserTree = qywxBaseDataService.getDeptAndUserTree();
        } catch (Exception e) {
            return ResponseBo.error("未获取到可联系成员，请先完成组织架构数据的上传！");
        }
        return ResponseBo.okWithData(null, deptAndUserTree);
    }
}
