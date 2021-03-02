package com.linksteady.operate.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.common.config.SystemProperties;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.exception.PushQywxMessageException;
import com.linksteady.operate.exception.SendCouponException;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

/**
 * 每日运营(企业微信)
 *
 * @author huang
 * @date 2020-05-12
 */
@Slf4j
@RestController
@RequestMapping("/qywxDaily")
public class QywxDailyController {

    @Autowired
    private QywxDailyService qywxDailyService;

    @Autowired
    private QywxDailyDetailService qywxDailyDetailService;

    @Autowired
    private PushConfig pushConfig;

    @Autowired
    private ConfigService configService;

    @Autowired
    private QywxDailyConfigService dailyConfigService;

    @Autowired
    private QywxMessageService qywxMessageService;

    @Autowired
    private QywxDailyCouponService qywxDailyCouponService;

    @Autowired
    QywxMdiaService qywxMdiaService;

    @Autowired
    QywxSendCouponService qywxSendCouponService;

    @Autowired
    private SystemProperties systemProperties;

    /**
     * 获取每日成长任务分页列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getHeadList")
    public ResponseBo getHeadList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String taskDate = request.getParam().get("taskDate");
        int count = qywxDailyService.getTotalCount(taskDate);
        List<QywxDailyHeader> dailyInfos = qywxDailyService.getHeadList(limit, offset, taskDate);

        // 先校验一遍补贴数据
        qywxDailyCouponService.validCoupon();
        //设置当前天记录的 校验状态
        Map<String, Object> validMap = dailyConfigService.validUserGroupForQywx();

        dailyInfos.stream().forEach(p -> {
            p.setValidateLabel(validMap.get("flag").toString());
            p.setCheckDesc(validMap.get("desc").toString());
        });

        return ResponseBo.okOverPaging(validMap.get("flag").toString(), count, dailyInfos);
    }

    /**
     * 获取用户预览数据
     *
     * @return
     */
    @GetMapping("/getTaskOverViewData")
    public ResponseBo getTaskOverViewData(Long headId) {
        //判断如果是演示环境，直接返回ok
        if(systemProperties.isDemoEnvironment())
        {
            return ResponseBo.ok();
        }

        // 首先判断状态和任务日期 如果任务是待执行及当天的任务，则执行文案、优惠券匹配
        QywxDailyHeader qywxDailyHeader = qywxDailyService.getHeadInfo(headId);
        if (null == qywxDailyHeader) {
            return ResponseBo.error("不存在的每日运营计划!");
        }

        String currentDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        if (qywxDailyHeader.getTotalNum() == 0) {
            return ResponseBo.error("当前计划没有待运营的用户！");
        }
        //将乐观锁版本号传入前端，后续逻辑带上版本号，防止并发操作
        int version = qywxDailyService.getHeadInfo(headId).getVersion();
        //待执行状态且是当天的任务
        if ("todo".equals(qywxDailyHeader.getStatus()) && currentDay.equals(qywxDailyHeader.getTaskDateStr())) {
            //如果已经进行了优惠券的发放
            if ("Y".equals(qywxDailyHeader.getCouponSendFlag())) {
                //直接返回
                return ResponseBo.okWithData(null,version);
            }

            //验证配置是否通过校验
            Map<String, Object> validMap = dailyConfigService.validUserGroupForQywx();
            if (validMap.get("flag").equals("未通过")) {
                return ResponseBo.error("成长组尚未完成配置，请先进行配置!");
            } else {
                //首先获取锁
                if (qywxDailyService.getTransContentLock(String.valueOf(headId))) {
                    try {
                        qywxDailyDetailService.generate(headId);
                        return ResponseBo.okWithData(null,version);
                    } catch (Exception e) {
                        log.error("每日运营[微信]转化生成文案错误，异常堆栈为{}", e);
                        return ResponseBo.error("每日运营[微信]生成文案错误，请联系系统运维人员！");
                    } finally {
                        //释放锁
                        qywxDailyService.delTransLock();
                    }
                } else {
                    return ResponseBo.error("其他用户正在生成文案，请稍后再操作！");
                }
            }
        } else {
            //直接返回
            return ResponseBo.okWithData(null,version);
        }
    }

