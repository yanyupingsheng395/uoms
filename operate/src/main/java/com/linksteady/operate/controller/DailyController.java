package com.linksteady.operate.controller;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.config.ConfigCacheManager;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.*;
import com.linksteady.operate.vo.DailyPersonalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 每日运营
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@RestController
@RequestMapping("/daily")
public class DailyController {

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyDetailService dailyDetailService;

    @Autowired
    private DailyProperties dailyProperties;

    /**
     * 获取每日成长任务分页列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getPageList")
    public ResponseBo getPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String touchDt = request.getParam().get("touchDt");
        int count = dailyService.getTotalCount(touchDt);
        List<DailyHead> dailyInfos = dailyService.getPageList(start, end, touchDt);

        //设置当前天记录的 校验状态
        String validateLabel=dailyService.validUserGroup()?"未通过":"通过";

        dailyInfos.stream().forEach(p->{
           p.setValidateLabel(validateLabel);
        });

        return ResponseBo.okOverPaging(validateLabel, count, dailyInfos);
    }

    /**
     * 根据成长性，活跃度，组Id获取对应的用户列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getDetailPageList")
    public ResponseBo getDetailPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        List<DailyDetail> dataList = dailyDetailService.getPageList(start, end, headId, userValue, pathActive);
        int count = dailyDetailService.getDataCount(headId, userValue, pathActive);
        return ResponseBo.okOverPaging(null, count, dataList);
    }


    /**
     * 生成待推送的短信文案
     *
     * @return
     */
    @GetMapping("/generatePushList")
    public ResponseBo generatePushList(String headId) {

        //首先获取锁
        if(dailyService.getTransContentLock(headId))
        {
            //调用文案生成逻辑
            dailyDetailService.deletePushContentTemp(headId);
            //1表示生成成功 0表示生成失败
            String result = dailyDetailService.generatePushList(headId);
            if ("1".equals(result)) {
                //释放锁
                dailyService.delTransLock();

                long timestamp=System.currentTimeMillis();
                //更新时间戳到头表
                dailyService.updateHeaderOpChangeDate(headId,timestamp);

                return ResponseBo.ok(timestamp);
            } else {
                return ResponseBo.error("生成文案错误，请联系系统运维人员！");
            }
        }else
        {
            return ResponseBo.error("其他用户正在生成文案，请稍后再操作！");
        }
    }

