package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.UserMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户映射API接口
 */
@RestController
@Slf4j
@RequestMapping("/userMapping")
public class UserMappingController extends ApiBaseController{

    @Autowired
    UserMappingService userMappingService;

    /**
     * update
     * @param request
     * @param signature
     * @param timestamp
     * @param externalUserId 外部联系人ID
     * @param followUserId 对应导购ID
     * @return
     */
    @PostMapping("/update")
    public ResponseBo userMapping(HttpServletRequest request, String signature, String timestamp,
                                  String externalUserId,
                                  String followUserId,
                                  String phoneNum) {
        String validate=validateLegality(request,signature,timestamp,externalUserId,followUserId,phoneNum);
        if(StringUtils.isNotEmpty(validate))
        {
            return ResponseBo.error(validate);
        }
        //对参数进行校验
        if(StringUtils.isEmpty(externalUserId)||StringUtils.isEmpty(followUserId)||StringUtils.isEmpty(phoneNum))
        {
            return ResponseBo.error("参数不能为空!");
        }

        Long userId=userMappingService.updateMappingInfo(externalUserId,followUserId,phoneNum);
        return ResponseBo.okWithData("",userId);
    }

    /**
     * delete
     * @param request
     * @param signature
     * @param timestamp
     * @param externalUserId 外部联系人ID
     * @param followUserId 对应导购ID
     * @return
     */
    @PostMapping("/delete")
    public ResponseBo userMapping(HttpServletRequest request, String signature, String timestamp,
                                  String externalUserId,
                                  String followUserId) {
        String validate=validateLegality(request,signature,timestamp,externalUserId,followUserId);
        if(StringUtils.isNotEmpty(validate))
        {
            return ResponseBo.error(validate);
        }

        //对参数进行校验
        if(StringUtils.isEmpty(externalUserId)||StringUtils.isEmpty(followUserId))
        {
            return ResponseBo.error("参数不能为空!");
        }

        //对当前匹配到的用户进行清除
        try {
            userMappingService.deleteMappingInfo(externalUserId,followUserId);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("删除用户匹配关系错误，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }

    }
}