    /**
     * 获取用户明细的列表
     *
     * @return
     */
    @GetMapping("/getDetailList")
    public ResponseBo getDetailList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long headId = Long.parseLong(request.getParam().get("headId"));
        String followUserId = request.getParam().get("followUserId");
        String prodId = request.getParam().get("recProdId");
        long recProdId =0l;
        if(!StringUtils.isEmpty(prodId)){
            recProdId =Long.parseLong( prodId);
        }
        List<QywxDailyDetail> dataList = qywxDailyDetailService.getQywxDetailList(headId, limit, offset, followUserId,recProdId);
        int count = qywxDailyDetailService.getQywxDetailCount(headId, followUserId,recProdId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @GetMapping("/getTaskDt")
    public ResponseBo getTaskDt(@RequestParam("headId") Long headId) {
        Date taskDate = qywxDailyService.getHeadInfo(headId).getTaskDate();
        return ResponseBo.okWithData(null, new SimpleDateFormat("yyyyMMdd").format(taskDate));
    }


    /**
     * 启动群组推送
     *
     * @return
     */
    @GetMapping("/submitTask")
    public synchronized ResponseBo submitTask(Long headId, Long effectDays,int version) {
        //判断如果是演示环境，推送测试文案
        if(systemProperties.isDemoEnvironment())
        {
            try {
                String title="测试活动商品";
                String messageTest="哈喽，上次购买的东西还满意吗？40元专属券已放入您的账户，别忘记来小程序使用呀~";
                String mpUrl=" /pages/about/about";
                String senderId="brandonz";
                String externalContact="wmXfFiDwAAIoOS6g8UB2tHo2pZKT0zfQ,wmXfFiDwAArXVAgKadY0lv9LZ3FISz8w";
                String mediaId = qywxMdiaService.getMpMediaId("100");
                testPush(title,
                        mpUrl,
                        mediaId,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "applets",
                        senderId,
                        externalContact,
                        messageTest);
                return ResponseBo.ok();
            } catch (Exception e) {
                return ResponseBo.error("推送错误，原因为:"+e.getMessage());
            }
        }
        else
        {
            if (null == effectDays || effectDays < 1 || effectDays > 10) {
                return ResponseBo.error("参数错误，请通过系统界面进行操作！");
            }
            //进行一次状态的判断
            QywxDailyHeader qywxDailyHeader = qywxDailyService.getHeadInfo(headId);
            //进行一次时间的判断 (调度修改状态有一定的延迟)
            if (!DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()).equals(qywxDailyHeader.getTaskDateStr())) {
                return ResponseBo.error("已过期的任务无法再执行!");
            }

            if (null == qywxDailyHeader || !qywxDailyHeader.getStatus().equalsIgnoreCase("todo")) {
                return ResponseBo.error("当前任务非待执行状态，请返回刷新后重试！");
            }

            String validateLabel = (String) dailyConfigService.validUserGroupForQywx().get("flag");
            if (validateLabel.equalsIgnoreCase("未通过")) {
                return ResponseBo.error("成长组配置验证未通过！");
            }

            try {
                qywxDailyService.push(qywxDailyHeader, effectDays,version);

                //获取是否有推送失败的情况
                int count = qywxDailyService.getPushErrorCount(headId);
                if (count > 0) {
                    throw new PushQywxMessageException("推送企业微信消息存在错误");
                }else
                {
                    return ResponseBo.okWithData(null,"");
                }
            } catch (Exception e) {
                log.error("企业微信每日运营推送错误，错误堆栈为", e);
                if (e instanceof OptimisticLockException) {
                    return ResponseBo.error(e.getMessage());
                } else if (e instanceof SendCouponException) {
                    //标记
                    qywxDailyService.updateStatusToDoneCouponError(headId);
                    return ResponseBo.error("发送优惠券失败，请联系系统运维人员!");
                } else if (e instanceof PushQywxMessageException) {
                    //标记
                    qywxDailyService.updateStatusToDonePushError(headId);
                    return ResponseBo.error("发送企业微信消息失败，请联系系统运维人员!");
                } else {
                    return ResponseBo.error("推送出现未知错误，请联系系统运维人员!");
                }
            }
        }
    }

