package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.service.CouPonService;
import com.linksteady.operate.service.DailyConfigService;
import com.linksteady.operate.service.DailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/daily")
public class DailyConfigController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyConfigService dailyConfigService;

    @Autowired
    private CouPonService couPonService;

    /**
     * 获取用户群组的数据
     */
    @GetMapping("/userGroupList")
    public ResponseBo userGroupList() {
        dailyConfigService.validUserGroup();
        List<DailyGroupTemplate> dataList = dailyService.getUserGroupList();
        return ResponseBo.okWithData(null, dataList);
    }

    /**
     * 理解用户
     */
    @RequestMapping("/usergroupdesc")
    public ResponseBo usergroupdesc(String userValue, String pathActive, String lifecycle) {
        return ResponseBo.okWithData("", dailyConfigService.usergroupdesc(userValue, pathActive, lifecycle));
    }

    /**
     * 触达用户之前进行用户群组的验证 并返回验证结果 true表示验证未通过 false表示验证通过
     */
    @GetMapping("/validUserGroup")
    public ResponseBo validUserGroup() {
        return ResponseBo.okWithData(null, dailyConfigService.validUserGroup());
    }

    /**
     * 删除用户群组上的文案配置
     * @return
     */
    @GetMapping("/deleteSmsGroup")
    public ResponseBo deleteSmsGroup(@RequestParam("groupId") String groupId) {
        dailyConfigService.deleteSmsGroup(groupId);
        return ResponseBo.ok();
    }

    @GetMapping("/updateWxMsgId")
    public ResponseBo updateWxMsgId(@RequestParam("groupId") String groupId, @RequestParam("qywxId") String qywxId) {
        dailyConfigService.updateWxMsgId(groupId, qywxId);
        return ResponseBo.ok();
    }

    /**
     * 根据选定的组获取短信和微信内容 短信优惠券和微信优惠券
     * @param userValue
     * @param lifeCycle
     * @param pathActive
     * @param tarType
     * @return
     */
    @GetMapping("/getCurrentGroupData")
    public ResponseBo getCurrentGroupData(@RequestParam("userValue") String userValue, @RequestParam("lifeCycle") String lifeCycle,
                                  @RequestParam("pathActive") String pathActive, @RequestParam("tarType") String tarType) {
        return ResponseBo.okWithData(null, dailyConfigService.getCurrentGroupData(userValue, lifeCycle, pathActive, tarType));
    }

    /**
     * 将补贴数据智能的安到每个组
     * @return
     */
    @GetMapping("/resetGroupCoupon")
    public ResponseBo resetGroupCoupon() {
        return dailyConfigService.resetGroupCoupon();
    }

    @GetMapping("/getUserGroupValue")
    public List<Map<String, String>> getUserGroupValue(@RequestParam("userValue") String userValue, @RequestParam("lifecycle") String lifecycle,
                                                       @RequestParam("pathActive") String pathActive) {
        return dailyConfigService.getUserGroupValue(userValue, lifecycle, pathActive);
    }
}
