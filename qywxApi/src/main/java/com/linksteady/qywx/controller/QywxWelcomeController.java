package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.Base64Img;
import com.linksteady.qywx.constant.FilePathConsts;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.service.WelcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/3
 */
@RestController
@RequestMapping("/welcome")
public class QywxWelcomeController {

    @Autowired
    private WelcomeService qywxWelcomeService;

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcome qywxWelcome) {
        return ResponseBo.okWithData(null, qywxWelcomeService.saveData(qywxWelcome));
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(QywxWelcome qywxWelcome) {
        qywxWelcomeService.updateData(qywxWelcome);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataTableList")
    public ResponseBo getDataTableList(Integer limit, Integer offset) {
        int count = qywxWelcomeService.getDataCount();
        List<QywxWelcome> dataList = qywxWelcomeService.getDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @PostMapping("/deleteById")
    public ResponseBo deleteById(long id) {
        qywxWelcomeService.deleteById(id);
        return ResponseBo.ok();
    }

    @PostMapping("/updateStatus")
    public ResponseBo updateStatus(long id, String status) {
        qywxWelcomeService.updateStatus(id, status);
        return ResponseBo.ok();
    }


    @PostMapping("/saveModelImg")
    @ResponseBody
    public ResponseBo saveModelImg(String base64Code,String filename)  {
        try {
            String fileSuffix = base64Code.substring("data:image/".length(), base64Code.lastIndexOf(";base64,"));
            String imagePath=filename+"_" +System.currentTimeMillis()+"." + fileSuffix;
            File file = Base64Img.base64ToFile(base64Code, imagePath, FilePathConsts.WELCOME_IMAGE_PATH);
            return ResponseBo.okWithData(null,imagePath);
        } catch (Exception e) {
            return ResponseBo.error();
        }
    }
}