    /**
     * 获取用户的文案
     *
     * @return
     */
    @GetMapping("/getUserStrategyList")
    public ResponseBo getUserStrategyList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        List<DailyDetail> dataList = dailyDetailService.getStrategyPageList(start, end, headId);
        int count = dailyDetailService.getStrategyCount(headId);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 获取用户预览数据
     *
     * @return
     */
    @GetMapping("/getUserStatsData")
    public ResponseBo getUserStatsData(String headId) {
        Map<String,String> pathActiveMap= ConfigCacheManager.getInstance().getPathActiveMap();
        Map<String,String> userValueMap=ConfigCacheManager.getInstance().getUserValueMap();
        Map<String,String> lifeCycleMap=ConfigCacheManager.getInstance().getLifeCycleMap();

        //获取人群分布
        List<DailyUserStats> dailyUserStats=dailyService.getUserStats(headId);

       dailyUserStats.stream().forEach(p->{
           p.setUserValueLabel(userValueMap.get(p.getUserValue()));
           p.setGetPathActivityLabel(pathActiveMap.get(p.getPathActivity()));
           p.setLifecycleLabel(lifeCycleMap.get(p.getLifecycle()));
       });

        JSONArray fparray=new JSONArray();
        JSONArray rparray=new JSONArray();
        JSONArray fpTemp=null;
        for(DailyUserStats d1:dailyUserStats)
        {
            fpTemp=new JSONArray();
            /**
             * x轴的数据 人数
             */
            fpTemp.add(d1.getUcnt());
            /**
             * y轴 用户活跃度
             */
            fpTemp.add(d1.getGetPathActivityLabel());

            int formatSize=0;
            if("ULC_01".equals(d1.getUserValue()))
            {
                formatSize=80;
            }else if("ULC_02".equals(d1.getUserValue()))
            {
                formatSize=60;
            }else if("ULC_03".equals(d1.getUserValue()))
            {
                formatSize=40;
            }else if("ULC_04".equals(d1.getUserValue()))
            {
                formatSize=20;
            }
            fpTemp.add(formatSize);
            fpTemp.add(d1.getLifecycleLabel());
            fpTemp.add("人数:"+d1.getUcnt()+",占比:"+d1.getPct()+"%");
            fpTemp.add(d1.getUserValue());
            fpTemp.add(d1.getPathActivity());
            fpTemp.add(d1.getLifecycle());

            if("1".equals(d1.getLifecycle()))
            {
                fparray.add(fpTemp);
            }else
            {
                rparray.add(fpTemp);
            }
        }

        Map<String,Object> result=Maps.newHashMap();
        result.put("fpUser",fparray);
        result.put("rpUser",rparray);

        //获取排名第一的群组 获取其类目信息
        DailyUserStats dailyUserStats1=dailyUserStats.get(0);
        if(null!=dailyUserStats1)
        {
            //获取用户在SPU上top 10
            List<DailyUserStats> spuList=dailyService.getUserStatsBySpu(headId,dailyUserStats1.getUserValue(),dailyUserStats1.getPathActivity(),dailyUserStats1.getLifecycle());
            result.put("spuList",spuList);
            result.put("groupName",dailyUserStats1.getUserValueLabel()+","+dailyUserStats1.getGetPathActivityLabel()+","+dailyUserStats1.getLifecycleLabel()+"群组");
            result.put("userValue",dailyUserStats1.getUserValue());
            result.put("pathActive",dailyUserStats1.getPathActivity());
            result.put("lifecycle",dailyUserStats1.getLifecycle());

            DailyUserStats dailyUserStats2=spuList.get(0);

            //获取top10 推荐商品
            if(null!=dailyUserStats2)
            {
                List<DailyUserStats> prodList=dailyService.getUserStatsByProd(headId,dailyUserStats1.getUserValue(),dailyUserStats1.getPathActivity(),dailyUserStats1.getLifecycle(),dailyUserStats2.getSpuName());
                result.put("prodList",prodList);
                result.put("prodGroupName",dailyUserStats1.getUserValueLabel()+","+dailyUserStats1.getGetPathActivityLabel()+","+dailyUserStats1.getLifecycleLabel()+"群组"+dailyUserStats2.getSpuName()+"类目");
                result.put("spuName",dailyUserStats2.getSpuName());
            }else
            {
                result.put("prodList",Lists.newArrayList());
                result.put("prodGroupName","");
                result.put("spuName","");
            }

        }else
        {
            result.put("spuList",Lists.newArrayList());
            result.put("groupName","");
            result.put("userValue","");
            result.put("pathActive","");
            result.put("lifecycle","");
        }
        return ResponseBo.okWithData("",result);
    }

    /**
     * 用户预览 刷新SPU表格和PROD表格
     */
    @GetMapping("/refreshUserStatData")
    public ResponseBo refreshUserStatData(String headId,String userValue,String pathActive,String lifecycle) {
        Map<String,String> pathActiveMap= ConfigCacheManager.getInstance().getPathActiveMap();
        Map<String,String> userValueMap=ConfigCacheManager.getInstance().getUserValueMap();
        Map<String,String> lifeCycleMap=ConfigCacheManager.getInstance().getLifeCycleMap();

        Map<String,Object> result=Maps.newHashMap();

        List<DailyUserStats> spuList=dailyService.getUserStatsBySpu(headId,userValue,pathActive,lifecycle);
        result.put("spuList",spuList);
        result.put("groupName",userValueMap.get(userValue)+","+pathActiveMap.get(pathActive)+","+lifeCycleMap.get(lifecycle)+"群组");

        DailyUserStats dailyUserStats2=spuList.get(0);

        //获取top10 推荐商品
        if(null!=dailyUserStats2)
        {
            List<DailyUserStats> prodList=dailyService.getUserStatsByProd(headId,userValue,pathActive,lifecycle,dailyUserStats2.getSpuName());
            result.put("prodList",prodList);
            result.put("prodGroupName",userValueMap.get(userValue)+","+pathActiveMap.get(pathActive)+","+lifeCycleMap.get(lifecycle)+"群组"+dailyUserStats2.getSpuName()+"类目");
            result.put("spuName",dailyUserStats2.getSpuName());
        }else
        {
            result.put("prodList",Lists.newArrayList());
            result.put("prodGroupName","");
            result.put("spuName","");
        }
        return ResponseBo.okWithData("",result);
    }

