package com.linksteady.operate.domain;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * 加法的返回结果集
 */
public class DiagAddResultInfo extends DiagResultInfo{

    List<String> legendData;  //图例列表
    String xname;         //x轴名称  (所有图公用)

    List<String> xdata;  //x轴数据(所有图公用)


    JSONArray areaData; //面积图的数据

    JSONArray  lineData;  //折线图的各部分数据
    JSONArray  lineAvgData;  //折线图的各部分平均数据

    JSONArray  covData;  //变异系数数据

    public List<String> getLegendData() {
        return legendData;
    }

    public void setLegendData(List<String> legendData) {
        this.legendData = legendData;
    }

    public List<String> getXdata() {
        return xdata;
    }

    public void setXdata(List<String> xdata) {
        this.xdata = xdata;
    }

    public String getXname() {
        return xname;
    }

    public void setXname(String xname) {
        this.xname = xname;
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
