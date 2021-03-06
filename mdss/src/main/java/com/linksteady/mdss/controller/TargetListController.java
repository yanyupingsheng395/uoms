package com.linksteady.mdss.controller;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.mdss.config.KpiCacheManager;
import com.linksteady.mdss.domain.TargetDimension;
import com.linksteady.mdss.domain.TargetInfo;
import com.linksteady.mdss.service.TargetDimensionService;
import com.linksteady.mdss.service.TargetListService;
import com.linksteady.mdss.service.impl.TgtCalculateContext;
import com.linksteady.mdss.task.CalculateAllTargetTask;
import com.linksteady.mdss.util.UomsConstants;
import com.linksteady.mdss.vo.TgtReferenceVO;
import com.linksteady.common.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-21
 * @author hxcao
 */
@Slf4j
@RestController
@RequestMapping("/target")
public class TargetListController {

    @Autowired
    private TargetListService targetListService;
    @Autowired
    private TargetDimensionService targetDimensionService;

    @Autowired
    CalculateAllTargetTask calculateAllTargetTask;

    @Autowired
    TgtCalculateContext tgtCalculateContext;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    /**
     * 获取指标列表
     * @return
     */
    @GetMapping("/getKpi")
    public ResponseBo getKpi() {
        Map<String, Object> map = Maps.newHashMap();
        KpiCacheManager.getInstance().getKpiCodeNamePair().forEach((k, v)->{
            if(UomsConstants.TARGET_KPI_LIST.contains(k))
            {
                map.put(k,v);
            }
        });

        return ResponseBo.okWithData(null, map);
    }

    /**
     * 获取维度列表
     * @return
     */
    @GetMapping("/getDimension")
    public ResponseBo getDimension() {
        Map<String, Object> map = Maps.newLinkedHashMap();

        KpiCacheManager.getInstance().getDiagDimList().forEach((k,v)->{
            if(UomsConstants.TARGET_DIM_LIST.contains(k))
            {
                map.put(k,v);
            }
        });
        return ResponseBo.okWithData(null, map);
    }

    /**
     * 根据维度code获取值
     * @return
     */
    @GetMapping("/getDimensionVal")
    public ResponseBo getDimensionVal(String key) {
        return ResponseBo.okWithData(null,KpiCacheManager.getInstance().getDiagDimValueList().row(key));
    }

    /**
     * 保存targetList和targetDimension数据
     * @param target
     * @return
     */
    @PostMapping("/save")
    public ResponseBo save(@RequestBody TargetInfo target) {
        long targetId=targetListService.save(target);

        //提交后台进行拆解
        try {
            TargetInfo targetInfo=targetListService.selectByPrimaryKey(targetId);
            tgtCalculateContext.calculateTarget(targetInfo.getKpiCode(),targetInfo);

            tgtCalculateContext.targetSplit(targetInfo.getKpiCode(),targetId);
        } catch (Exception e) {
            log.error("ID: {} 拆分计算任务异常",targetId,e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            //更新目标的状态为错误状态
            targetListService.updateTargetStatus(targetId,"-1");
        }

        return ResponseBo.ok();
    }

    /**
     * 获取列表页数据
     * @return
     */
    @PostMapping(value = "/getPageList")
    public ResponseBo getPageList(@RequestBody QueryRequest request) {
        List<Map<String, Object>> dataList = targetListService.getPageList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(), request.getParam().get("targetName"));
        List<Map<String, Object>> newDataList = targetDimensionService.getDataList(dataList);
        int count = targetListService.getTotalCount(request.getParam().get("targetName"));
        return ResponseBo.okOverPaging(null, count, newDataList);
    }

    @GetMapping("/getDataById")
    public ResponseBo getDataById (@RequestParam("id") Long id) {
        return ResponseBo.okWithData(null, targetListService.getDataById(id));
    }

