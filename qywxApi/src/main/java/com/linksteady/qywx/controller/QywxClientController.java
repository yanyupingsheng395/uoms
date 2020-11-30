package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/qwClient")
public class QywxClientController {

    @Autowired
    QywxService qywxService;
    @Autowired
    private ExternalContactService externalContactService;

    @Autowired
    private GuideResultService guideResultService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    MediaService mediaService;

    /**
     * 请求首页
     *
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "qywxClient/qywxindex";
    }

    /**
     * 请求首页
     *
     * @return
     */
    @RequestMapping("/main")
    public String main() {
        return "qywxClient/main";
    }
    /**
     * 导购运营引导
     *
     * @return
     */
    @RequestMapping("/guidance")
    public String guidance( HttpServletRequest request) {
        return "qywxClient/guidance/list";
    }

    /**
     * 导购结果
     *
     * @return
     */
    @RequestMapping("/guideResult")
    public String userManager() {
        return "qywxClient/guideResult/main";
    }

    /**
     * 导购发圈建议
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/friendCirlceGuide")
    public String friendCirlceGuide(Model model, HttpServletRequest request) {
        return "qywxClient/friends/list";
    }

    /**
     * 导购关系引导(根据给定的条件获取对应的用户)
     */
    @RequestMapping("/getGuidanceList")
    @ResponseBody
    public ResponseBo getGuidanceList(@RequestParam("relation") String relation,
                                      @RequestParam("loss") String loss,
                                      @RequestParam("stagevalue") String stagevalue,
                                      @RequestParam("interval") String interval,
                                      Integer limit,Integer offset)
    {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String followUserId=userBo.getUsername();
        int count=externalContactService.getQywxGuidanceCount(followUserId, relation, loss, stagevalue, interval);
        List<ExternalContact> list = externalContactService.getQywxGuidanceList(followUserId, relation, loss, stagevalue, interval,offset,limit);
        return ResponseBo.okOverPaging(null, count, list);
    }

    /**
     *导购关系引导中未购买客户
     * 按添加时间条件查询
     */
    @RequestMapping("/getAddTimeList")
    @ResponseBody
    public ResponseBo getAddTimeList(@RequestParam("addtime") String addtime,Integer limit,Integer offset){
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String followUserId =userBo.getUsername();
        int count=externalContactService.getgetAddTimeCount(followUserId);
        List<ExternalContact> list = externalContactService.getAddTimeList(followUserId, addtime,offset,limit);
        return ResponseBo.okOverPaging(null, count, list);
    }

    @GetMapping("/getGuideResultData")
    @ResponseBody
    public ResponseBo getGuideResultData(@RequestParam("during") String during, String startDt, String endDt) throws URISyntaxException {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if (null != userBo) {
            Long userId = userBo.getUserId();
            if(during.equalsIgnoreCase("0")) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.plusDays(-1);
                startDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                endDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            if(during.equalsIgnoreCase("1")) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.plusDays(-8);
                LocalDate end = now.plusDays(-1);
                startDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                endDt = end.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            GuideResult resultData = guideResultService.getResultData(userId + "", startDt, endDt);
            resultData.getUserTotalCnt();
            log.info("获取到的结果：" + resultData);
            return ResponseBo.okWithData(null,resultData);
        } else {
            throw new IllegalArgumentException("未获取到登录会话！");
        }
    }

