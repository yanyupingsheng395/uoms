package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.domain.enums.ActivityGroupEnum;
import com.linksteady.operate.domain.enums.ActivityPlanStatusEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ActivityDetailService;
import com.linksteady.operate.service.ActivityPlanService;
import com.linksteady.operate.vo.ActivityGroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author  huang
 * @date 2020-02-24
 */
@Slf4j
@RestController
@RequestMapping("/activityPlan")
public class ActivityPlanController {

    @Autowired
    private ActivityPlanService activityPlanService;

    @Autowired
    private ActivityDetailService activityDetailService;

    @Autowired
    private PushProperties pushProperties;

    /**
     * 获取任务计划
     * @param headId
     * @return
     */
    @RequestMapping("/getPlanList")
    public ResponseBo getPlanList(@RequestParam Long headId) {
        List<ActivityPlan> planList = activityPlanService.getPlanList(headId);
        return ResponseBo.okWithData(null, planList);
    }

    /**
     * 获取给定日期的群组统计信息
     * @param planId
     * @return
     */
    @GetMapping("/getUserGroupList")
    public List<ActivityGroupVO> getUserGroupList(@RequestParam Long planId) {
        ActivityGroupVO vo=null;
        List<ActivityGroupVO> result=Lists.newArrayList();
        int sum=0;

        ActivityPlan activityPlan=activityPlanService.getPlanInfo(planId);

        if(null==activityPlan)
        {
           return result;
        }
        List<Map<String,Object>> activitySummaryList = activityPlanService.getUserGroupList(planId);

        for(Map<String,Object> temp:activitySummaryList)
        {
            vo=new ActivityGroupVO();
            vo.setGroupId((String)temp.get("GROUP_ID"));
            vo.setGroupName((String)temp.get("GROUP_NAME"));
            vo.setProdActivityProp((String)temp.get("PROD_ACTIVITY_PROP"));
            vo.setPlanId(String.valueOf(temp.get("PLAN_ID")));
            sum+=((BigDecimal)temp.get("USER_NUM")).intValue();
            vo.setUserNum(((BigDecimal)temp.get("USER_NUM")).intValue());

            result.add(vo);
        }

        vo=new ActivityGroupVO();
        vo.setGroupId("-1");
        vo.setGroupName("合计");
        vo.setUserNum(sum);
        result.add(vo);

        return result;
    }

    /**
     * 获取推送的明细数据
     * @param request
     * @return
     */
    @GetMapping("/getDetailPage")
    public ResponseBo getDetailPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String planId = request.getParam().get("planId");
        String groupId=request.getParam().get("groupId");
        int count = activityDetailService.getDataCount(Long.parseLong(planId),groupId);
        List<ActivityDetail>  dataList = activityDetailService.getPageList(start, end, Long.parseLong(planId),groupId);

        for(ActivityDetail activityDetail:dataList)
        {
            activityDetail.setGroupName(ActivityGroupEnum.getGroupNameById(activityDetail.getGroupId()));
        }
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 开始执行计划
     * @return
     */
    @PostMapping("/startPush")
    public  ResponseBo startPush(@RequestParam Long planId,@RequestParam String pushMethod, @RequestParam String pushPeriod) {
        if(StringUtils.isEmpty(planId)||StringUtils.isEmpty(pushMethod))
        {
            return ResponseBo.error("非法参数，请通过系统界面进行操作！");
        }

        ActivityPlan activityPlan = activityPlanService.getPlanInfo(planId);

        if(null==activityPlan)
        {
            return ResponseBo.error("不存在的活动执行计划，请通过系统界面进行操作！");
        }

        //仅有 待执行状态能进行推送
        if(!ActivityPlanStatusEnum.WAIT_EXEC.getStatusCode().equalsIgnoreCase(activityPlan.getPlanStatus())) {
            return ResponseBo.error("计划状态已改变，请在列表界面刷新后重新操作！");
        }
        //进行推送的操作
        try {
             activityPlanService.pushActivity(pushMethod,pushPeriod, activityPlan);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("活动运营推送失败，异常堆栈为{}",e);
            if(e instanceof LinkSteadyException)
            {
                return ResponseBo.error(e.getMessage());
            }else
            {
                return ResponseBo.error("推送失败,请联系系统运维人员!");
            }
        }
    }

    /**
     * 转化推送明细表的文案
     * @param planId
     * @return
     */
    @GetMapping("/transActivityDetail")
    public ResponseBo transActivityDetail(@RequestParam Long planId) {
        String msg="";
        //校验
        if(StringUtils.isEmpty(planId))
        {
            msg="非法请求，请通过界面进行操作!";
            return ResponseBo.error(msg);
        }else
        {
            ActivityPlan activityPlan=activityPlanService.getPlanInfo(planId);

            if(null==activityPlan)
            {
                msg="非法请求，请通过界面进行操作!";
                return ResponseBo.error(msg);
            }

            if(!ActivityPlanStatusEnum.WAIT_EXEC.getStatusCode().equals(activityPlan.getPlanStatus()))
            {
                msg="计划状态已改变，请刷新数据后重新操作!";
                return ResponseBo.error(msg);
            }

            String result=activityPlanService.transActivityDetail(activityPlan);

            if("1".equals(result))
            {
                return ResponseBo.ok("转化文案成功!");
            }else if("2".equals(result))
            {
                return ResponseBo.error("其他用户正在操作，请稍后再试!");
            }else
            {  //0失败
                return ResponseBo.error("转化文案失败，请联系系统运维人员！");
            }

        }
    }

    /**
     * 获取默认推送方式和定时推送时间
     *
     * @return
     */
    @GetMapping("/getDefaultPushInfo")
    public ResponseBo getPushInfo() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("method", pushProperties.getPushMethod());
        int hour = LocalTime.now().getHour();
        final List<String> timeList = IntStream.rangeClosed(8, 22).filter(x -> x > hour).boxed().map(y -> {
            if (y < 10) {
                return "0" + y + ":00";
            }
            return y + ":00";
        }).collect(Collectors.toList());
        result.put("timeList", timeList);
        return ResponseBo.okWithData(null, result);
    }
}
