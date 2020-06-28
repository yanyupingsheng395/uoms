package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Tconfig;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.domain.PushLog;
import com.linksteady.operate.domain.enums.PushSignalEnum;
import com.linksteady.operate.service.PushListService;
import com.linksteady.operate.service.PushLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-09-28
 */
@RestController
@RequestMapping("/push")
@Slf4j
public class PushStatusController extends BaseController {


    @Autowired
    private PushLogService pushLogService;

    @Autowired
    private PushListService pushListService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private PushConfig pushConfig;

    /**
     * 关闭推送服务
     * @param
     * @return
     */
    @GetMapping("/stop")
    public ResponseBo stop() {
        //判断当前状态
        if("Y".equalsIgnoreCase(pushConfig.getPushFlag())) {
            //关闭服务
            try {
                pushConfig.sendPushSignal(PushSignalEnum.SIGNAL_STOP,getCurrentUser().getUsername());
                return ResponseBo.ok("关闭服务成功！");
            } catch (Exception e) {
                log.error("关闭推送服务异常，异常原因为{}",e);
                return ResponseBo.error("关闭服务失败！");
            }
        }else
        {
            return ResponseBo.error("关闭服务失败，服务已经是关闭状态！");
        }
    }

    /**
     * 开启推送服务
     * @param
     * @return
     */
    @GetMapping("/start")
    public ResponseBo start() {
        //判断当前状态
        if("N".equalsIgnoreCase(pushConfig.getPushFlag())) {
            //开启服务
            try {
                pushConfig.sendPushSignal(PushSignalEnum.SIGNAL_START,getCurrentUser().getUsername());
                return ResponseBo.ok("开启服务成功！");
            } catch (Exception e) {
                log.error("开启推送服务异常，异常原因为{}",e);
                return ResponseBo.error("开启服务失败！");
            }
        }else
        {
            return ResponseBo.error("开启服务失败,服务已经是开启状态！");
        }
    }

    /**
     * 获取当前服务的启动状态
     * @param
     * @return
     */
    @GetMapping("/status")
    public ResponseBo status() {
        return ResponseBo.okWithData(null, pushConfig.getPushFlag());
    }

