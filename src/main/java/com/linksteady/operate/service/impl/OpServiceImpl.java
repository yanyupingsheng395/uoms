package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.LifeCycleMapper;
import com.linksteady.operate.dao.OpMapper;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.OpService;
import com.linksteady.operate.vo.Echart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OpServiceImpl implements OpService {

    @Autowired
    private OpMapper opMapper;


    @Override
    public List<Map<String, Object>> getOpDayList(int startRow, int endRow,String daywid) {
        return opMapper.getOpDayList(startRow, endRow,daywid);
    }

    @Override
    public int getOpDayListCount(String daywid) {
        return opMapper.getOpDayListCount(daywid);
    }

    @Override
    public Integer getOpDayUserCountInfo(String daywid) {
        return opMapper.getOpDayUserCountInfo(daywid);
    }

    @Override
    public List<Map<String, Object>> getOpDayDetailList(int startRow, int endRow, String daywid) {
        return opMapper.getOpDayDetailList(startRow,endRow,daywid);
    }

    @Override
    public int getOpDayDetailListCount(String daywid) {
        return opMapper.getOpDayDetailListCount(daywid);
    }

    @Override
    public List<Map<String, Object>> getOpDayDetailAllList(String daywid) {
        return opMapper.getOpDayDetailAllList(daywid);
    }

    @Override
    public List<Map<String, Object>> getPeriodHeaderList(int startRow, int endRow) {
        return opMapper.getPeriodHeaderList(startRow,endRow);
    }

    @Override
    public int getPeriodListCount() {
        return opMapper.getPeriodListCount();
    }

    @Override
    public void savePeriodHeaderInfo(String periodName, String startDt, String endDt) {
        opMapper.savePeriodHeaderInfo(periodName,startDt,endDt);
    }

    @Override
    public List<Map<String, Object>> getPeriodUserList(int startRow, int endRow, String headerId) {
        return opMapper.getPeriodUserList(startRow, endRow,headerId);
    }

    @Override
    public int getPeriodUserListCount(String headerId) {
        return opMapper.getPeriodUserListCount(headerId);
    }

    @Override
    public List<Map<String, Object>> getSpuStatis(String touchDt) {
        return opMapper.getSpuStatis(touchDt);
    }

    @Override
    public Echart getChartData(String touchDt, String type) {
        List<Map<String, Object>> dataList = opMapper.getChartData(touchDt, type);
        Echart echart = new Echart();
        String xAxisName = "";
        String yAxisName = "";
        switch (type) {
            case "unit_price":
                xAxisName = "目标件单价（元）";
                yAxisName = "推荐次数";
                break;
            case "discount_rate":
                xAxisName = "折扣率（%）";
                yAxisName = "推荐次数";
                break;
            case "buying_time":
                xAxisName = "购买时间";
                yAxisName = "订单量";
                break;
            case "refer_deno":
                xAxisName = "优惠面额（元）";
                yAxisName = "推荐次数";
                break;
        }
        echart.setxAxisName(xAxisName);
        echart.setyAxisName(yAxisName);
        echart.setxAxisData(dataList.stream().map(x->x.get("X_DATA").toString()).collect(Collectors.toList()));
        echart.setyAxisData(dataList.stream().map(x->x.get("Y_DATA").toString()).collect(Collectors.toList()));
        return echart;
    }
}
