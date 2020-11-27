package com.linksteady.qywx.controller;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.QywxUser;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2020/9/17
 */
@Slf4j
@RestController
@RequestMapping("/contract")
public class ExternalContactController extends BaseController {

    @Autowired
    private ExternalContactService externalContactService;

    @Autowired
    QywxService qywxService;

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

    /**
     * 获取当前导购手机号维护不正确的用户列表
     */
    @RequestMapping("/selectRemarkInvalid")
    @ResponseBody
    public ResponseBo selectRemarkInvalid(HttpServletRequest httpServletRequest,
                                          QueryRequest request)
    {
        QywxUser user=(QywxUser)httpServletRequest.getSession().getAttribute("user");
        String corpId=qywxService.getCorpId();
        String followUserId = user.getUserId();

        int count=externalContactService.selectRemarkInvalidCount(corpId,followUserId);
        List<ExternalContact> list=externalContactService.selectRemarkInvalid(request.getOffset(),request.getLimit(),corpId,followUserId);
        return ResponseBo.okOverPaging(null,count,list);
    }

    /**
     * 导购关系引导(根据给定的条件获取对应的用户)
     */
    @RequestMapping("/getGuidanceList")
    @ResponseBody
    public Map<String, Object> getGuidanceList(HttpServletRequest httpServletRequest,
                                               QueryRequest request,
                                               @RequestParam("relation") String relation,
                                               @RequestParam("loss") String loss,
                                               @RequestParam("stagevalue") String stagevalue,
                                               @RequestParam("interval") String interval)
    {
        UserBo user=(UserBo)httpServletRequest.getSession().getAttribute("user");
        String corpId=qywxService.getCorpId();
        String followUserId = user.getUsername();
        return super.selectByPageNumSize(request, () -> externalContactService.getQywxGuidanceList(corpId,followUserId,relation,loss,stagevalue,interval));
    }

    /**
     *导购关系引导中未购买客户
     * 按添加时间条件查询
     */
    @RequestMapping("/getAddTimeList")
    @ResponseBody
    public Map<String,Object> getAddTimeList(HttpServletRequest httpServletRequest,
                                             QueryRequest request,
                                             @RequestParam("addtime") String addtime){
        QywxUser user=(QywxUser)httpServletRequest.getSession().getAttribute("user");
        String corpId=user.getCorpId();
        String followUserId = user.getUserId();
        return super.selectByPageNumSize(request, () -> externalContactService.getAddTimeList(corpId,followUserId,addtime));
    }
}