    /**
     * 获取外部联系人的名称和其在用户成长系统的用户ID
     * @param externalUserId
     * @return
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public ResponseBo getUserInfo( @RequestParam("externalUserId") String externalUserId)
    {
        Map<String, String> result = Maps.newHashMap();
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String followUserId = userBo.getUsername();
        ExternalContact userInfo = externalContactService.getUserInfo(followUserId, externalUserId);
        result.put("userId", userInfo.getOperateUserId());
        result.put("userName", userInfo.getName());
        return ResponseBo.okWithData(null, result);
    }

    /**
     * 获取用户购买历史数据
     * @return
     */
    @RequestMapping("/getUserBuyHistory")
    public ResponseBo getUserBuyHistory(@RequestParam String externalUserId, @RequestParam Long spuId) {
        try {
            UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
            //判断当前用户是否已经匹配到商城
            String followUserId = userBo.getUsername();
            ExternalContact externalContact = externalContactService.getUserInfo(followUserId, externalUserId);
            if(externalContact==null){
                return null;
            }
            if(!"Y".equals(externalContact.getMappingFlag())){
                return null;
            }
            String userId=externalContact.getOperateUserId();
            Map<String,Object> result= Maps.newHashMap();

            //用户的购买统计数据
            Map<String,String> userStats;
            String spuName="";
            String userValue="";
            String lifeCycle="";

            //整体
            if(null==spuId||spuId==-1l){
                userStats=userTaskService.getUserStatis(userId);
            }else{
                //用户在类目上的价值敏感度
                userValue=userTaskService.getUserValue(userId,spuId);
                //用户在类目上的生命周期阶段
                lifeCycle=userTaskService.getLifeCycle(userId,spuId);
                //类目名称
                spuName=userTaskService.getSpuName(spuId);

                userStats=userTaskService.getUserStatis(userId,spuId,spuName);
            }

            //用户涉及的类目
            List<SpuInfo> spuList=userTaskService.getSpuList(userId);
            //用户购买历史
            List<UserBuyHistory> userBuyHistoryList=userTaskService.getUserBuyHistory(userId,spuId);
            //构造返回数据
            result.put("spuList",spuList);
            result.put("userStats",userStats);
            result.put("userBuyHistoryList",userBuyHistoryList);

            ResponseBo responseBo=ResponseBo.okWithData(null, result);
            //用户在商城的首购时间
            responseBo.put("firstBuyDate",userTaskService.getFirstBuyDate(userId));
            responseBo.put("spuName",spuName);
            responseBo.put("userValue",userValue);
            responseBo.put("lifeCycle",lifeCycle);

            //描述信息
            responseBo.put("userDesc", getDescInfo(externalContact,spuId,
                    (String)responseBo.get("firstBuyDate"),
                    (String)responseBo.get("spuName"),
                    (String)responseBo.get("userValue"),
                    (String)responseBo.get("lifeCycle")));
            return responseBo;
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    private String getDescInfo(ExternalContact externalContact,Long spuId,String firstBuyDate,String spuName,String userValue,String lifecycle)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
        StringBuffer desc=new StringBuffer();
        //整体
        if(null==spuId||spuId==-1)
        {
            //用户X于X年X月X日添加为企业微信好友；于X年X月X日在商城首次购买；截止到X年X月X日；
            desc.append("用户%user%于");
            desc.append(sdf.format(new Date(Long.parseLong(externalContact.getCreatetime())*1000)));
            desc.append("添加为企业微信好友;于");
            desc.append(firstBuyDate);
            desc.append("在商城首次购买；截止到");
            desc.append(sdf.format(new Date()));
            desc.append(":");

            return desc.toString();
        }else
        {
            //某个spu下
            //用户X在类目H上属于高价值低敏感用户——见下表 用户X在类目H上处于生命周期阶段的成长期——见下表
            desc.append("用户%user%在类目");
            desc.append(spuName);
            desc.append("上属于");
            desc.append((StringUtils.isEmpty(userValue)||"null".equalsIgnoreCase(userValue))?"未知":userValue);
            desc.append("用户;");

            desc.append("在类目");
            desc.append(spuName);
            desc.append("上处于生命周期阶段的");
            desc.append((StringUtils.isEmpty(lifecycle)||"null".equalsIgnoreCase(lifecycle))?"未知":lifecycle);
            desc.append("，见下表：");

            return desc.toString();
        }
    }


    @RequestMapping("/getUserData")
    public Map<String, Object> getUserData(@RequestParam String userId, @RequestParam String productId) throws URISyntaxException {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(productId)) {
            if (null != userBo) {
                return userTaskService.getUserData(userId, productId);
            } else {
                throw new IllegalArgumentException("未获取到登录会话！");
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/getProductData")
    public Map<String, Object> getProductData(@RequestParam String userId) {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNotEmpty(userId)) {
            if (null != userBo) {
                return ResponseBo.okWithData(null, userTaskService.getProductData(userId));
            } else {
                throw new IllegalArgumentException("未获取到登录会话！");
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/getMediaId")
    public Map<String, Object> getMediaId(@RequestParam Long identityId,@RequestParam String identityType, HttpServletRequest request) throws URISyntaxException {
        UserBo user =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if(user==null){
            throw new IllegalArgumentException("未获取到登录会话！");
        }
        if (user.getUserId()==0) {
            return ResponseBo.okWithData(null,mediaService.getMpMediaId(identityType,identityId));
        } else {
            return null;
        }
    }


    /**
     * 获取当前用户的朋友圈方案
     */
    @RequestMapping("/guideList")
    @ResponseBody
    public Map<String, Object> guideList() {
        //获取当前用户及公司
        //获取当前页数、每页数量
        int count=5;
        List<FriendsGuide> list= Lists.newArrayList();
        list.add(new FriendsGuide("20200911","20200911-01","阿瓦提长绒棉浴巾,纯棉超柔婴儿浴巾","浴巾",""));
        list.add(new FriendsGuide("20200911","20200911-02","华夫格多功能毯","休闲毯",""));
        list.add(new FriendsGuide("20200911","20200911-03","老粗布条格凉席三件套,天然蔺草席三件套","凉席",""));
        list.add(new FriendsGuide("20200912","20200912-01","30支精梳埃及浴巾,SPIMA棉条纹浴巾","浴巾",""));
        list.add(new FriendsGuide("20200912","20200912-02","三层纱蓬松褶皱毛巾被","休闲毯",""));
        return ResponseBo.okOverPaging(null, count, list);
    }

    /**
     * 生成朋友圈方案
     */
    @RequestMapping("/friend/generatePolicy")
    @ResponseBody
    public ResponseBo guideList(@RequestParam(name = "pushInterval") int pushInterval,
                                @RequestParam(name = "pushNum") int pushNum,
                                @RequestParam(name = "productNum") int productNum,
                                @RequestParam(name = "periodNum") int periodNum,
                                @RequestParam(name = "pushStartDt") String pushStartDt,
                                HttpServletRequest httpServletRequest) {
        UserBo user =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if(pushInterval>3||pushInterval<1)
        {
            return ResponseBo.error("商品朋友圈推送频率只能介于1-3之间!");
        }

        if(productNum!=1)
        {
            return ResponseBo.error("每条朋友圈体现商品数只能为1!");
        }


        if(pushNum>15||pushNum<1)
        {
            return ResponseBo.error("每天可发送商品朋友圈数只能介于1-15!");
        }

        if(periodNum>3||periodNum<1)
        {
            return ResponseBo.error("生成方案的周期数只能介于1-3之间!");
        }

        LocalDate pushStartDate=null;
        try {
            //判断日期参数
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            pushStartDate = LocalDate.parse(pushStartDt, formatter);
        } catch (Exception e) {
            return ResponseBo.error("商品朋友圈开始推送时间格式不正确!");
        }

        if(pushStartDate.isAfter(LocalDate.now()))
        {
            return ResponseBo.error("商品朋友圈开始推送时间不能早于当前日期!");
        }

        log.info("朋友圈发圈接收到的参数为{}-{}-{}-{}-{}",pushInterval,pushNum,productNum,periodNum,pushStartDt);
        //发送到用户成长系统
        //推送到用户成长端
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String corpId=qywxService.getCorpId();

        Map<String,String> param= Maps.newHashMap();
        param.put("timestamp", timestamp);
        param.put("corpId",corpId);
        param.put("pushInterval",String.valueOf(pushInterval));
        param.put("pushNum",String.valueOf(pushNum));
        param.put("productNum",String.valueOf(productNum));
        param.put("periodNum",String.valueOf(periodNum));
        param.put("pushStartDt",pushStartDt);
        param.put("followUserId",user.getUsername());
        param.put("signature", SHA1.gen(timestamp,corpId,
                String.valueOf(pushInterval),
                String.valueOf(pushNum),
                String.valueOf(productNum),
                String.valueOf(periodNum),
                pushStartDt,
                user.getUsername()
        ));

        //推送到用户成长端
        //String result= OkHttpUtil.postRequestByFormBody(qywxService.getOperateUrl(corpId)+ OperateConsts.GENERATE_FRIEND_POLICY,param);
      String result="";
        JSONObject jsonObject = JSON.parseObject(result);

        if(null==jsonObject||200!=jsonObject.getIntValue("code"))
        {
            return ResponseBo.ok();
        }else
        {
            return ResponseBo.error(jsonObject.getString("msg"));
        }
    }

    /**
     * 获取jsapi相关的配置信息
     */
    @RequestMapping("/getJsapiInfo")
    @ResponseBody
    public ResponseBo getJsapiInfo(HttpServletRequest httpServletRequest)
    {
        Map<String,String> result= null;
        try {
            String corpId=qywxService.getCorpId();
            //获取当前应用的agentId
            String agentId=qywxService.getAgentId();
            String jsapiTicket=qywxService.getJsapiTicket();

            String timestamp = Long.toString(System.currentTimeMillis() / 1000);
            String nonceStr=getNoncestr();
            String url=httpServletRequest.getParameter("url");

            String content="jsapi_ticket="+jsapiTicket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
            String signature= SHA1.gen(content);

            result = Maps.newHashMap();
            result.put("nonceStr",nonceStr);
            result.put("timestamp",timestamp);
            result.put("corpId",corpId);
            result.put("signature",signature);

            //获取应用的ticket
             String agentTicket=qywxService.getAgentJsapiTicket();

            String agentContent="jsapi_ticket="+agentTicket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
            String agentSignature=SHA1.gen(agentContent);

            result.put("agentId",agentId);
            result.put("agentSignature",agentSignature);
            return ResponseBo.okWithData("",result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error();
        }
    }

    private String getNoncestr() {
        return UUID.randomUUID().toString();
    }

}
