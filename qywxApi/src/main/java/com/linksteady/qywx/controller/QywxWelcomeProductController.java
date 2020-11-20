package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxWelcomeProduct;
import com.linksteady.qywx.service.WelcomeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@RestController
@RequestMapping("/qywxWelcomeProduct")
public class QywxWelcomeProductController {

    @Autowired
    private WelcomeProductService welcomeProductService;

    @GetMapping("/getTableDataList")
    public ResponseBo getTableDataList(Integer limit, Integer offset) {
        int count = welcomeProductService.getTableDataCount();
        List<QywxWelcomeProduct> dataList = welcomeProductService.getTableDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcomeProduct qywxWelcomeProduct) {
        welcomeProductService.saveData(qywxWelcomeProduct);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById(String productId) {
        return ResponseBo.okWithData(null, welcomeProductService.getDataById(productId));
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(QywxWelcomeProduct qywxWelcomeProduct) {
        welcomeProductService.updateData(qywxWelcomeProduct);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProductById")
    public ResponseBo deleteProductById(String productId) {
        welcomeProductService.deleteProductById(productId);
        return ResponseBo.ok();
    }
}
