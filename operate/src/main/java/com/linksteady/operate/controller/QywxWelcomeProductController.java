package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxWelcomeProduct;
import com.linksteady.operate.service.QywxWelcomeProductService;
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
    private QywxWelcomeProductService qywxWelcomeProductService;

    @GetMapping("/getTableDataList")
    public ResponseBo getTableDataList(Integer limit, Integer offset) {
        int count = qywxWelcomeProductService.getTableDataCount();
        List<QywxWelcomeProduct> dataList = qywxWelcomeProductService.getTableDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcomeProduct qywxWelcomeProduct) {
        qywxWelcomeProductService.saveData(qywxWelcomeProduct);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById(String productId) {
        return ResponseBo.okWithData(null, qywxWelcomeProductService.getDataById(productId));
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(QywxWelcomeProduct qywxWelcomeProduct) {
        qywxWelcomeProductService.updateData(qywxWelcomeProduct);
        return ResponseBo.ok();
    }

    @PostMapping("/deleteProductById")
    public ResponseBo deleteProductById(String productId) {
        qywxWelcomeProductService.deleteProductById(productId);
        return ResponseBo.ok();
    }
}
