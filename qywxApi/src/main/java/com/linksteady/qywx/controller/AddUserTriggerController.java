package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.AddUserHead;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.AddUserJobService;
import com.linksteady.qywx.service.AddUserTriggerService;
import com.linksteady.qywx.service.QywxParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@RestController
@RequestMapping("/addUserTrigger")
@Slf4j
public class AddUserTriggerController extends BaseController {

    private final ReentrantLock lock = new ReentrantLock();

    private final ReentrantLock processOrderLock = new ReentrantLock();

    @Autowired
    private AddUserJobService addUserJobService;

    @Autowired
    private AddUserTriggerService addUserTriggerService;

    @Autowired
    QywxParamService qywxParamService;

    @RequestMapping("/getHeadPageList")
    public ResponseBo getHeadPageList(int limit, int offset) {
        int count = addUserTriggerService.getHeadCount();
        List<AddUserHead> dataList = addUserTriggerService.getHeadPageList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 对主记录进行保存
     *
     * @param addUserHead
     * @return
     */
    @RequestMapping("/saveData")
    public synchronized ResponseBo saveData(AddUserHead addUserHead) {
        addUserTriggerService.saveData(addUserHead,getCurrentUser().getUsername());
        return ResponseBo.okWithData(null, addUserHead.getId());
    }

    /**
     * 删除任务
     *
     * @param id
     * @return
     */
    @RequestMapping("/deleteTask")
    public ResponseBo deleteTask(@RequestParam String id) {
        AddUserHead addUserHead = addUserTriggerService.getHeadById(Long.parseLong(id));
        if (null == addUserHead || !"edit".equals(addUserHead.getTaskStatus())) {
            return ResponseBo.error("仅有计划中的任务支持删除！");
        } else {
            addUserTriggerService.deleteTask(id);
            return ResponseBo.ok();
        }
    }

    @RequestMapping("/getSource")
    public ResponseBo getSource() {
        return ResponseBo.okWithData(null, addUserTriggerService.getSource());
    }

    /**
     * 更新头表
     */
    @RequestMapping("/updateSmsContentAndContactWay")
    public synchronized ResponseBo updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId
            , String contactWayUrl, String isSourceName, String isProdName) {
        String status = addUserTriggerService.getStatus(headId);
        if (!("edit".equalsIgnoreCase(status) || "stop".equalsIgnoreCase(status))) {
            return ResponseBo.error("该任务的当前状态不支持更新操作！");
        } else {
            addUserTriggerService.updateSmsContentAndContactWay(headId, smsContent, contactWayId, contactWayUrl, isSourceName, isProdName);
            return ResponseBo.ok();
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

                //todo 判断当前是否有已经处于doing状态的任务 如果有，给出提示，退出
                addUserTriggerService.execTask(headId, getCurrentUser().getUsername());
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
        return ResponseBo.okWithData(null, addUserTriggerService.getStatisApplyData(headId, scheduleId));
    }

    /**
     * 获取地域数据
     *
     * @return
     */
    @RequestMapping("/geRegionData")
    public ResponseBo geRegionData() {
        return ResponseBo.okWithData(null, addUserTriggerService.geRegionData());
    }


    /**
     * 获取默认的转化率、及发送人数
     */
    @RequestMapping("/getTriggerParam")
    public ResponseBo getTriggerParam() {
        QywxParam qywxParam = qywxParamService.getQywxParam();
        return ResponseBo.okWithData(null, qywxParam);
    }

    /**
     * 手工进行订单处理
     */
    @RequestMapping("/startProcess")
    public ResponseBo startProcess() {
        try {
            if (processOrderLock.tryLock()) {
                addUserJobService.deleteAddUserHistory();
                addUserJobService.processDailyOrders();
            } else {
                throw new OptimisticLockException("任务已经在运行中，请稍后再试!");
            }
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("企业微信拉新自动处理订单出错，错误原因为{}", e);
            return ResponseBo.error(e.getMessage());
        } finally {
            processOrderLock.unlock();
        }
    }

    /**
     * 手工进行效果的计算
     */
    @RequestMapping("/startJob")
    public ResponseBo startJob() {
        try {
            if (processOrderLock.tryLock()) {
                addUserJobService.updateTriggerStatus();
                addUserJobService.updateTriggerEffect();
            } else {
                throw new OptimisticLockException("任务已经在运行中，请稍后再试!");
            }
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("企业微信拉新自动处理订单出错，错误原因为{}", e);
            return ResponseBo.error(e.getMessage());
        } finally {
            processOrderLock.unlock();
        }
    }
}
