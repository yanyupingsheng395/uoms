package com.linksteady.operate.service.impl;

import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.DimConfigInfo;
import com.linksteady.operate.util.UomsConstants;
import com.linksteady.operate.vo.DiagConditionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 诊断操作 公共方法的一些类
 * @author huang
 */
@Service
class DiagOpCommonServiceImpl {

    /**
     * 判断一组过滤条件 是否依赖于订单明细表（也就是某些字段是否只能通过订单明细行来实现 如产品、品类、SPU）
     * @return 如果依赖于订单明细，则返回true 否则返回false
     */
     boolean isRelyOrderDetail(List<DiagConditionVO> whereInfo)
    {
        if(null==whereInfo||whereInfo.size()==0)
        {
            return false;
        }

        List<String> selectDimCodeList=whereInfo.stream().map(DiagConditionVO::getDimCode).collect(Collectors.toList());

        List<String> relyDimCodeList= KpiCacheManager.getInstance().getDimConfigList().stream()
                .filter(a->"Y".equals(a.getRelyOrderDetail())).map(DimConfigInfo::getDimCode).collect(Collectors.toList());


        List<String> intersection = selectDimCodeList.stream().filter(item -> relyDimCodeList.contains(item)).collect(Collectors.toList());
        return intersection.size()>0;
    }

    /**
     * 判断某个维度 是否依赖于订单明细表 (如产品、SPU等只能通过订单行来实现过滤，无法通过订单头表实现)
     * @return 如果依赖于订单明细，则返回true 否则返回false
     */
    boolean isRelyOrderDetail(String dimCode)
    {
        List<String> relyDimCodeList=KpiCacheManager.getInstance().getDimConfigList().stream()
                .filter(a->"Y".equals(a.getRelyOrderDetail())).map(DimConfigInfo::getDimCode).collect(Collectors.toList());

        return relyDimCodeList.contains(dimCode);
    }

    /**
     * @param periodType 周期类型 M表示月 Y表示年 D表示天
     * @param  beginDt  YYYY-MM-DD格式  月份为YYYYMM格式  年为YYYY格式
     * @param  endDt
     * @return 构造完成的字符串
     */
    String buildDateRange(String periodType,String beginDt,String endDt)
    {
        StringBuffer bf=new StringBuffer();
        if(UomsConstants.PERIOD_TYPE_MONTH.equals(periodType))
        {
            bf.append(" AND W_DATE.MONTH>=").append(StringUtils.replaceChars(beginDt,"-","")).append(" AND W_DATE.MONTH<=").append(StringUtils.replaceChars(endDt,"-",""));
        }else if(UomsConstants.PERIOD_TYPE_DAY.equals(periodType))
        {
            bf.append(" AND W_DATE.DAY>=").append(StringUtils.replaceChars(beginDt,"-","")).append(" AND W_DATE.DAY<=").append(StringUtils.replaceChars(endDt,"-",""));
        }else if(UomsConstants.PERIOD_TYPE_YEAR.equals(periodType))
        {
            bf.append(" AND W_DATE.YEAR>=").append(beginDt).append(" AND W_DATE.YEAR<=").append(endDt);
        }
        return bf.toString();
    }

    /**
     * 对指标结果进行格式化
     */
    String valueFormat(Double value,String formatType)
    {
        if(null==value)
        {
            return "";
        }

        String pattern="#,##0.00";
        if("DF".equals(formatType))
        {
            pattern="#,##0.00";
        }else if("D".equals(formatType))
        {
            pattern="0";
        }else if("D2F".equals(formatType))
        {
            pattern="#,##0.00";
        }
        else if("D2".equals(formatType))
        {
            pattern="0.00";
        }
        else if("D4F".equals(formatType))
        {
            pattern="#,##0.0000";
        }else if("D4".equals(formatType))
        {
            pattern="0.0000";
        }

        double val=value;
        //做这一步 是因为 DecimalFormat 格式化的时候默认使用的四舍五入模式
        if("D2".equals(formatType)||"D2F".equals(formatType))
        {
            val= ArithUtil.formatDoubleByMode(value,2, RoundingMode.DOWN);
        }else if("D4".equals(formatType)||"D4F".equals(formatType))
        {
            val=ArithUtil.formatDoubleByMode(value,4,RoundingMode.DOWN);
        }
        DecimalFormat decimalFormat= new DecimalFormat(pattern);

        return decimalFormat.format(val);
    }

    /**
     * 对指标结果进行格式化
     */
    List<String> valueFormat(List<Double> value,String formatType)
    {
        return value.stream().map(s->valueFormat(s,formatType)).collect(Collectors.toList());
    }

    /**
     * 获取周期起始之间的每一个周期 如果period为M，则startDt和endDt必须为YYYY-MM格式，如果periodType为D,则startDt和endDt必须为YYYY-MM-DD格式
     */
    List<String> getPeriodList(String periodType,String beginDt,String endDt)
    {
        List<String> periodList=null;

        //获取周期内的每个明细单位 //按月
        if(UomsConstants.PERIOD_TYPE_MONTH.equals(periodType))
        {
            periodList= DateUtil.getMonthBetween(beginDt,endDt);
        }else if(UomsConstants.PERIOD_TYPE_DAY.equals(periodType))
        {
            if(beginDt.length()==7||endDt.length()==7)
            {
                //获取月末最后一天
                periodList=DateUtil.getEveryday(beginDt+"-01",DateUtil.getLastDayOfMonth(endDt));
            }else
            {
                periodList=DateUtil.getEveryday(beginDt,endDt);
            }

        }
        return periodList;
    }

    /**
     * 修复数据  遍历连续的周期列表，判断当前周期列表是否在第一个数据集中有值，如果有，则返回第一个数据集中的值，如果没有，则填充0
     * @param datas 其key为周期字段，如YYYY-MM格式 或YYYY-MM-DD格式
     ** @param periodList 连续的周期类型列表
     * @return
     */
    List<Double> fixData(Map<String,Double> datas, List<String> periodList)
    {
        return periodList.stream().map(s->{
            if(null==datas.get(s)||"".equals(datas.get(s)))
            {
                return 0d;
            }else
            {
                return datas.get(s);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 根据周期类型 返回对应的列
     * @param periodType
     * @return
     */
    String buildPeriodInfo(String periodType)
    {
        StringBuffer bf=new StringBuffer();
        if(UomsConstants.PERIOD_TYPE_MONTH.equals(periodType))
        {
            bf.append("W_DATE.MONTH_SHORT_NAME");
        }else if(UomsConstants.PERIOD_TYPE_DAY.equals(periodType))
        {
            bf.append("W_DATE.DAY_SHORT_NAME");
        }else if(UomsConstants.PERIOD_TYPE_YEAR.equals(periodType))
        {
            bf.append("W_DATE.YEAR");
        }
        return bf.toString();
    }
}
