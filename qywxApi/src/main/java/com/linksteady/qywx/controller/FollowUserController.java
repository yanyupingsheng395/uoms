package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
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
@RequestMapping("/followUser")
public class FollowUserController {

    @Autowired
    private FollowUserService followUserService;

    /**
     * 同步导购信息
     * @param request
     * @return
     */
    @RequestMapping("/syncQywxFollowUser")
    public ResponseBo syncQywxFollowUser(HttpServletRequest request) {
        try {
            followUserService.syncQywxFollowUser();
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("同步具有外部客户联系功能的成员失败，原因为{}",e);
            return ResponseBo.error();
        }
    }

    /**
     * 同步部门信息
     * @param request
     * @return
     */
    @RequestMapping("/syncDept")
    public ResponseBo syncDept(HttpServletRequest request) {
        try {
            followUserService.syncDept();
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("同步部门信息失败，原因为{}",e);
            return ResponseBo.error();
        }
    }
}
