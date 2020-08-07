package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.service.AddUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@RestController
@RequestMapping("/addUser")
public class AddUserController {

    @Autowired
    private AddUserService addUserService;

    @RequestMapping("/getHeadPageList")
    public ResponseBo getHeadPageList(int limit, int offset) {
        int count = addUserService.getHeadCount();
        List<AddUserHead> dataList = addUserService.getHeadPageList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @RequestMapping("/saveData")
    public ResponseBo saveData(AddUserConfig addUserConfig) {
        addUserService.saveData(addUserConfig);
        return ResponseBo.ok();
    }


    @RequestMapping("/deleteTask")
    public ResponseBo deleteTask(@RequestParam String id) {
        addUserService.deleteTask(id);
        return ResponseBo.ok();
    }

    @RequestMapping("/editConfig")
    public ResponseBo editConfig(AddUserConfig addUserConfig) {
        addUserService.editConfig(addUserConfig);
        return ResponseBo.ok();
    }

    @RequestMapping("/getSource")
    public ResponseBo getSource() {
        return ResponseBo.okWithData(null, addUserService.getSource());
    }

    @RequestMapping("/test")
    public ResponseBo test() {
        try {
            addUserService.filterUsers(20,"11","110000,120000");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseBo.ok();
    }
}
