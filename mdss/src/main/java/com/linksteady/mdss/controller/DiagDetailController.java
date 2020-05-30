package com.linksteady.mdss.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.mdss.service.DiagDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("未知错误发生！");
        }
    }

    /**
     * 更改节点的标记状态
     * @return
     */
    @PostMapping("/updateAlarmFlag")
    public ResponseBo updateAlarmFlag(@RequestParam("diagId") String diagId, @RequestParam("nodeId") String nodeId, @RequestParam("flag") String flag) {
        try {
            service.updateAlarmFlag(diagId, nodeId, flag);
            return ResponseBo.ok();
        }catch (Exception e) {
            log.error("标记节点失败", e);
            return ResponseBo.error();
        }
    }
}