    /**
     * 获取任务的详细信息
     *
     * @param headId
     * @return
     */
    @GetMapping("/getOverAllInfo")
    public ResponseBo getOverAllInfo(@RequestParam Long headId) {
        QywxDailyHeader qywxDailyHeader = qywxDailyService.getHeadInfo(headId);
        return ResponseBo.okWithData(null, qywxDailyHeader);
    }

    /**
     * 获取推送变化数据
     *
     * @return
     */
    @GetMapping("/getPushEffectChange")
    public ResponseBo getPushEffectChange(@RequestParam("headId") Long headId) {
        return ResponseBo.okWithData(null, qywxDailyService.getPushEffectChange(headId));
    }

    /**
     * 获取已转化的明细数据
     *
     * @return
     */
    @GetMapping("/getConversionList")
    public ResponseBo getConversionList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long headId = Long.parseLong(request.getParam().get("headId"));
        String followUserId = request.getParam().get("followUserId");
        List<QywxDailyDetail> dataList = qywxDailyDetailService.getConversionList(headId, limit, offset, followUserId);
        int count = qywxDailyDetailService.getConversionCount(headId, followUserId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取企业微信成员列表
     *
     * @return
     */
    @GetMapping("/getFollowUserList")
    public ResponseBo getFollowUserList(Long headId) {
        List<FollowUserVO> dataList = qywxDailyDetailService.getAllFollowUserList(headId);
        return ResponseBo.okWithData(null, dataList);
    }

    /**
     *获取商品列表
     * @return
     */
    @GetMapping("/getRecProdList")
    public ResponseBo getRecProdList(Long headId){
        List<RecProdVo> dataList = qywxDailyDetailService.getRecProdList(headId);
        return ResponseBo.okWithData(null, dataList);
    }


    /**
     * 获取默认推送方式和定时推送时间
     *
     * @return
     */
    @GetMapping("/getPushInfo")
    public ResponseBo getPushInfo() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("method", pushConfig.getPushMethod());
        int hour = LocalTime.now().getHour();
        final List<String> timeList = IntStream.rangeClosed(8, 22).filter(x -> x > hour).boxed().map(y -> {
            if (y < 10) {
                return "0" + y + ":00";
            }
            return y + ":00";
        }).collect(Collectors.toList());
        result.put("timeList", timeList);

        result.put("effectDays", configService.getValueByName("op.daily.default.effectDays"));
        return ResponseBo.okWithData(null, result);
    }

    /**
     * 触达用户之前进行用户群组的验证 并返回验证结果 true表示验证未通过 false表示验证通过
     */
    @GetMapping("/validUserGroupForQywx")
    public ResponseBo validUserGroupForQywx() {
        //todo 临时修改
        Map<String, Object> result = Maps.newHashMap();
        result.put("flag", "通过");
        result.put("desc", "");
        return ResponseBo.okWithData("", result);
    }

    /**
     * 企业微信测试推送
     */
    @GetMapping("/testQywxPush")
    public ResponseBo testQywxPush(String mpTitle, String mpUrl,String mediaId,String linkPicurl,String linkUrl,String linkDesc,String linkTitle,String picUrl,String msgType, String senderId, String externalContact, String messageTest) {
        try {
            testPush(mpTitle,mpUrl,mediaId,linkPicurl,linkUrl,linkDesc,linkTitle,picUrl,msgType,senderId,externalContact,messageTest);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }

    }

    /**
     * 推送的实际方法
     *
     * @return
     */
    private String testPush(String mpTitle,
                            String mpUrl,
                            String mediaId,
                            String linkPicurl,
                            String linkUrl,
                            String linkDesc,
                            String linkTitle,
                            String picUrl,
                            String msgType,
                            String senderId,
                            String externalContact,
                            String messageTest) throws Exception {
        QywxMessage qywxMessage = new QywxMessage();
        qywxMessage.setText(messageTest);
        List<String> externalContactList = Arrays.asList(externalContact.split(","));
        if("applets".equals(msgType)){
            String appId =qywxMessageService.getMpAppId();
            qywxMessage.setMpPicMediaId(mediaId);
            qywxMessage.setMpAppid(appId);
            qywxMessage.setMpPage(mpUrl);
            qywxMessage.setMpTitle(mpTitle);
        }else if("image".equals(msgType)){
            qywxMessage.setImgPicUrl(picUrl);
        }else if("web".equals(msgType)){
            qywxMessage.setLinkDesc(linkDesc);
            qywxMessage.setLinkPicUrl(linkPicurl);
            qywxMessage.setLinkTitle(linkTitle);
            qywxMessage.setLinkUrl(linkUrl);
        }
        String result = qywxMessageService.pushQywxMessage(qywxMessage, senderId, externalContactList);
        if(StringUtils.isEmpty(result)){
            throw new Exception("推送消息失败");
        }
        return result;
    }

