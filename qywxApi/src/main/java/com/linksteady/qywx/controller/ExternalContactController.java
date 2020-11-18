package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.FollowUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huang
 * @date 2020/9/17
 */
@Slf4j
@RestController
@RequestMapping("/contract")
public class ExternalContactController{

    @Autowired
    private ExternalContactService externalContactService;

    /**
     * 同步外部客户
     * @param request
     * @return
     */
    @RequestMapping("/syncContract")
    public ResponseBo syncContract(HttpServletRequest request) {
        try {
            externalContactService.syncExternalContact();
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("同步外部客户失败，原因为{}",e);
            return ResponseBo.error();
        }
    }
}
