package com.linksteady.qywx.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.service.*;
import com.linksteady.qywx.vo.GuideResultPurchInfoVO;
import com.linksteady.qywx.vo.GuideResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
     * 聊天工具栏页
     *
     * @return
     */
    @RequestMapping("/guideAssist/main")
    public String guideAssist() {
        return "qywxClient/guideAssist/main";
    }

    /**
     * 导购结果
     *
     * @return
     */
    @RequestMapping("/userManager")
    public String userManager() {
        return "qywxClient/guideResult/main";
    }

    /**
     * 导购发圈建议
     *
     * @return
     */
    @RequestMapping("/friendCirlceGuide")
    public String friendCirlceGuide() {
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

    /**
     * 获取导购运营结果
     * @param during
     * @param startDt
     * @param endDt
     * @return
     */
    @GetMapping("/getGuideResultData")
    @ResponseBody
    public ResponseBo getGuideResultData(@RequestParam("during") String during, String startDt, String endDt) {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if (null != userBo) {
            String followUserId = userBo.getUsername();
            //累计
            if(during.equalsIgnoreCase("-1")) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.plusDays(-1);
                startDt = guideResultService.getMinDayWid();
                endDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            //昨日
            if(during.equalsIgnoreCase("0")) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.plusDays(-1);
                startDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                endDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            //最近7日
            if(during.equalsIgnoreCase("1")) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.plusDays(-8);
                LocalDate end = now.plusDays(-1);
                startDt = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                endDt = end.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            log.info("导购运行结果，参数为{},{},{},{}",followUserId,during,startDt,endDt);
            //查询当前企业微信的总用户数
            int totalCnt=guideResultService.getTotalCnt(followUserId,Integer.parseInt(endDt));
            //查询当前企业微信在给定时间段内的添加用户数
            int addCnt=guideResultService.getAddCnt(followUserId,Integer.parseInt(startDt),Integer.parseInt(endDt));

            //获取推送转化数据
            GuideResult resultData = guideResultService.getResultData(followUserId, Integer.parseInt(startDt), Integer.parseInt(endDt));

            if(resultData==null)
            {
                resultData=new GuideResult();
            }
            //查询订单总金额
            double TotalOrderAmount=guideResultService.getTotalOrderAmount(Integer.parseInt(startDt),Integer.parseInt(endDt));

            //观测期购买用户数和购买金额、订单总金额
            GuideResultPurchInfoVO guideResultPurchInfoVO=guideResultService.getPurchInfo(followUserId,Integer.parseInt(startDt),Integer.parseInt(endDt));

            //构造返回数据
            GuideResultVO guideResultVO=new GuideResultVO();
            //总用户数
            guideResultVO.setTotalCnt(totalCnt);
            //添加用户数
            guideResultVO.setAddCnt(addCnt);
            //购买用户数
            guideResultVO.setPurchCnt(guideResultPurchInfoVO.getPurchCnt());
            //用户购买率
            double purchRate=ArithUtil.formatDoubleByMode(totalCnt==0?0d:guideResultPurchInfoVO.getPurchCnt()*1.00/totalCnt*100,2, RoundingMode.HALF_UP);
            guideResultVO.setPurchRate(purchRate);
            //购买金额
            guideResultVO.setTotalAmount(guideResultPurchInfoVO.getPurchAmount());

            guideResultVO.setReceiveMsg(resultData.getReceiveMsg());
            guideResultVO.setPushMsg(resultData.getPushMsg());

            double executeRate=ArithUtil.formatDoubleByMode(resultData.getReceiveMsg()==0?0d: resultData.getPushMsg()*1.00/resultData.getReceiveMsg()*100,2, RoundingMode.HALF_UP);
            //消息执行率
            guideResultVO.setPushExecuteRate(executeRate);
            guideResultVO.setReceiveUserCnt(resultData.getReceiveUserCnt());
            guideResultVO.setPushUserCnt(resultData.getPushUserCnt());
            guideResultVO.setPushCovCnt(resultData.getPushCovCnt());
            //推送转化率
            double covRate=ArithUtil.formatDoubleByMode(resultData.getPushUserCnt()==0?0d: resultData.getPushCovCnt()*1.00/resultData.getPushUserCnt()*100,2, RoundingMode.HALF_UP);
            guideResultVO.setPushCovRate(covRate);

            guideResultVO.setPushTotalAmount(resultData.getPushTotalAmount());
            guideResultVO.setOrderAmount(TotalOrderAmount);
            //占总体
            double pushAmountPct=ArithUtil.formatDoubleByMode(TotalOrderAmount==0?0d: resultData.getPushTotalAmount()*1.00/TotalOrderAmount*100,2, RoundingMode.HALF_UP);
            guideResultVO.setPushAmountPct(pushAmountPct);

            return ResponseBo.okWithData(null,guideResultVO);
        } else {
            throw new IllegalArgumentException("未获取到登录会话！");
        }
    }

    /**
     * 获取外部联系人的信息
     * @param externalUserId
     * @return
     */
    @RequestMapping("/getExternalUserInfo")
    @ResponseBody
    public ResponseBo getExternalUserInfo( @RequestParam("externalUserId") String externalUserId)
    {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        String followUserId = userBo.getUsername();
        ExternalContact userInfo = externalContactService.selectExternalUser(followUserId, externalUserId);

        if(userInfo!=null)
        {
            if(null==userInfo.getOperateUserId())
            {
                userInfo.setOperateUserId(-999L);
            }
        }
        return ResponseBo.okWithData(null, userInfo);
    }


    /**
     * 获取用户购买历史数据
     * @return
     */
    @RequestMapping("/getUserBuyHistory")
    @ResponseBody
    public ResponseBo getUserBuyHistory(@RequestParam String externalUserId, @RequestParam Long spuId) {
        try {
            UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
            //判断当前用户是否已经匹配到商城
            String followUserId = userBo.getUsername();
            ExternalContact externalContact = externalContactService.selectExternalUser(followUserId, externalUserId);
            if(externalContact==null||null==externalContact.getOperateUserId()){
                return ResponseBo.okWithData("N",null);
            }

            Long userId=externalContact.getOperateUserId();
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

            //msg用来标记是否匹配到商城
            ResponseBo responseBo=ResponseBo.okWithData("Y", result);
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

    /**
     * 构造用户的描述信息
     * @param externalContact
     * @param spuId
     * @param firstBuyDate
     * @param spuName
     * @param userValue
     * @param lifecycle
     * @return
     */
    private String getDescInfo(ExternalContact externalContact,Long spuId,String firstBuyDate,String spuName,String userValue,String lifecycle)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
        StringBuffer desc=new StringBuffer();
        //整体
        if(null==spuId||spuId==-1)
        {
            //用户X于X年X月X日添加为企业微信好友；于X年X月X日在商城首次购买；截止到X年X月X日；
            desc.append("用户").append(externalContact.getName()).append("于");
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


    /**
     * 获取用户更详细的指引信息
     * @param operateUserId
     * @param productId
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping("/getUserGuideInfo")
    @ResponseBody
    public Map<String, Object> getUserGuideInfo(@RequestParam String operateUserId, @RequestParam String productId) throws URISyntaxException {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNotEmpty(operateUserId) && StringUtils.isNotEmpty(productId)) {
            if (null != userBo) {
                Map<String, Object> info = userTaskService.getUserGuideInfo(operateUserId, productId);
                info.put("code",200);
                return info;
            } else {
                throw new IllegalArgumentException("未获取到登录会话！");
            }
        } else {
            return null;
        }
    }

    /**
     * 获取引导用户购买的商品列表
     * @param operateUserId
     * @return
     */
    @RequestMapping("/getRecProductList")
    @ResponseBody
    public Map<String, Object> getRecProductList(@RequestParam String operateUserId) {
        UserBo userBo =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNotEmpty(operateUserId)) {
            if (null != userBo) {
                List<Map<String, Object>> list = userTaskService.getRecProductList(operateUserId);
                return ResponseBo.okWithData(null, list);
            } else {
                throw new IllegalArgumentException("未获取到登录会话！");
            }
        } else {
            return null;
        }
    }

    /**
     * 获取临时素材的mediaId
     * @param identityId
     * @param identityType
     * @param request
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping("/getMediaId")
    public ResponseBo getMediaId(@RequestParam Long identityId,@RequestParam String identityType, HttpServletRequest request) throws URISyntaxException {
        UserBo user =(UserBo) SecurityUtils.getSubject().getPrincipal();
        if(user==null){
            throw new IllegalArgumentException("未获取到登录会话！");
        }
        try {
            return ResponseBo.okWithData(null,mediaService.getMpMediaId(identityType,identityId));
        } catch (Exception e) {
            log.error("获取临时素材ID错误，原因为{}",e);
            return ResponseBo.error("获取临时素材ID错误");
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
        int count=0;
        List<FriendsGuide> list= Lists.newArrayList();
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
                                @RequestParam(name = "pushStartDt") String pushStartDt) {
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

        return ResponseBo.ok();
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
            log.error("获取jsapi_ticket异常，原因为{}",e);
            return ResponseBo.error();
        }
    }

    private String getNoncestr() {
        return UUID.randomUUID().toString();
    }

}
