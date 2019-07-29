package com.linksteady.operate.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.operate.dao.FitDataMapper;
import com.linksteady.operate.service.FitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;

/**
 * 拟合曲线
 * @author hxcao
 * @date 2019-07-26
 */
@Service
public class FitDataServiceImpl implements FitDataService {

    @Autowired
    private FitDataMapper fitDataMapper;

    /**
     * 根据公式和购买次数生成拟合曲线数据
     */
    @Override
    public List<Double> generateFittingData(String spuId, List<Integer> purchTimes, String type)
    {
        List<Double> result= Lists.newArrayList();

        //获取当前SPU的生成的系数和截距  字符串的格式为 a,b,c|f
        String param = fitDataMapper.getCeofBySpu(spuId, type);

        if(null==param||param.length()==0||!param.contains(",")||!param.contains("|"))
        {
            return result;
        }

        // 第一个值为系数 第二个值为截距
        List<String> paramList= Splitter.on('|').trimResults().omitEmptyStrings().splitToList(param);

        List<String> ceofList=Splitter.on(',').trimResults().omitEmptyStrings().splitToList(paramList.get(0));
        double ceof1=Double.parseDouble(ceofList.get(0));
        double ceof2=Double.parseDouble(ceofList.get(1));
        double ceof3=Double.parseDouble(ceofList.get(2));
        double ceof4=Double.parseDouble(ceofList.get(3));
        double intercept=Double.parseDouble(paramList.get(1));

        for(int i:purchTimes)
        {
            //获取对应的系数值
            if("formula".equals(type)) {
                Double d = calculateFormulaValue4ExpPercent(i,ceof1,ceof2,ceof3,ceof4,intercept);
                if(d >= 100D) {
                    result.add(100D);
                }else {
                    result.add(d);
                }
            }else {
                result.add(calculateFormulaValue4Exp(i,ceof1,ceof2,ceof3,ceof4,intercept));
            }
        }
        return result;
    }

    private double calculateFormulaValue3Exp(int x,double ceof1,double ceof2,double ceof3,double intercept) {
        double result= ArithUtil.formatDoubleByMode((Math.pow(x,3)*ceof1+Math.pow(x,2)*ceof2+x*ceof3+intercept) * 100,2, RoundingMode.DOWN);
        return result;
    }

    private double calculateFormulaValue4Exp(int x,double ceof1,double ceof2,double ceof3,double ceof4, double intercept) {
        double result= ArithUtil.formatDoubleByMode((Math.pow(x,4)*ceof1+Math.pow(x,3)*ceof2+ceof1+Math.pow(x,2)*ceof3+ceof1+x*ceof4+intercept),2, RoundingMode.DOWN);
        return result;
    }

    private double calculateFormulaValue4ExpPercent(int x,double ceof1,double ceof2,double ceof3,double ceof4, double intercept) {
        double result= ArithUtil.formatDoubleByMode((Math.pow(x,4)*ceof1+Math.pow(x,3)*ceof2+ceof1+Math.pow(x,2)*ceof3+ceof1+x*ceof4+intercept) * 100,2, RoundingMode.DOWN);
        return result;
    }
}
