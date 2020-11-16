package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.ExternalContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 外部联系人事件
 * @author huang
 * @date 2020/9/17
 */
@Slf4j
@RestController
@RequestMapping("/contractEvent")
public class ExternalContactEventController {

    @Autowired
    private ExternalContactService externalContactService;

    /**
     * 获取导购运行结果的数据
     * @param request
     * @return
     */
    @RequestMapping("/syncContract")
    public ResponseBo syncContract(HttpServletRequest request) {
        try {
            externalContactService.syncExternalContact();
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("同步具有外部客户联系功能的成员失败，原因为{}",e);
            return ResponseBo.error();
        }
    }
}