    /**
     * 用户预览 刷新PROD表格
     */
    @GetMapping("/refreshUserStatData2")
    public ResponseBo refreshUserStatData2(String headId,String userValue,String pathActive,String lifecycle,String spuName) {

        Map<String,String> pathActiveMap= ConfigCacheManager.getInstance().getPathActiveMap();
        Map<String,String> userValueMap=ConfigCacheManager.getInstance().getUserValueMap();
        Map<String,String> lifeCycleMap=ConfigCacheManager.getInstance().getLifeCycleMap();

        Map<String,Object> result=Maps.newHashMap();

        List<DailyUserStats> prodList=dailyService.getUserStatsByProd(headId,userValue,pathActive,lifecycle,spuName);
        result.put("prodList",prodList);
        result.put("prodGroupName",userValueMap.get(userValue)+","+pathActiveMap.get(pathActive)+","+lifeCycleMap.get(lifecycle)+"群组"+spuName+"类目");

        return ResponseBo.okWithData("",result);
    }



    /**
     * 启动群组推送
     *
     * @return
     */
    @GetMapping("/submitData")
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResponseBo submitData(String headId, String pushMethod, String pushPeriod,Long timestamp) {
        //进行一次状态的判断
        String status = dailyService.getDailyHeadById(headId).getStatus();
        if (!status.equalsIgnoreCase("todo")) {
            return ResponseBo.error("当前数据状态不支持该操作！");
        }

        // 短信文案的校验  (是否包含变量，短信长度)
        String validResult = smsContentValid(headId);
        if (null != validResult) {
            return ResponseBo.error(validResult);
        }

        //判断时间戳
        int count=dailyService.validateOpChangeTime(headId,timestamp);
        if(count==0)
        {
            return ResponseBo.error("数据已被其他用户操作，请返回列表界面重新尝试！");
        }else
        {
            //更新时间戳
            long timestampNew=System.currentTimeMillis();
            //更新时间戳到头表
            dailyService.updateHeaderOpChangeDate(headId,timestampNew);

            // 推送方式 IMME立即推送 AI智能推送 FIXED固定时间推送
            updateSmsPushMethod(headId, pushMethod, pushPeriod);

            //复制写入待推送列表
            dailyDetailService.copyToPushList(headId);

            status = "done";
            dailyService.updateStatus(headId, status);
            return ResponseBo.ok();
        }
    }

    /**
     * 根据推送方式更新短信推送时间
     */
    private void updateSmsPushMethod(String headId, String method, String period) {
        String pushOrderPeriod = "";
        // 立即推送：当前时间往后顺延10分钟
        if ("IMME".equalsIgnoreCase(method)) {
            pushOrderPeriod = String.valueOf(LocalTime.now().plusMinutes(10).getHour());
        }

        // 固定时间推送：参数获取
        if ("FIXED".equalsIgnoreCase(method)) {
            pushOrderPeriod = String.valueOf(LocalTime.parse(period, DateTimeFormatter.ofPattern("HH:mm")).getHour());
        }
        // 默认是AI：plan_push_period = order_period 此时，pushOrderPeriod = ""
        dailyDetailService.updatePushOrderPeriod(headId, pushOrderPeriod);
    }

    /**
     * 发送之前校验短信内容
     *
     * @param headId
     * @return
     */
    private String smsContentValid(String headId) {
        List<Map<String, Object>> smsContentList = dailyDetailService.getContentList(headId);
        int totalSize = smsContentList.size();
        // 短信长度超出限制
        List<String> lengthIds = smsContentList.stream().filter(x -> String.valueOf(x.get("CONTENT")).length() > dailyProperties.getSmsLengthLimit())
                .map(y -> String.valueOf(y.get("ID"))).collect(Collectors.toList());
        // 短信含未被替换的模板变量
        List<String> invalidIds = smsContentList.stream().filter(x -> String.valueOf(x.get("CONTENT")).contains("$"))
                .map(y -> String.valueOf(y.get("ID"))).collect(Collectors.toList());
        if (0 != lengthIds.size()) {
            return "短信长度超出限制，共：" + totalSize + "条，不符合规范：" + lengthIds.size() + "条";
        }
        if (0 != invalidIds.size()) {
            return "短信含未被替换的模板变量，共：" + totalSize + "条，不符合规范：" + invalidIds.size() + "条";
        }
        return null;
    }

