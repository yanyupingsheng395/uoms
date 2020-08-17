package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.AddUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@RestController
@RequestMapping("/addUser")
@Slf4j
public class AddUserController extends BaseController {

    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private AddUserService addUserService;

    @RequestMapping("/getHeadPageList")
    public ResponseBo getHeadPageList(int limit, int offset) {
        int count = addUserService.getHeadCount();
        List<AddUserHead> dataList = addUserService.getHeadPageList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 对主记录进行保存
     *
     * @param addUserHead
     * @return
     */
    @RequestMapping("/saveData")
    public ResponseBo saveData(AddUserHead addUserHead) {
        List<String> statusList = addUserService.getStatusList();
        long doingCnt = statusList.stream().filter(x -> x.equalsIgnoreCase("doing")).count();
        if(doingCnt > 0) {
            return ResponseBo.error("当前有任务正在执行中，无法保存新记录！");
        }else {
            addUserHead.setInsertDt(new Date());
            addUserHead.setInsertBy(getCurrentUser().getUsername());
            addUserHead.setUpdateDt(new Date());
            addUserHead.setUpdateBy(getCurrentUser().getUsername());
            addUserService.saveData(addUserHead);
            return ResponseBo.okWithData(null, addUserService.getHeadById(addUserHead.getId()));
        }
    }

    /**
     * 删除任务
     *
     * @param id
     * @return
     */
    @RequestMapping("/deleteTask")
    public ResponseBo deleteTask(@RequestParam String id) {
        AddUserHead addUserHead = addUserService.getHeadById(Long.parseLong(id));
        if (null == addUserHead || !"edit".equals(addUserHead.getTaskStatus())) {
            return ResponseBo.error("仅有计划中的任务支持删除！");
        } else {
            addUserService.deleteTask(id);
            return ResponseBo.ok();
        }
    }

    @RequestMapping("/getSource")
    public ResponseBo getSource() {
        return ResponseBo.okWithData(null, addUserService.getSource());
    }

    @RequestMapping("/saveDailyUserData")
    public ResponseBo saveDailyUserData(String headId, String dailyUserCnt, String dailyApplyRate) {
        return ResponseBo.okWithData(null, addUserService.saveDailyUserData(headId, dailyUserCnt, dailyApplyRate));
    }

    @RequestMapping("/updateSmsContentAndContactWay")
    public synchronized ResponseBo updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl) {
        String status = addUserService.getHeadById(Long.parseLong(headId)).getTaskStatus();
        if("edit".equalsIgnoreCase(status)) {
            addUserService.updateSmsContentAndContactWay(headId, smsContent, contactWayId, contactWayUrl);
            return ResponseBo.ok();
        }else {
            return ResponseBo.error("该记录当前状态不支持编辑操作！");
        }
    }

    /**
     * 提交任务进行执行
     *
     * @param headId
     * @return
     */
    @PostMapping("/executeTask")
    public ResponseBo executeTask(@Param("headId") long headId) {
        try {
            if (lock.tryLock()) {
                addUserService.execTask(headId, getCurrentUser().getUsername());
            } else {
                throw new OptimisticLockException("其他用户正在操作，请稍后再试!");
            }
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("企业微信拉新执行任务出错，错误原因为{}", e);
            return ResponseBo.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @RequestMapping("/getStatisApplyData")
    public ResponseBo getStatisApplyData(String headId, String scheduleId) {
        return ResponseBo.okWithData(null, addUserService.getStatisApplyData(headId, scheduleId));
    }

    /**
     * 获取地域数据
     *
     * @return
     */
    @RequestMapping("/geRegionData")
    public ResponseBo geRegionData() {
        return ResponseBo.okWithData(null, addUserService.geRegionData());
    }

}