    /**
     * 获取参考数据 如果是年 获取的是过去三年的数据 如果是月 获取的是过去三个月的数据，如果是天到天，获取的是这个周期 过去三年的数据
     * @param kpiCode 指标名称
     * @param startDt 开始时间
     * @param endDt   结束时间
     * @param period  周期类型
     * @param dimInfo 维度信息
     * @return
     */
    @GetMapping("/getReferenceData")
    public ResponseBo getReferenceData(@RequestParam("kpiCode") String kpiCode,@RequestParam("startDt") String startDt, @RequestParam("endDt") String endDt,@RequestParam("period") String period, @RequestParam("dimInfo") String dimInfo) {
        JSONArray jsonArray = JSONArray.parseArray(dimInfo);
        List<TargetDimension> list = jsonArray.toJavaList(TargetDimension.class);
        Map<String, String> dimInfoMap = Maps.newHashMap();
        list.stream().forEach(x-> {
            dimInfoMap.put(x.getDimensionCode(), x.getDimensionValCode());
        });

        List<TgtReferenceVO> resultList=tgtCalculateContext.getReferenceData(kpiCode,period,startDt,endDt,dimInfoMap);
        return ResponseBo.okWithData(null, resultList);
    }

    /**
     * 获取启用状态的目标
     * @return
     */
    @GetMapping("/getTargetList")
    public ResponseBo getTargetList() {
        String user = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return ResponseBo.okWithData(null, targetListService.getTargetList(user));
    }

    @GetMapping("/getDimensionsById")
    public ResponseBo getDimensionsById(@RequestParam("id") String id) {
        return ResponseBo.okWithData(null, targetDimensionService.getDimensionsById(id));
    }

    /**
     * 获取参考数据 如果是年 获取的是过去三年的数据 如果是月 获取的是过去三个月的数据，如果是天到天，获取的是这个周期 过去三年的数据
     * @return
     */
    @GetMapping("/getReferenceDataById")
    public ResponseBo getReferenceData(@RequestParam("id") Long id) {
        Map<String, Object> map = targetListService.getDataById(id);
        String kpiCode = (String)map.get("KPI_CODE");
        String startDt = (String)map.get("START_DT");
        String endDt = (String)map.get("END_DT");
        String period = (String)map.get("PERIOD_TYPE");
        List<Map<String, Object>> dimInfoList = (List)map.get("DIMENSIONS");
        Map<String, String> dimInfo = Maps.newHashMap();
        dimInfoList.stream().forEach(x-> {
            dimInfo.put(String.valueOf(x.get("DIMENSION_CODE")), String.valueOf(x.get("DIMENSION_VAL_CODE")));
        });
        List<TgtReferenceVO> resultList=tgtCalculateContext.getReferenceData(kpiCode,period,startDt,endDt,dimInfo);

        return ResponseBo.okWithData(null, resultList);
    }

    /**
     * 获取拆解后的结果
     * @return
     */
    @RequestMapping("/getDismantData")
    public ResponseBo getDismantData(@RequestParam("id") Long targetId) {
        return ResponseBo.okWithData(null, targetListService.getDismantData(targetId));
    }

    @RequestMapping("/deleteDataById")
    public ResponseBo deleteDataById(@RequestParam("id") String id) {
        try {
            targetListService.deleteDataById(id);
            return ResponseBo.ok();
        } catch(Exception ex){
            log.error(ex.getMessage());
            return ResponseBo.error();
        }
    }


    /**
     * 对某个目标的实际完成情况进行手工运算
     * @return
     */
    @RequestMapping("/calculate")
    public ResponseBo calculate(@RequestParam("targetId") Long targetId) {

        TargetInfo targetInfo=targetListService.selectByPrimaryKey(targetId);
        tgtCalculateContext.calculateTarget(targetInfo.getKpiCode(),targetInfo);

        return ResponseBo.ok();
    }

    /**
     * 对所有目标的实际完成情况进行手工运算
     * @return
     */
    @RequestMapping("/calculateAll")
    public ResponseBo calculateAll() {

        try {
            calculateAllTargetTask.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseBo.ok();
    }

    /**
     * 提交后台进行拆解
     * @return
     */
    @RequestMapping("/submitToSplit")
    public ResponseBo submitToSplit(@RequestParam("targetId") long targetId) {
        //获取目标对象
        Map<String,Object> targetInfo=targetListService.getDataById(targetId);
        String kpiCode = (String)targetInfo.get("KPI_CODE");

        //提交后台进行拆解
        try {
            tgtCalculateContext.targetSplit(kpiCode,targetId);
        } catch (Exception e) {
            log.error("ID: {} 拆分计算任务异常",targetId,e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            //更新目标的状态为错误状态
            targetListService.updateTargetStatus(targetId,"-1");
        }

        return ResponseBo.ok();
    }
}