    /**
     * 根据headId获取当前记录的状态
     *
     * @param headId
     * @return
     */
    @GetMapping("/getStatusById")
    public ResponseBo getStatusById(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getDailyHeadById(headId).getStatus());
    }

    /**
     * 获取当前日期和任务日期
     *
     * @param headId
     * @return
     */
    @GetMapping("/getCurrentAndTaskDate")
    public ResponseBo getCurrentAndTaskDate(@RequestParam String headId) {
        return ResponseBo.okWithData(null, dailyService.getCurrentAndTaskDate(headId));
    }

    /**
     * 获取推送数据
     * @return
     */
    @GetMapping("/getPushData")
    public ResponseBo getPushData(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, dailyService.getPushData(headId));
    }

    /**
     * 获取用户组配置分页数据
     *
     * @param
     * @return
     */
    @GetMapping("/userGroupList")
    public ResponseBo userGroupList() {
        List<DailyGroupTemplate> dataList = dailyService.getUserGroupList();
        return ResponseBo.okWithData(null, dataList);
    }

    /**
     * 每日成长任务用户群组设置短信内容
     * @param groupId
     * @param smsCode
     * @return
     */
    @GetMapping("/setSmsCode")
    public ResponseBo setSmsCode(@RequestParam String groupId, @RequestParam String smsCode) {
        dailyService.setSmsCode(groupId, smsCode);
        return ResponseBo.ok();
    }

    /**
     * 每日运营-个体效果
     * @return
     */
    @GetMapping("/getDailyPersonalEffect")
    public ResponseBo getDailyPersonalEffect(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String headId = request.getParam().get("headId");
        String spuIsConvert = request.getParam().get("spuIsConvert");
        String userValue = request.getParam().get("userValue");
        String pathActive = request.getParam().get("pathActive");
        DailyPersonalVo dailyPersonalVo = new DailyPersonalVo();
        dailyPersonalVo.setSpuIsConvert(spuIsConvert);
        dailyPersonalVo.setUserValue(userValue);
        dailyPersonalVo.setPathActive(pathActive);

        List<DailyPersonal> personals = dailyService.getDailyPersonalEffect(dailyPersonalVo, start, end, headId);
        int count = dailyService.getDailyPersonalEffectCount(dailyPersonalVo, headId);
        return ResponseBo.okOverPaging(null, count, personals);
    }

    /**
     * 导出每日运营个体结果表
     * @param headId
     * @return
     * @throws InterruptedException
     */
    @PostMapping("/downloadExcel")
    public ResponseBo excel(@RequestParam("headId") String headId) throws InterruptedException {
        List<DailyPersonal> list = Lists.newLinkedList();
        List<Callable<List<DailyPersonal>>> tmp = Lists.newLinkedList();
        int count = dailyService.getDailyPersonalEffectCount(new DailyPersonalVo(), headId);
        ExecutorService service = Executors.newFixedThreadPool(10);
        int pageSize = 1000;
        int pageNum = count % pageSize == 0 ? count / pageSize : (count / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            int idx = i;
            tmp.add(() -> {
                int start = idx * pageSize + 1;
                int end = (idx + 1) * pageSize;
                end = end > count ? count : end;
                return dailyService.getDailyPersonalEffect(new DailyPersonalVo(), start, end, headId);
            });
        }

        List<Future<List<DailyPersonal>>> futures = service.invokeAll(tmp);
        futures.stream().forEach(x-> {
            try {
                list.addAll(x.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        try {
            return FileUtils.createExcelByPOIKit("每日运营个体结果表", list, DailyPersonal.class);
        } catch (Exception e) {
            log.error("导出每日运营个体结果表失败", e);
            return ResponseBo.error("导出每日运营个体结果表失败，请联系网站管理员！");
        }
    }

    /**
     * 获取默认推送方式和定时推送时间
     *
     * @return
     */
    @GetMapping("/getPushInfo")
    public ResponseBo getPushInfo() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("method", dailyProperties.getPushMethod());
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

    /**
     * 触达用户之前进行用户群组的验证 并返回验证结果 true表示验证未通过 false表示验证通过
     *
     * @return
     */
    @GetMapping("/validUserGroup")
    public ResponseBo validUserGroup() {
        return ResponseBo.okWithData(null, dailyService.validUserGroup());
    }
}
