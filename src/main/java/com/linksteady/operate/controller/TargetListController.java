package com.linksteady.operate.controller;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TargetList;
import com.linksteady.operate.domain.TgtReference;
import com.linksteady.operate.service.TargetDimensionService;
import com.linksteady.operate.service.TargetListService;
import com.linksteady.operate.service.TargetSplitAsyncService;
import com.linksteady.operate.vo.TgtReferenceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hxcao on 2019-05-21
 * @author hxcao
 */
@RestController
@RequestMapping("/target")
public class TargetListController {

    @Autowired
    private TargetListService targetListService;
    @Autowired
    private TargetDimensionService targetDimensionService;

    @Autowired
    TargetSplitAsyncService targetSplitAsyncService;

    private static final List<String> TARGET_KPI_LIST= Arrays.asList("gmv");
    private static final List<String> TARGET_DIM_LIST= Arrays.asList("neworold","source");

    /**
     * 获取指标列表
     * @return
     */
    @GetMapping("/getKpi")
    public ResponseBo getKpi() {
        Map<String, Object> map = Maps.newHashMap();
        KpiCacheManager.getInstance().getKpiCodeNamePair().forEach((k,v)->{
            if(TARGET_KPI_LIST.contains(k))
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
            if(TARGET_DIM_LIST.contains(k))
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
        int targetId=targetListService.save(target);

        //提交后台进行拆解
        targetSplitAsyncService.targetSplit(targetId);

        return ResponseBo.ok();
    }

    /**
     * 获取列表页数据
     * @return
     */
    @PostMapping(value = "/getPageList")
    public ResponseBo getPageList(@RequestBody QueryRequest request) {
        List<Map<String, Object>> dataList = targetListService.getPageList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());
        List<Map<String, Object>> newDataList = targetDimensionService.getDataList(dataList);
        int count = targetListService.getTotalCount();
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
    public ResponseBo getReferenceData(@RequestParam("kpiCode") String kpiCode,@RequestParam("startDt") String startDt, @RequestParam("endDt") String endDt,@RequestParam("period") String period,@RequestBody Map<String,String> dimInfo) {
        List<TgtReferenceVO> list=null;

        if("gmv".equals(kpiCode))
        {
            list=targetListService.getGmvReferenceData(period,startDt,endDt,dimInfo);

        }else
        {
            list=Lists.newArrayList();
        }
        return ResponseBo.okWithData(null, list);
    }

    /**
     * 获取启用状态的目标
     * @return
     */
    @GetMapping("/getTargetList")
    public ResponseBo getTargetList() {
        return ResponseBo.okWithData(null, targetListService.getTargetList());
    }

    @GetMapping("/getDimensionsById")
    public ResponseBo getDimensionsById(@RequestParam("id") String id) {
        return ResponseBo.okWithData(null, targetDimensionService.getDimensionsById(id));
    }
}