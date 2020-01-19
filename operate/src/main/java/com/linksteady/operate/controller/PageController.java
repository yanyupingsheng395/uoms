package com.linksteady.operate.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.ConfigCacheManager;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.ActivityHeadService;
import com.linksteady.operate.service.DailyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author hxcao
 * @date 2019-07-19
 */
@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private ActivityHeadService activityHeadService;

    @Autowired
    private DailyProperties dailyProperties;

    @Log("用户运营监控")
    @RequestMapping("/operator/user")
    public String userOperator() {
        return "operate/useroperator/monitor";
    }

    /**
     * 品类列表
     * @param
     * @return
     */
    @Log("品类运营")
    @RequestMapping("/lifecycle/catlist")
    public String catlist(Model model) {
        return "operate/lifecycle/cat_list";
    }


    /**
     * 日运营列表
     * @param
     * @return
     */
    @Log("每日运营")
    @RequestMapping("/op/day")
    public String opDayList() {
        return "operate/op/opday";
    }

    /**
     * 周期运营列表
     * @param
     * @return
     */
    @Log("周期运营")
    @RequestMapping("/op/period")
    public String opPeriodList() {
        return "operate/op/opperiod";
    }

    @RequestMapping("/daily/task")
    public String daily() {
        return "operate/daily/list";
    }

    @RequestMapping("/daily/config")
    public String dailyGroupConfig(Model model) {
        model.addAttribute("shortUrlLen", dailyProperties.getShortUrlLen());
        model.addAttribute("couponNameLen", dailyProperties.getCouponNameLen());
        model.addAttribute("prodNameLen", dailyProperties.getProdNameLen());
        model.addAttribute("couponSendType", dailyProperties.getCouponSendType());
        model.addAttribute("smsLengthLimit", dailyProperties.getSmsLengthLimit());
        return "operate/daily/config";
    }


    /**
     * 每日运营-任务提交
     * @param model
     * @param headId
     * @return
     */
    @RequestMapping("/daily/edit")
    public String dailyEdit(Model model, @RequestParam("id") String headId) {

        //校验
        if(StringUtils.isEmpty(headId))
        {
            model.addAttribute("errormsg","非法请求，请通过界面进行操作!");
            return "redirect:/page/daily/task";
        }else
        {
            DailyHead dailyHead=dailyService.getDailyHeadById(headId);

            if(null==dailyHead)
            {
                model.addAttribute("errormsg","不存在的任务,请通过界面进行操作!");
                return "redirect:/page/daily/task";
            }

            String currDay=LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMDD"));

            if(!"todo".equals(dailyHead.getStatus()))
            {
                model.addAttribute("errormsg","只有待执行状态的任务才能提交执行!");
                return "redirect:/page/daily/task";
            }

            if(!currDay.equals(dailyHead.getTouchDtStr()))
            {
                model.addAttribute("errormsg","只有当天的任务才能被执行!");
                return "redirect:/page/daily/task";
            }

            //验证成长组是否通过
            if(dailyService.validUserGroup())
            {
                model.addAttribute("errormsg","成长组配置验证未通过!");
                return "redirect:/page/daily/task";
            }

            model.addAttribute("headId", headId);
            model.addAttribute("touchDt", dailyHead.getTouchDtStr());
            model.addAttribute("userNum", dailyHead.getTotalNum());
            return "operate/daily/edit";
        }
    }

    /**
     * 每日运营-用户预览
     * @param model
     * @param headId
     * @return
     */
    @RequestMapping("/daily/userStats")
    public String userStats(Model model, @RequestParam("id") String headId) {
        //校验
        if(StringUtils.isEmpty(headId))
        {
            model.addAttribute("errormsg","非法请求，请通过界面进行操作!");
            return "redirect:/page/daily/task";
        }else
        {
            DailyHead dailyHead=dailyService.getDailyHeadById(headId);

            if(null==dailyHead)
            {
                model.addAttribute("errormsg","不存在的任务,请通过界面进行操作!");
                return "redirect:/page/daily/task";
            }

            String currDay=LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMDD"));

            if(!currDay.equals(dailyHead.getTouchDtStr()))
            {
                model.addAttribute("errormsg","只有当天的任务才能被执行!");
                return "redirect:/page/daily/task";
            }

            model.addAttribute("headId", headId);
            model.addAttribute("touchDt", dailyHead.getTouchDtStr());
            model.addAttribute("userNum", dailyHead.getTotalNum());
            return "operate/daily/userStats";
        }
    }

    /**
     * 活动短信模板配置
     * @param
     * @return
     */
    @Log("活动短信模板列表")
    @RequestMapping("/cfg/activitySmsTemplate")
    public String activitySmsTemplate() {
        return "operate/config/activitySmsTemplate";
    }

    /**
     * 短信模板配置
     * @param
     * @return
     */
    @Log("短信模板列表")
    @RequestMapping("/cfg/smsTemplate")
    public String smsTemplateList(Model model) {
        model.addAttribute("shortUrlLen", dailyProperties.getShortUrlLen());
        model.addAttribute("couponNameLen", dailyProperties.getCouponNameLen());
        model.addAttribute("prodNameLen", dailyProperties.getProdNameLen());
        model.addAttribute("couponSendType", dailyProperties.getCouponSendType());
        model.addAttribute("smsLengthLimit", dailyProperties.getSmsLengthLimit());
        return "operate/config/smstemplate";
    }


    /**
     * 优惠券配置
     * @param
     * @return
     */
    @Log("短信模板列表")
    @RequestMapping("/cfg/coupon")
    public String couponList(Model model) {
        model.addAttribute("validUrl", dailyProperties.getCouponSendType());
        model.addAttribute("couponNameLen", dailyProperties.getCouponNameLen());
        return "operate/config/coupon";
    }

    /**
     * 运营配置
     * @param
     * @return
     */
    @Log("运营配置")
    @RequestMapping("/cfg/dailyConfig")
    public String dailyConfig() {
        return "operate/config/dailyConfig";
    }

    /**
     * 运营配置预览
     * @param
     * @return
     */
    @Log("运营配置预览")
    @RequestMapping("/cfg/dailyConfigView")
    public String dailyConfigView() {
        return "operate/config/dailyConfigView";
    }

    /**
     * 用户运营 - 成长组描述
     * @return
     */
    @RequestMapping("/daily/usergroupdesc")
    public String userStats(Model model,String userValue,String pathActive,String lifecycle) {

        //根据活跃度，设置活跃度按钮
        String activeCode=ConfigCacheManager.getInstance().getConfigMap().get("op.daily.pathactive.list");
        List<String> activeCodeList= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(activeCode);

        List<Map<String,String>> activeResult= Lists.newArrayList();
        Map<String,String> temp;
        for(String code:activeCodeList)
        {
            temp= Maps.newHashMap();
            temp.put("name",ConfigCacheManager.getInstance().getPathActiveMap().get(code));

            if(pathActive.equals(code))
            {
                temp.put("flag","1");
            }else
            {
                temp.put("flag","0");
            }
            activeResult.add(temp);
        }

        List<Map<String,String>> userValueResult= Lists.newArrayList();
        //根据用户价值，设置用户价值按钮
        Map<String,String> userValues=ConfigCacheManager.getInstance().getUserValueMap();
        for (Map.Entry<String, String> entry : userValues.entrySet()) {
            temp= Maps.newHashMap();
            temp.put("name",entry.getValue());

            if(userValue.equals(entry.getKey()))
            {
                temp.put("flag","1");
            }else
            {
                temp.put("flag","0");
            }
            userValueResult.add(temp);
        }

        //根据生命周期，设置生命周期按钮
        List<Map<String,String>> lifecycleResult= Lists.newArrayList();
        Map<String,String> lifecycles=ConfigCacheManager.getInstance().getLifeCycleMap();
        for (Map.Entry<String, String> entry : lifecycles.entrySet()) {
            temp= Maps.newHashMap();
            temp.put("name",entry.getValue());

            if(lifecycle.equals(entry.getKey()))
            {
                temp.put("flag","1");
            }else
            {
                temp.put("flag","0");
            }
            lifecycleResult.add(temp);
        }

        String activeDesc="";
        String activePolicy="";
        //设置活跃度业务理解 及 运营策略
        switch (pathActive)
        {
            case "UAC_01":
                activeDesc="当前到达下一次购买类目的最早合理时间;";
                activePolicy="处在引导提升购买频率有效时机;";
                break;
            case "UAC_02":
                activeDesc="到达下一次购买类目成功率最高的时间点;";
                activePolicy="处在借势培养用户购买最佳时机;";
                break;
            case "UAC_03":
                activeDesc="经过当前时间没有购买，后续再购买较难;";
                activePolicy="处在流失之前刺激购买最后时机;";
                break;
            case "UAC_04":
                activeDesc="流失后，再购买概率相对较高的时间节点;";
                activePolicy="处在流失后尝试挽回的可行时机;";
                break;
            case "UAC_05":
                activeDesc="经过当前时间没有购买，后续不会再购买;";
                activePolicy="处在沉睡之前唤醒用户可行时机;";
                break;
        }

        //设置用户价值 业务理解 及 运营策略
        String valueDesc="";
        String valuePolicy="";
        switch (userValue)
        {
            case "ULC_01":
                valueDesc="在类目消费很多，未来购买力强，价格不敏感;";
                valuePolicy="加强情感关怀，防止用户流失，通常无需补贴;";
                break;
            case "ULC_02":
                valueDesc="在类目消费较多，未来购买力强，价格较敏感;";
                valuePolicy="加强情感关怀，关注用户成长，补贴重点培养;";
                break;
            case "ULC_03":
                valueDesc="在类目消费一般，购买力不确定，价格较敏感;";
                valuePolicy="无需情感关怀，加强用户留存，补贴适度刺激;";
                break;
            case "ULC_04":
                valueDesc="在类目消费较少，未来购买力弱，价格很敏感;";
                valuePolicy="无需情感关怀，减少补贴投入;";
                break;
        }

        //设置生命周期价值 业务理解及运营策略
        String lifecyleDesc="";
        String lifecyclePolicy="";
        switch (lifecycle)
        {
            case "1":
                lifecyleDesc="对类目没有形成忠诚度;";
                lifecyclePolicy="降低门槛刺激复购;";
                break;
            case "0":
                lifecyleDesc="对类目忠诚度开始提升;";
                lifecyclePolicy="递减补贴培养多购;";
                break;
        }

        model.addAttribute("activeResult",activeResult);
        model.addAttribute("userValueResult",userValueResult);
        model.addAttribute("lifecycleResult",lifecycleResult);

        model.addAttribute("activeDesc",activeDesc);
        model.addAttribute("activePolicy",activePolicy);

        model.addAttribute("valueDesc",valueDesc);
        model.addAttribute("valuePolicy",valuePolicy);

        model.addAttribute("lifecyleDesc",lifecyleDesc);
        model.addAttribute("lifecyclePolicy",lifecyclePolicy);

        return "operate/daily/usergroupdesc";
    }

    /**
     * 日运营-效果跟踪
     * @return
     */
    @RequestMapping("daily/effect")
    public String effectTrack(Model model, @RequestParam("id") String headId) {
        String status = dailyService.getDailyHeadById(headId).getStatus();
        if(StringUtils.isNotEmpty(status)) {
            if(status.equals("done") || status.equals("finished") || status.equals("doing")) {
                model.addAttribute("headId", headId);
                DailyHead dailyHead = dailyService.getEffectById(headId);
                if(dailyHead != null) {
                    model.addAttribute("dailyHead", dailyHead);
                }else {
                    return "redirect:/page/daily/task";
                }
                return "operate/daily/effect";
            }
        }
        return "redirect:/page/daily/task";
    }
    /**
     * 活动运营
     * @return
     */
    @RequestMapping("/activity")
    public String activity() {
        return "operate/activity/list";
    }

    @RequestMapping("/activity/add")
    public String activityAdd(Model model)
    {
        model.addAttribute("operateType", "save");
        return "operate/activity/add";
    }

    /**
     * 用户组模板配置表
     * @return
     */
    @RequestMapping("/usergroup")
    public String userGroup() {
        return "operate/daily/usergroup";
    }


    /**
     * 会员日成长任务列表页
     * @return
     */
    @RequestMapping("/member")
    public String memberList() {
        return "operate/member/list";
    }


    /**
     * 会员日成长任务列表页
     *
     * @return
     */
    @RequestMapping("/member/edit")
    public String memberEdit(String id, Model model) {
        model.addAttribute("id", id);
        return "operate/member/edit";
    }

    /**
     * 短信推送列表页
     * @return
     */
    @RequestMapping("/push")
    public String push() {
        return "operate/push/list";
    }

    @RequestMapping("/activity/edit")
    public String activityEdit(@RequestParam("headId") String headId, Model model) {
        ActivityHead activityHead = activityHeadService.findById(headId);
        String preheatStatus = activityHeadService.getStatus(headId, "preheat");
        String formalStatus = activityHeadService.getStatus(headId, "formal");
        model.addAttribute("activityHead", activityHead);
        model.addAttribute("operateType", "update");
        // 当处于done状态的时候，按钮不显示
        model.addAttribute("preheatStatus", preheatStatus);
        model.addAttribute("formalStatus", formalStatus);
        return "operate/activity/add";
    }

    @RequestMapping("/activity/plan")
    public String activityPlan(Model model, @RequestParam String id)
    {
        int count = activityHeadService.getActivityStatus(id);
        if(count == 0) {
            return "redirect:/page/activity";
        }
        model.addAttribute("headId", id);
        return "operate/activity/plan";
    }

    /**
     * 活动运营-效果统计页
     * @return
     */
    @RequestMapping("/activity/effect")
    public String activityEffect(@RequestParam("headId") String headId, Model model) {
        model.addAttribute("headId", headId);
        return "operate/activity/effect";
    }

    /**
     * 用户成长洞察页
     * @return
     */
    @RequestMapping("/insight")
    public String insight() {
        return "operate/insight/insight";
    }

    /**
     * 手动短信推送
     * @return
     */
    @RequestMapping("/manual")
    public String manual(Model model) {
        model.addAttribute("fontNum", dailyProperties.getSmsLengthLimit());
        return "operate/manual/manual";
    }

    /**
     * 单一用户的成长洞察
     */
    @RequestMapping("/personInsight")
    public String personInsight(@RequestParam("userId") String userId, @RequestParam("headId") String headId, Model model) {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        model.addAttribute("userId", userId);
        model.addAttribute("headId", headId);
        model.addAttribute("pathActive", configCacheManager.getConfigMap().get("op.daily.pathactive.list"));
        return "operate/daily/person_insight";
    }
}