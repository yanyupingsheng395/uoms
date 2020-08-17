package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.domain.QywxParam;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.AddUserJobService;
import com.linksteady.operate.service.AddUserTriggerService;
import com.linksteady.operate.service.QywxParamService;
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
@RequestMapping("/addUserTrigger")
@Slf4j
public class AddUserTriggerController extends BaseController {

    private final ReentrantLock lock = new ReentrantLock();

    private final ReentrantLock paramLock = new ReentrantLock();

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
        List<String> statusList = addUserTriggerService.getStatusList();
        long doingCnt = statusList.stream().filter(x -> x.equalsIgnoreCase("doing")).count();
        if(doingCnt > 0) {
            return ResponseBo.error("当前有任务正在执行中，无法保存新记录！");
        }else {
            addUserHead.setInsertDt(new Date());
            addUserHead.setInsertBy(getCurrentUser().getUsername());
            addUserHead.setUpdateDt(new Date());
            addUserHead.setUpdateBy(getCurrentUser().getUsername());
            addUserTriggerService.saveData(addUserHead);
            return ResponseBo.okWithData(null, addUserHead.getId());
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
        AddUserHead addUserHead = addUserTriggerService.getHeadById(Long.parseLong(id));
        if (null == addUserHead || !"edit".equals(addUserHead.getTaskStatus())) {
            return ResponseBo.error("仅有待计划的任务支持删除！");
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
        QywxParam qywxParam=qywxParamService.getQywxParam();
        return ResponseBo.okWithData(null,qywxParam);
    }

    /**
     * addNum:当前企业微信每日加人上限
     * 更新转化率
     */
    @PostMapping("/updateQywxParam")
    public ResponseBo updateQywxParam(int addNum,double addRate,int version) {
       //todo 对转化率进行校验 addNum>0 addRate>0 且这两个必须是数字
        try {
            if (paramLock.tryLock()) {

                //todo 校验当前是否有正在执行的拉新任务
                QywxParam qywxParam=qywxParamService.updateQywxParam(addNum,addRate,getCurrentUser().getUsername(),version);
                return ResponseBo.ok(qywxParam);
            } else {
                throw new OptimisticLockException("其他用户正在操作，请稍后再试!");
            }
        } catch (Exception e) {
            log.error("修改企业微信参数错误，错误原因为{}", e);
            if(e instanceof OptimisticLockException)
            {
                return ResponseBo.error(e.getMessage());
            }else
            {
                return ResponseBo.error("保存失败！");
            }
        } finally {
            paramLock.unlock();
        }

    }

    /**
     * 获取默认的转化率、及发送人数
     */
    @RequestMapping("/startJob")
    public ResponseBo startJob() {
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
}
