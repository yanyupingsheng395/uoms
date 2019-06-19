package com.linksteady.operate.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.DiagDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 诊断明细
 * @author caohuixue
 */
@RestController
@RequestMapping("/diagdetail")
public class DiagDetailController {

    @Autowired
    private DiagDetailService service;

    private static Logger logger = LoggerFactory.getLogger(DiagDetailController.class);

    @PostMapping("/save")
    public ResponseBo save(@RequestParam("json") String json) {
        try{
            service.save(json);
            return ResponseBo.ok("保存信息成功！");
        }catch (Exception e) {
            logger.error("保存信失败：", e);
            return ResponseBo.error("未知错误发生！");
        }
    }
}