    @GetMapping("/getUserStatics")
    public ResponseBo getUserStatics(Long headId, String followUserId) {
        QywxDailyStaffEffect qywxDailyStaffEffect = qywxDailyService.getDailyStaffEffect(headId, followUserId);
        return ResponseBo.okWithData(null, qywxDailyStaffEffect);
    }

    /**
     * 手工发送优惠券
     */
    @GetMapping("/manualSubmitCoupon")
    public ResponseBo manualSubmitCoupon(Long headId) {
        return ResponseBo.ok(qywxDailyService.manualSubmitCoupon(headId));
    }

    /**
     * 手工发送消息
     */
    @GetMapping("/manualSubmitMessage")
    public ResponseBo manualSubmitMessage(Long headId) {
        return ResponseBo.ok(qywxDailyService.manualSubmitMessage(headId));
    }

    /**
     * 获取转化的明细数据
     * @param
     * @return
     */
    @GetMapping("/getConvertDetailData")
    public ResponseBo getConvertDetailData(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long headId = Long.parseLong(request.getParam().get("headId"));

        List<QywxDailyPersonalEffect> qywxDailyPersonalList = qywxDailyService.getConvertDetailData(limit, offset, headId);
        int count = qywxDailyService.getConvertDetailCount(headId);
        return ResponseBo.okOverPaging(null, count, qywxDailyPersonalList);
    }

    /**
     * 测试单人发券逻辑
     * @param couponIdentity 券标识
     * @param userIdentity 用户唯一标识
     * @return
     */
    @GetMapping("/sendCouponToUser")
    public ResponseBo sendCouponToUser(@RequestParam("couponId")Long couponId,@RequestParam("couponIdentity") String couponIdentity,@RequestParam("userIdentity") String userIdentity){
        //获取优惠券信息
        CouponInfoVO couponInfoVO=new CouponInfoVO();
        couponInfoVO.setCouponId(couponId);
        couponInfoVO.setCouponIdentity(couponIdentity);
        couponInfoVO.setBeginDate(LocalDate.now());
        couponInfoVO.setEndDate(LocalDate.now().plusMonths(2));
        couponInfoVO.setCouponName("测试优惠券");
        //构造发送信息
        SendCouponVO sendCouponVO=new SendCouponVO();
        sendCouponVO.setBusinessId(1l);
        sendCouponVO.setBusinessType("TEST");
        sendCouponVO.setUserIdentity(userIdentity);
        SendCouponResultVO sendCouponResultVO= null;
        try {
            sendCouponResultVO = qywxSendCouponService.sendCouponToUser(couponInfoVO,sendCouponVO);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("单人发券失败，原因为{}",e);
            return ResponseBo.error("单人发券错误,原因："+e.getMessage());
        }
    }

    /**
     * 测试多人发券
     * @return
     */
    @RequestMapping("/testCouponBatch")
    public ResponseBo  testCouponBatch(@RequestParam("couponId")Long couponId){
        CouponInfoVO couponInfoVO=new CouponInfoVO();
        couponInfoVO.setCouponId(couponId);
        couponInfoVO.setCouponIdentity("RXKHYRYXJUKR");
        couponInfoVO.setBeginDate(LocalDate.now());
        couponInfoVO.setEndDate(LocalDate.now().plusMonths(2));
        couponInfoVO.setCouponName("测试优惠券");
        //设置优惠券的时效
        List<SendCouponVO> sendCouponVOList=new ArrayList<>();
        SendCouponVO sendCouponVO1=new SendCouponVO();
        sendCouponVO1.setUserIdentity("15097615242");
        sendCouponVO1.setBusinessId(1L);
        sendCouponVO1.setBusinessType("TEST");
        sendCouponVOList.add(sendCouponVO1);
        SendCouponVO sendCouponVO2=new SendCouponVO();
        sendCouponVO2.setUserIdentity("15810081993");
        sendCouponVO2.setBusinessId(1L);
        sendCouponVO2.setBusinessType("TEST");
        sendCouponVOList.add(sendCouponVO2);
        SendCouponResultVO sendCouponResultVO= null;
        try {
            sendCouponResultVO = qywxSendCouponService.sendCouponBatch(couponInfoVO, sendCouponVOList);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("多人发券失败，原因为{}",e);
            return ResponseBo.error("多人发券错误,原因："+e.getMessage());
        }

    }


