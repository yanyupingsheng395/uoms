package com.linksteady.operate.domain;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * 加法的返回结果集
 */
public class DiagAddResultInfo extends DiagResultInfo{

    List<String> legendData;  //图例列表

    List<String> firXdata;  //面积图的x轴数据
    String firXName;         //面积图的x轴名称  y轴固定为gmv

    JSONArray areaData; //面积图的数据

    JSONArray  lineData;  //折线图的各部分数据
    JSONArray  lineAvgData;  //折线图的各部分数据

    JSONArray  covData;  //折线图的各部分数据

    public List<String> getLegendData() {
        return legendData;
    }

    public void setLegendData(List<String> legendData) {
        this.legendData = legendData;
    }

    public List<String> getFirXdata() {
        return firXdata;
    }

    public void setFirXdata(List<String> firXdata) {
        this.firXdata = firXdata;
    }

    public String getFirXName() {
        return firXName;
    }

    public void setFirXName(String firXName) {
        this.firXName = firXName;
    }

    public JSONArray getAreaData() {
        return areaData;
    }

    public void setAreaData(JSONArray areaData) {
        this.areaData = areaData;
    }

    public JSONArray getLineData() {
        return lineData;
    }

    public void setLineData(JSONArray lineData) {
        this.lineData = lineData;
    }

    public JSONArray getLineAvgData() {
        return lineAvgData;
    }

    public void setLineAvgData(JSONArray lineAvgData) {
        this.lineAvgData = lineAvgData;
    }

    public JSONArray getCovData() {
        return covData;
    }

    public void setCovData(JSONArray covData) {
        this.covData = covData;
    }
}
