package com.linksteady.common.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.OpenApiService;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
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
public class OpenApiController extends BaseController {

    @Autowired
    private OpenApiService openApiService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @ResponseBody
    @RequestMapping("/updatePassword")
    public ResponseBo updatePassword(@RequestParam("newPassword") String newPassword) {
        try {
            User user = (User) getCurrentUser();
            this.openApiService.updatePassword(user.getUsername(), newPassword);
            getSubject().logout();
            return ResponseBo.ok("更改密码成功！");
        } catch (Exception e) {
            log.error("更改密码失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error("更改密码失败，请联系管理员！");
        }
    }
}
