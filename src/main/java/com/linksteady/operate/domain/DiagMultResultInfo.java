package com.linksteady.operate.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 诊断 乘法的返回结果集
 */
public class DiagMultResultInfo  extends DiagResultInfo{

    List<String> xData;  //对应横轴  时间
    String xName; // x轴的名称

    String firYName;  //第1个图的y轴名称
    List<Double>  firData;  //第1个图的数据
    double firChangeRate;   //第1个图的变化率
    double firAvg; //第1个平均值
    double firUp;  //上浮5%值
    double firDown;  //下浮5%值

    String secYName;  //第2个图的y轴名称
    List<Double>  secData;  //第2个图的数据
    double secChangeRate;   //第2个图的变化率
    double secAvg; //第2个平均值
    double secUp;  //上浮5%值
    double secDown;  //下浮5%值

    String thirdYName;  //第3个图的y轴名称
    List<Double>  thirdData;  //第3个图的数据
    double thirdChangeRate;   //第3个图的变化率
    double thirdAvg; //第3个平均值
    double thirdUp;  //上浮5%值
    double thirdDown;  //下浮5%值


    //变异系数 k为那个的变异系数编码 v为对应的值列表
    Map<String,List<Double>> covValues;
    //变异系数编码、变异系数名称 编码为cov1 cov2 cov3
    Map<String,String> covNames;

    JSONObject relate;  //相关性

    public List<String> getxData() {
        return xData;
    }

    public void setxData(List<String> xData) {
        this.xData = xData;
    }

    public String getxName() {
        return xName;
    }

    public void setxName(String xName) {
        this.xName = xName;
    }

    public String getFirYName() {
        return firYName;
    }

    public void setFirYName(String firYName) {
        this.firYName = firYName;
    }

    public List<Double> getFirData() {
        return firData;
    }

    public void setFirData(List<Double> firData) {
        this.firData = firData;
    }

    public double getFirChangeRate() {
        return firChangeRate;
    }

    public void setFirChangeRate(double firChangeRate) {
        this.firChangeRate = firChangeRate;
    }

    public double getFirUp() {
        return firUp;
    }

    public void setFirUp(double firUp) {
        this.firUp = firUp;
    }

    public double getFirDown() {
        return firDown;
    }

    public void setFirDown(double firDown) {
        this.firDown = firDown;
    }

    public String getSecYName() {
        return secYName;
    }

    public void setSecYName(String secYName) {
        this.secYName = secYName;
    }

    public List<Double> getSecData() {
        return secData;
    }

    public void setSecData(List<Double> secData) {
        this.secData = secData;
    }

    public double getSecChangeRate() {
        return secChangeRate;
    }

    public void setSecChangeRate(double secChangeRate) {
        this.secChangeRate = secChangeRate;
    }

    public double getSecUp() {
        return secUp;
    }

    public void setSecUp(double secUp) {
        this.secUp = secUp;
    }

    public double getSecDown() {
        return secDown;
    }

    public void setSecDown(double secDown) {
        this.secDown = secDown;
    }

    public String getThirdYName() {
        return thirdYName;
    }

    public void setThirdYName(String thirdYName) {
        this.thirdYName = thirdYName;
    }

    public List<Double> getThirdData() {
        return thirdData;
    }

    public void setThirdData(List<Double> thirdData) {
        this.thirdData = thirdData;
    }

    public double getThirdChangeRate() {
        return thirdChangeRate;
    }

    public void setThirdChangeRate(double thirdChangeRate) {
        this.thirdChangeRate = thirdChangeRate;
    }

    public double getThirdUp() {
        return thirdUp;
    }

    public void setThirdUp(double thirdUp) {
        this.thirdUp = thirdUp;
    }

    public double getThirdDown() {
        return thirdDown;
    }

    public void setThirdDown(double thirdDown) {
        this.thirdDown = thirdDown;
    }

    public Map<String, List<Double>> getCovValues() {
        return covValues;
    }

    public void setCovValues(Map<String, List<Double>> covValues) {
        this.covValues = covValues;
    }

    public double getFirAvg() {
        return firAvg;
    }

    public void setFirAvg(double firAvg) {
        this.firAvg = firAvg;
    }

    public double getSecAvg() {
        return secAvg;
    }

    public void setSecAvg(double secAvg) {
        this.secAvg = secAvg;
    }

    public double getThirdAvg() {
        return thirdAvg;
    }

    public void setThirdAvg(double thirdAvg) {
        this.thirdAvg = thirdAvg;
    }

    public Map<String, String> getCovNames() {
        return covNames;
    }

    public void setCovNames(Map<String, String> covNames) {
        this.covNames = covNames;
    }

    public JSONObject getRelate() {
        return relate;
    }

    public void setRelate(JSONObject relate) {
        this.relate = relate;
    }
}
