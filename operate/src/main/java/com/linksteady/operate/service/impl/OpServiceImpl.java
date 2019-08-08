package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.OpMapper;
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
    public List<Map<String, Object>> getPeriodHeaderList(int startRow, int endRow, String taskName) {
        return opMapper.getPeriodHeaderList(startRow,endRow, taskName);
    }

    @Override
    public int getPeriodListCount(String taskName) {
        return opMapper.getPeriodListCount(taskName);
    }

    @Override
    public void savePeriodHeaderInfo(String periodName, String startDt, String endDt) {
        //获取主键
        int pk=opMapper.getPeriodPrimaryKey();
        opMapper.savePeriodHeaderInfo(pk,periodName,startDt,endDt);
        opMapper.copyPeriodDetail(pk);
        opMapper.copyPeriodStatis(pk);
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
    public List<Map<String, Object>> getPeriodSpuStatis(String headerId) {
        return opMapper.getPeriodSpuStatis(headerId);
    }

    @Override
    public Echart getPeriodChartData(String headerId, String type) {
        List<Map<String, Object>> dataList = opMapper.getPeriodChartData(headerId, type);
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
