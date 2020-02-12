//package com.linksteady.operate.task;
//
//import com.linksteady.jobclient.annotation.JobHandler;
//import com.linksteady.jobclient.domain.ResultInfo;
//import com.linksteady.jobclient.service.IJobHandler;
//import com.linksteady.operate.service.DailyService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * 用户群组每日验证
// *
// * @author hxcao
// * @date 2020/1/2
// */
//@Slf4j
//@Component
//@JobHandler(value = "dailyUserGroupValid")
//public class DailyUserGroupValid extends IJobHandler{
//
//    @Autowired
//    private DailyService dailyService;
//
//    @Override
//    public ResultInfo execute(String param) throws Exception {
//        dailyService.validUserGroup();
//        return ResultInfo.success("success");
//    }
//}
