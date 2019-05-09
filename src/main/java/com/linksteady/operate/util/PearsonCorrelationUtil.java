package com.linksteady.operate.util;

import java.util.List;

/**
 * 计算两个数据集之间的皮尔逊相关系数
 * @author internet
 */
public class PearsonCorrelationUtil {

    public static double getPearsonCorrelationScoreByList(List<Double> x, List<Double> y) {
        if (x.size() != y.size())
        {
            throw new RuntimeException("数据不正确！");
        }

        double[] xData = new double[x.size()];
        double[] yData = new double[x.size()];
        for (int i = 0; i < x.size(); i++) {
            xData[i] = x.get(i);
            yData[i] = y.get(i);
        }

        return getPearsonCorrelationScore(xData,yData);
    }

    public static double getPearsonCorrelationScore(double[] xData, double[] yData) {
        if (xData.length != yData.length)
        {
            throw new RuntimeException("数据不正确！");
        }

        double xMeans;
        double yMeans;

        // 求解皮尔逊的分子
        double numerator = 0;
        // 求解皮尔逊系数的分母
        double denominator = 0;

        double result = 0;
        // 拿到两个数据的平均值
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        // 计算皮尔逊系数的分子
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数的分母
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数
        if(denominator==0)
        {
            return 0.00d;
        }else
        {
            return  numerator / denominator;
        }
    }

    /**
     * 计算分子
     *
     * @param xData x数组
     * @param xMeans x数组的均值
     * @param yData y数组
     * @param yMeans y数组的均值
     * @return 分子值
     */
    private static double generateNumerator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double numerator = 0.0;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    /**
     * 生成分母
     *
     * @param yMeans y的平均值
     * @param yData y数组
     * @param xMeans x数组的平均值
     * @param xData x数组
     * @return 分母
     */
    private static double generateDenomiator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double xSum = 0.0;
        double ySum = 0.0;
        for(double xdata:xData)
        {
            xSum += (xdata - xMeans) * (xdata - xMeans);
        }
        for(double ydata:yData)
        {
            ySum += (ydata - yMeans) * (ydata - yMeans);
        }

        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    /**
     * 根据给定的数据集进行平均值计算
     *
     * @param datas 数据集
     * @return 给定数据集的平均值
     */
    private static double getMeans(double[] datas) {
        double sum = 0.0;
        for(double data:datas)
        {
            sum += data;
        }
        return sum / datas.length;
    }


}
