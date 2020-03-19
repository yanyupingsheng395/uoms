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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * 获取智能券
     * @return
     */
    @GetMapping("/getIntelCouponList")
    public List<CouponInfo> getIntelCouponList() {
        return couPonService.getIntelCouponList();
    }
}