    /**
     * 预览推送，删除数据
     * @param headId
     * @param delDetailId 需要删除的detailId集合字符串
     * @return
     */
    @GetMapping("/resetPushDel")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo resetPushDel(@RequestParam("headId") Long headId,@RequestParam("delDetailId")String delDetailId,@RequestParam("version")int version){
        int count=qywxDailyService.updateVersion(headId, version);
        if(count<=0){
            return ResponseBo.error("当前记录已经被其他用户修改，请返回列表界面重新进入！");
        }
        List<Long> list=Arrays.stream(delDetailId.split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        qywxDailyDetailService.delDetail(headId,list);

        return ResponseBo.okWithData(null,qywxDailyService.getHeadInfo(headId).getVersion());
    }

    /**
     * 通过上传文件，新增券码
     * @param file
     * @param couponId
     * @param couponIdentity
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadCoupon")
    public ResponseBo uploadCoupon(@RequestParam("file") MultipartFile file,
                                   @RequestParam("couponId")  Long couponId,
                                   @RequestParam("couponIdentity")  String couponIdentity) throws Exception {
        if(FileUtils.multipartFileToFile(file)==null){
            return ResponseBo.error("上传文件为空,请重新上传数据！");
        }
        try {
            qywxDailyDetailService.uploadCoupon(file,couponId,couponIdentity);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseBo.error("文件解析异常！");
        } catch (LinkSteadyException e) {
            return  ResponseBo.error(e.getMessage());
        }
        return  ResponseBo.ok();
    }

    /**
     * 一键生成，券码流水号
     * @param couponId
     * @param couponIdentity
     * @return
     * @throws Exception
     */
    @PostMapping("/couponToSequence")
    public ResponseBo couponToSequence(@RequestParam("couponId")  Long couponId ,@RequestParam("couponIdentity")  String couponIdentity) throws Exception {
        try {
            qywxDailyDetailService.couponToSequence(couponId,couponIdentity);
        } catch (LinkSteadyException e) {
            return  ResponseBo.error(e.getMessage());
        }
        return  ResponseBo.ok();
    }

    /**
     * 下载模板
     * @param response
     * @throws IOException
     */
    @RequestMapping("/download")
    public void fileDownload(HttpServletResponse response) throws Exception {
        String fileName = "coupon_template.xls";
        String realFileName = fileName.substring(fileName.indexOf('_') + 1);
        Resource resource =  new ClassPathResource("excel/" + fileName);
        InputStream in = resource.getInputStream();
        response.setHeader("Content-Disposition", "inline;fileName=" + java.net.URLEncoder.encode(realFileName, "utf-8"));
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try (InputStream inputStream = in; OutputStream os = response.getOutputStream()) {
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
        }
    }

    /**
     * 查看有效优惠券
     * @param request
     * @return
     */
    @RequestMapping("/getCouponListPage")
    public ResponseBo getCouponListPage(QueryRequest request){
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = qywxDailyService.getCouponListCount();
        List<QywxCoupon> dataList = qywxDailyService.getCouponListData(limit,offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 查看券码明细
     * @param request
     * @return
     */
    @RequestMapping("/viewCouponData")
    public ResponseBo viewCouponData(QueryRequest request){
        int limit = request.getLimit();
        int offset = request.getOffset();
        Long couponId =Long.parseLong(StringUtils.isEmpty(request.getParam().get("couponId"))?"0":request.getParam().get("couponId")) ;
        String couponIdentity=request.getParam().get("couponIdentity");
        int count = qywxDailyService.viewCouponCount(couponId,couponIdentity);
        List<couponSerialNo> dataList = qywxDailyService.viewCouponData(limit,offset,couponId,couponIdentity);
        return ResponseBo.okOverPaging(null, count, dataList);
    }
}
