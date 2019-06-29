package com.linksteady.operate.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.service.DiagDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
@Slf4j
public class DiagDetailController {

    @Autowired
    private DiagDetailService service;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @PostMapping("/save")
    public ResponseBo save(@RequestParam("json") String json) {
        try{
            service.save(json);
            return ResponseBo.ok("保存信息成功！");
        }catch (Exception e) {
            log.error("保存信失败：", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("未知错误发生！");
        }
    }
}
