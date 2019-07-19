package com.linksteady.mdss.vo;

import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-09
 */
public class Echart {

    private List<String> legendData;

    private List<String> xAxisData;

    private String xAxisName;

    private String yAxisName;

    private List<Map<String, Object>> seriesData;

    private List<String> yAxisData;

    public List<String> getyAxisData() {
        return yAxisData;
    }

    public void setyAxisData(List<String> yAxisData) {
        this.yAxisData = yAxisData;
    }

    public List<String> getLegendData() {
        return legendData;
    }

    public void setLegendData(List<String> legendData) {
        this.legendData = legendData;
    }

    public List<String> getxAxisData() {
        return xAxisData;
    }

    public void setxAxisData(List<String> xAxisData) {
        this.xAxisData = xAxisData;
    }

    public String getxAxisName() {
        return xAxisName;
    }

    public void setxAxisName(String xAxisName) {
        this.xAxisName = xAxisName;
    }

    public String getyAxisName() {
        return yAxisName;
    }

    public void setyAxisName(String yAxisName) {
        this.yAxisName = yAxisName;
    }

    public List<Map<String, Object>> getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(List<Map<String, Object>> seriesData) {
        this.seriesData = seriesData;
    }
}
