package com.linksteady.operate.controller;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.TargetList;
import com.linksteady.operate.domain.TgtReference;
import com.linksteady.operate.service.TargetDimensionService;
import com.linksteady.operate.service.TargetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hxcao on 2019-05-21
 */
@RestController
@RequestMapping("/target")
public class TargetListController {

    @Autowired
    private TargetListService targetListService;
    @Autowired
    private TargetDimensionService targetDimensionService;

    /**
     * 获取指标列表
     * @return
     */
    @GetMapping("/getKpi")
    public ResponseBo getKpi() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("gmv", "GMV");
        return ResponseBo.okWithData(null, map);
    }

    /**
     * 获取维度列表
     * @return
     */
    @GetMapping("/getDimension")
    public ResponseBo getDimension() {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("neworold", "首购/非首购");
        map.put("source", "渠道");
        return ResponseBo.okWithData(null, map);
    }

    /**
     * 根据维度code获取值
     * @return
     */
    @GetMapping("/getDimensionVal")
    public ResponseBo getDimensionVal(String key) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        Map<String, Object> value = Maps.newLinkedHashMap();
        value.put("Y", "首购");
        value.put("N", "非首购");
        map.put("neworold", value);

        value = Maps.newLinkedHashMap();
        value.put("1", "PC商城");
        value.put("2", "小程序");
        value.put("3", "APP");
        value.put("4", "wap站");
        map.put("source", value);
        return ResponseBo.okWithData(null, map.get(key));
    }

    /**
     * 保存targetList和targetDimension数据
     * @param target
     * @return
     */
    @PostMapping("/save")
    public ResponseBo save(@RequestBody TargetList target) {
        targetListService.save(target);
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

    @GetMapping("/getReferenceData")
    public ResponseBo getReferenceData(@RequestParam("startDt") String startDt, @RequestParam("endDt") String endDt,@RequestParam("period") String period) {
        List<TgtReference> list = Lists.newArrayList();
        Random ra =new Random();
        if("year".equals(period)) {
            TgtReference tgtReference1 = new TgtReference(String.valueOf(Long.valueOf(startDt)-1), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            TgtReference tgtReference2 = new TgtReference(String.valueOf(Long.valueOf(startDt)-2), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            TgtReference tgtReference3 = new TgtReference(String.valueOf(Long.valueOf(startDt)-3), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            list.add(tgtReference1);
            list.add(tgtReference2);
            list.add(tgtReference3);
        }
        if("month".equals(period)) {
            YearMonth yearMonth = YearMonth.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM"));
            yearMonth = yearMonth.plusMonths(-1);
            TgtReference tgtReference1 = new TgtReference(yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            yearMonth = yearMonth.plusMonths(-1);
            TgtReference tgtReference2 = new TgtReference(yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            yearMonth = yearMonth.plusMonths(-1);
            TgtReference tgtReference3 = new TgtReference(yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            list.add(tgtReference1);
            list.add(tgtReference2);
            list.add(tgtReference3);
        }
        if("day".equals(period)) {
            LocalDate localDate1 = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate localDate2 = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            localDate1 = localDate1.plusMonths(-1);
            localDate2 = localDate2.plusMonths(-1);
            TgtReference tgtReference1 = new TgtReference(localDate1.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "~"+localDate2.format(DateTimeFormatter.ofPattern("yyyyMMdd")), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            localDate1 = localDate1.plusMonths(-1);
            localDate2 = localDate2.plusMonths(-1);
            TgtReference tgtReference2 = new TgtReference(localDate1.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "~"+localDate2.format(DateTimeFormatter.ofPattern("yyyyMMdd")), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            localDate1 = localDate1.plusMonths(-1);
            localDate2 = localDate2.plusMonths(-1);
            TgtReference tgtReference3 = new TgtReference(localDate1.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "~"+localDate2.format(DateTimeFormatter.ofPattern("yyyyMMdd")), "GMV", String.valueOf(ra.nextInt(100)),String.valueOf(ra.nextInt(100)));
            list.add(tgtReference1);
            list.add(tgtReference2);
            list.add(tgtReference3);
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