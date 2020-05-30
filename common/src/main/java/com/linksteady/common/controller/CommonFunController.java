package com.linksteady.common.controller;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Slf4j
@Controller
@RequestMapping("/api")
public class CommonFunController extends BaseController {

    @Autowired
    private CommonFunService commonFunService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @ResponseBody
    @RequestMapping("/updatePassword")
    public ResponseBo updatePassword(@RequestParam("newPassword") String newPassword) {
        try {
            UserBo userBo = getCurrentUser();
            commonFunService.updatePassword(userBo.getUserId(), newPassword);
            getSubject().logout();
            return ResponseBo.ok("更改密码成功！");
        } catch (Exception e) {
            log.error("更改密码失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return ResponseBo.error("更改密码失败，请联系管理员！");
        }
    }

    /**
     * 检查用户输入的密码和原密码是否一致
     * @param password
     * @return
     */
    @RequestMapping("/user/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        UserBo userBo = super.getCurrentUser();
        String newPass = MD5Utils.encrypt(userBo.getUsername().toLowerCase(), password);
        return commonFunService.checkPassword(userBo.getUserId(),newPass);
    }
}
