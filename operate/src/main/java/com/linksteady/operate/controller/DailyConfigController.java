package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.DailyConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 每日运营配置
 */
@Slf4j
@RestController
@RequestMapping("/dailyConfig")
public class DailyConfigController {

    @Autowired
    private DailyConfigService dailyConfigService;

    /**
     * 获取用户群组的列表
     */
    @GetMapping("/userGroupList")
    public ResponseBo userGroupList() {
        //对用户群组信息进行校验
        dailyConfigService.validUserGroup();
        //当前默认按用户价值、活跃度、生命周期分组
        List<DailyGroupTemplate> dataList = dailyConfigService.getUserGroupList();
        return ResponseBo.okWithData(null, dataList);
    }

    /**
     * 触达用户之前进行用户群组的验证 并返回验证结果 true表示验证未通过 false表示验证通过
     */
    @GetMapping("/validUserGroup")
    public ResponseBo validUserGroup() {
        return ResponseBo.okWithData(null, dailyConfigService.validUserGroup());
    }


    /**
     * 更新群组上的 企业微信消息ID
     * @param groupId
     * @param qywxId
     * @return
     */
    @GetMapping("/updateWxMsgId")
    public ResponseBo updateWxMsgId(@RequestParam("groupId") Long groupId, @RequestParam("qywxId") Long qywxId) {
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
    @GetMapping("/getConfigInfoByGroup")
    public ResponseBo getConfigInfoByGroup(@RequestParam("userValue") String userValue, @RequestParam("lifeCycle") String lifeCycle,
                                  @RequestParam("pathActive") String pathActive, @RequestParam("tarType") String tarType) {
        return ResponseBo.okWithData(null, dailyConfigService.getConfigInfoByGroup(userValue, lifeCycle, pathActive, tarType));
    }

    /**
     * 将补贴数据智能的装配到每个组
     * @return
     */
    @GetMapping("/autoSetGroupCoupon")
    public ResponseBo autoSetGroupCoupon() {
        Lock lock = new ReentrantLock();
        if(lock.tryLock())
        {
            try {
                dailyConfigService.autoSetGroupCoupon();
                return ResponseBo.ok();
            } catch (OptimisticLockException e)
            {
                return ResponseBo.error(e.getMessage());
            }
            finally {
                lock.unlock();
            }
        }else
        {
            return ResponseBo.error("其他用户正在操作，请稍后再试!");
        }
    }

    /**
     * 获取理解群组的解释数据
     * @param userValue
     * @param lifecycle
     * @param pathActive
     * @return
     */
    @GetMapping("/getGroupDescription")
    public List<Map<String, String>> getGroupDescription(@RequestParam("userValue") String userValue, @RequestParam("lifecycle") String lifecycle,
                                                       @RequestParam("pathActive") String pathActive) {
        return dailyConfigService.getGroupDescription(userValue, lifecycle, pathActive);
    }

    /**
     * 每日成长任务用户群组设置短信内容
     *
     * @param groupId
     * @param smsCode
     * @return
     */
    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam Long groupId, @RequestParam String smsCode) {
        dailyConfigService.setSmsCode(groupId, smsCode);
        return ResponseBo.ok();
    }

    /**
     * 恢复文案的配置标记
     */
    @GetMapping("/resetOpFlag")
    public ResponseBo resetOpFlag() {
        dailyConfigService.resetOpFlag();
        return ResponseBo.ok();
    }
}