    /**
     * 获取推送的日志
     * @param
     * @return
     */
    @GetMapping("/getPushLog")
    public ResponseBo getPushLog(@RequestParam("day") int day) {
        HeartBeatInfo heartBeatInfo=HeartBeatInfo.getInstance();

        List<PushLog> list=pushLogService.getPushLogList(day);
        //分成两部分
        List<PushLog> pushLogList= list.stream().filter(p->"1".equals(p.getLogType())&&p.getUserCount()>0).collect(Collectors.toList());
        List<PushLog> repeatLogList= list.stream().filter(p->"0".equals(p.getLogType())&&p.getUserCount()>0).collect(Collectors.toList());
        List<PushLog> purgeLogList= list.stream().filter(p->"2".equals(p.getLogType())).collect(Collectors.toList());
        List<PushLog> pushIctLogList= list.stream().filter(p->"10".equals(p.getLogType())).collect(Collectors.toList());
        Map<String,Object> map= Maps.newHashMap();
        map.put("push",pushLogList);
        map.put("repeat",repeatLogList);
        map.put("purge",purgeLogList);
        map.put("push_intercept",pushIctLogList);
        map.put("logDate", LocalDate.now().minusDays(day).format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
        map.put("lastPushDate", null==heartBeatInfo.getLastPushDate()?"":heartBeatInfo.getLastPushDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("lastBatchPushDate",null==heartBeatInfo.getLastBatchPushDate()?"":heartBeatInfo.getLastBatchPushDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("lastPurgeDate",null==heartBeatInfo.getLastPurgeDate()?"":heartBeatInfo.getLastPurgeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("lastRptDate",null==heartBeatInfo.getLastRptDate()?"":heartBeatInfo.getLastRptDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("lastMoDate",null==heartBeatInfo.getLastMoDate()?"":heartBeatInfo.getLastMoDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ResponseBo.okWithData("",map);
    }

    @GetMapping("/getPushInfoListPage")
    public ResponseBo getPushInfoListPage(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String sourceCode = request.getParam().get("sourceCode");
        String pushStatus = request.getParam().get("pushStatus");
        String pushDateStr = request.getParam().get("pushDateStr");
        List<PushListInfo> dataList = pushListService.getPushInfoListPage(limit,offset, sourceCode, pushStatus, pushDateStr);
        int count = pushListService.getTotalCount(sourceCode, pushStatus, pushDateStr);
        return ResponseBo.okOverPaging(null, count, dataList);
    }


    /**
     * 重新加载配置
     * @param
     * @return
     */
    @GetMapping("/reloadPushConfig")
    public ResponseBo reloadPushConfig() {
        try {
            configService.loadConfigToRedis();
            return ResponseBo.ok("重新加载推送配置成功!");
        } catch (Exception e) {
            log.error("加载推送配置失败，异常原因为{}",e);
            return ResponseBo.error("重新加载推送配置失败！");
        }
    }


    /**
     * 打印配置信息 (让推送端在控制台打印配置信息)
     * @param
     * @return
     */
    @GetMapping("/noticePrint")
    public ResponseBo printPushProperties() {
        try {
            pushConfig.sendPushSignal(PushSignalEnum.SIGNAL_PRINT,getCurrentUser().getUsername());
            return ResponseBo.ok("发送打印信号成功!");
        } catch (Exception e) {
            log.error("发送刷新信号失败，异常原因为{}",e);
            return ResponseBo.error("发送刷新信号失败！");
        }
    }

    /**
     * 推送推送配置信息 (让推送端到数据库重新加载配置)
     * @param
     * @return
     */
    @GetMapping("/noticeRefresh")
    public ResponseBo noticeRefresh() {
        try {
            pushConfig.sendPushSignal(PushSignalEnum.SIGNAL_REFRESH,getCurrentUser().getUsername());
            return ResponseBo.ok("发送刷新信号成功!");
        } catch (Exception e) {
            log.error("发送刷新信号异常，异常原因为{}",e);
            return ResponseBo.error("发送刷新信号失败！");
        }

    }

    /**
     * 获取推送配置
     * @param
     * @return
     */
    @GetMapping("/getPushConfig")
    public ResponseBo getPushConfig() {
        List<Tconfig> list=configService.selectConfigListFromRedis();

        Map<String,String> result=Maps.newHashMap();

        //对用户名和密码进行处理
        list.stream().forEach(x->{
            if(x.getName().indexOf("Account")!=-1||x.getName().indexOf("Password")!=-1)
            {
                if(!"-".equals(x.getValue()))
                {
                    x.setValue("******");
                }
            }

            if("op.push.pushVendor".equals(x.getName()))
            {
                if("MONTNETS".equals(x.getValue()))
                {
                    result.put("vendorName","梦网云通讯");
                }else  if("CHUANGLAN".equals(x.getValue()))
                {
                    result.put("vendorName","创蓝253");
                }else if("NONE".equals(x.getValue()))
                {
                    result.put("vendorName","无");
                }
            }

        });

        list = list.stream().sorted(Comparator.comparing(Tconfig::getOrderNum, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        return ResponseBo.okWithData(result, list);
    }

    /**
     * 获取推送数据
     * @param day
     * @return
     */
    @GetMapping("/getPushData")
    public ResponseBo getPushData(@RequestParam("day") int day) {
        return ResponseBo.okWithData(null, pushListService.getPushData(day));
    }

    /**
     * 获取上行信息和黑名单信息
     * @param day
     * @return
     */
    @GetMapping("/getRptAndBlackData")
    public ResponseBo getRptAndBlackData(@RequestParam("day") int day) {
        return ResponseBo.okWithData(null, pushListService.getRptAndBlackData(day));
    }

    /**
     * 单条推送测试
     * @return
     */
    @GetMapping("/singleTest")
    public ResponseBo singlePushTest() {
        return ResponseBo.ok();
    }

    /**
     * 批量推送测试
     * @return
     */
    @GetMapping("/batchTest")
    public ResponseBo batchPushTest() {
        return ResponseBo.ok();
    }
}
