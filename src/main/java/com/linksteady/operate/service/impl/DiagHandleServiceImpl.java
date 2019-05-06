package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.linksteady.common.util.*;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.CommonSelectMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.DiagHandleService;
import com.linksteady.operate.util.PearsonCorrelationUtil;
import com.linksteady.operate.util.UomsConstants;
import com.linksteady.operate.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 诊断-数据处理
 * @author huang
 */
@Service
@Slf4j
public class DiagHandleServiceImpl implements DiagHandleService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CommonSelectMapper commonSelectMapper;

    @Override
    public void saveHandleInfoToRedis(DiagHandleInfo diagHandleInfo) {
        redisTemplate.opsForValue().set("diag:"+diagHandleInfo.getDiagId()+":"+diagHandleInfo.getKpiLevelId(),diagHandleInfo);
    }

    @Override
    public DiagHandleInfo getHandleInfoFromRedis(int diagId, int kpiLevelId) {
        return (DiagHandleInfo)redisTemplate.opsForValue().get("diag:"+diagId+":"+kpiLevelId);
    }

    @Override
    public void saveResultToRedis(DiagResultInfo diagResultInfo) {
        redisTemplate.opsForValue().set("diagresult:"+diagResultInfo.getDiagId()+":"+diagResultInfo.getKpiLevelId(),diagResultInfo);
    }

    @Override
    public DiagResultInfo getResultFromRedis(int diagId, int kpiLevelId) {
        return (DiagResultInfo)redisTemplate.opsForValue().get("diagresult:"+diagId+":"+kpiLevelId);
    }


    @Override
    public DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo) {
        //获取到操作类型
        String handleType=diagHandleInfo.getHandleType();
        DiagResultInfo result=null;

        List<String> periodList=null;
        //获取周期内的每个明细单位 //按月
        if("M".equals(diagHandleInfo.getPeriodType()))
        {
            periodList= DateUtil.getMonthBetween(diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        }else  //按天
        {
            periodList=DateUtil.getEveryday(diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        }

        //乘法
        if("M".equals(handleType))
        {
             result=processMultiple(diagHandleInfo,periodList);
        }else if("A".equals(handleType))
        {
             //加法
             result=processAdd(diagHandleInfo,periodList);
        }else if("F".equals(handleType))
        {
            //过滤
            result=processFilter(diagHandleInfo);
        }

        result.setDiagId(diagHandleInfo.getDiagId());
        result.setKpiLevelId(diagHandleInfo.getKpiLevelId());
        result.setBeginDt(diagHandleInfo.getBeginDt());
        result.setEndDt(diagHandleInfo.getEndDt());
        result.setPeriodType(diagHandleInfo.getPeriodType());
        result.setHandleDesc(diagHandleInfo.getHandleDesc());
        result.setHandleType(handleType);

        result.setKpiCode(diagHandleInfo.getKpiCode());
        result.setKpiName(KpiCacheManager.getInstance().getDiagKpiList().get(diagHandleInfo.getKpiCode()).getKpiName());

        //增加条件信息
        result.setWhereinfo(diagHandleInfo.getWhereinfo());

        //result信息持久化到redis
        saveResultToRedis(result);

        return result;
    }


    /**
     * 处理乘法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processMultiple(DiagHandleInfo diagHandleInfo, List<String> periodList)
    {
        //返回的结果集对象
        DiagMultResultInfo diagMultResultInfo=new DiagMultResultInfo();

        Map<String,String> codeNamePair=KpiCacheManager.getInstance().getKpiCodeNamePair();
        //获取当前要进行乘法的指标编码和指标名称
        String kpiCode=diagHandleInfo.getKpiCode();
        //获取其拆分为的两个指标
        KpiDismantInfo kpiDismantInfo=KpiCacheManager.getInstance().getKpiDismant().get(kpiCode);
        String part1Code=kpiDismantInfo.getDismantPart1Code();
        String part2Code=kpiDismantInfo.getDismantPart2Code();

        diagMultResultInfo.setFirYName(KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode).getAxisName());
        diagMultResultInfo.setSecYName(KpiCacheManager.getInstance().getDiagKpiList().get(part1Code).getAxisName());
        diagMultResultInfo.setThirdYName(KpiCacheManager.getInstance().getDiagKpiList().get(part2Code).getAxisName());
        diagMultResultInfo.setXName("D".equals(diagHandleInfo.getPeriodType())?"天":"月份");

        //根据配置的模板读取模板数据
        //判断用户选择的维度中是否含有品牌、SPU 如果有，则获取 从明细查询的模板
        KpiSqlTemplateVO kpiSqlTemplate=null;
        List<DiagComnCollector> collectorData=null;
        List<DiagComnCollector> covData=null;
        if(isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+kpiCode.toUpperCase()+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+kpiCode.toUpperCase());
        }

        //判断是否拿到了模板
        if(null!=kpiSqlTemplate&& !StringUtils.isBlank(kpiSqlTemplate.getSqlTemplate())) {
            //构造参数 填充到模板中
            StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());
            //计算变异系数的模板
            StringTemplate covTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

            //构造$JOIN_TABLE$字符串和 $WHERE_INFO$ $PERIOD_TYPE$
            List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());
            TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters);

            //$DATE_RANGE$
            String data_range=buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

            //$PERIOD_NAME$
            String periodName=buildPeriodInfo(diagHandleInfo.getPeriodType());

            stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                    .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",data_range).add("$PERIOD_NAME$",periodName);

            if(log.isDebugEnabled())
            {
                log.debug(stringTemplate.render());
            }
            //发送SQL到数据库中执行，并获取结果
            collectorData=commonSelectMapper.selectCollectorDataBySql(stringTemplate.render());

            //如果周期是月，额外计算变异系数
            if(UomsConstants.PERIOD_TYPE_MONTH.equals(diagHandleInfo.getPeriodType()))
            {
                //固定按天获取明细数据
                covTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                        .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",data_range).add("$PERIOD_NAME$",buildPeriodInfo(UomsConstants.PERIOD_TYPE_DAY));

                covData=commonSelectMapper.selectCollectorDataBySql(covTemplate.render());
                processMultiCovInfo(diagMultResultInfo,kpiCode,part1Code,part2Code,periodList,covData);
            }
        }

        LinkedList<Double> firData= Lists.newLinkedList();
        LinkedList<Double> secData= Lists.newLinkedList();
        LinkedList<Double> thirdData= Lists.newLinkedList();

        if(null!=collectorData&&collectorData.size()>0)
        {
             collectorData.stream().forEach(s->{
                 try {
                     firData.add((Double) s.getClass().getDeclaredField(kpiCode).get(s));
                     secData.add((Double)s.getClass().getDeclaredField(part1Code).get(s));
                     thirdData.add((Double)s.getClass().getDeclaredField(part2Code).get(s));
                 } catch (IllegalAccessException e) {
                     e.printStackTrace();
                 } catch (NoSuchFieldException e) {
                     e.printStackTrace();
                 }
             });
        }

        // todo 是否考虑对结果进行修正 修补周期不存在的数据

        diagMultResultInfo.setFirData(firData);
        diagMultResultInfo.setSecData(secData);
        diagMultResultInfo.setThirdData(thirdData);
        diagMultResultInfo.setXData(periodList);

        //末期比基期的变化率
        diagMultResultInfo.setFirChangeRate(ArithUtil.formatDouble(((firData.getLast()-firData.getFirst())/firData.getFirst()*100),2));
        diagMultResultInfo.setSecChangeRate(ArithUtil.formatDouble(((secData.getLast()-secData.getFirst())/secData.getFirst()*100),2));
        diagMultResultInfo.setThirdChangeRate(ArithUtil.formatDouble(((thirdData.getLast()-thirdData.getFirst())/thirdData.getFirst()*100),2));

        //均值及上下5%区域；
        Double firavg=firData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        Double secavg=secData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        Double thirdavg=thirdData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();

        diagMultResultInfo.setFirAvg(ArithUtil.formatDouble(firavg,2));
        diagMultResultInfo.setFirUp(ArithUtil.formatDouble(firavg*1.05,2));
        diagMultResultInfo.setFirDown(ArithUtil.formatDouble(firavg*0.95,2));

        diagMultResultInfo.setSecAvg(ArithUtil.formatDouble(secavg,2));
        diagMultResultInfo.setSecUp(ArithUtil.formatDouble(secavg*1.05,2));
        diagMultResultInfo.setSecDown(ArithUtil.formatDouble(secavg*0.95,2));

        diagMultResultInfo.setThirdAvg(ArithUtil.formatDouble(thirdavg,2));
        diagMultResultInfo.setThirdUp(ArithUtil.formatDouble(thirdavg*1.05,2));
        diagMultResultInfo.setThirdDown(ArithUtil.formatDouble(thirdavg*0.95,2));

        //计算相关系数
        processMultiRelateInfo(diagMultResultInfo,codeNamePair.get(kpiCode),codeNamePair.get(part1Code),codeNamePair.get(part2Code),firData,secData,thirdData);
        return diagMultResultInfo;
    }

    /**
     * 处理乘法的变异系数
     */
    private void processMultiCovInfo(DiagMultResultInfo diagMultResultInfo,String kpiCode,String part1Code,String part2Code,List<String> periodList, List<DiagComnCollector> covData)
    {
        List<Double> firData=Lists.newArrayList();
        List<Double> secData=Lists.newArrayList();
        List<Double> thirdData=Lists.newArrayList();

        //变异系数值
        List<Double> firCov=Lists.newArrayList();
        List<Double> secCov=Lists.newArrayList();
        List<Double> thirdCov=Lists.newArrayList();
        //变异系数值
        Map<String,List<Double>> covValues= Maps.newHashMap();
        Map<String,String> covNames=Maps.newHashMap();

        //获取两个日期之间所有按日的指标数据
        for(String period:periodList)
        {
            //清空存放当前周期下每天数据的几个列表
            firData.clear();
            secData.clear();
            thirdData.clear();

            //从所有数据中获取当前周期所有的数据
             covData.stream().filter(a->period.equals(a.getPeriodName().substring(0,7))).forEach(s->{
                 try {
                     firData.add((Double) s.getClass().getDeclaredField(kpiCode).get(s));
                     secData.add((Double)s.getClass().getDeclaredField(part1Code).get(s));
                     thirdData.add((Double)s.getClass().getDeclaredField(part2Code).get(s));
                 } catch (IllegalAccessException e) {
                     e.printStackTrace();
                 } catch (NoSuchFieldException e) {
                     e.printStackTrace();
                 }
             });
            //计算变异系数

            //均值
            System.out.println(period);
            Double firavg=firData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            Double secavg=secData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            Double thirdavg=thirdData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();

            //标准差
            Double firStand=DataStatisticsUtils.getStandardDevitionByList(firData);
            Double secStand=DataStatisticsUtils.getStandardDevitionByList(firData);
            Double thirdStand=DataStatisticsUtils.getStandardDevitionByList(firData);

            firCov.add(firStand==0?-1:ArithUtil.formatDoubleByMode(firavg/firStand*100,2,RoundingMode.DOWN));
            secCov.add(secStand==0?-1:ArithUtil.formatDoubleByMode(secavg/secStand*100,2,RoundingMode.DOWN));
            thirdCov.add(thirdStand==0?-1:ArithUtil.formatDoubleByMode(thirdavg/thirdStand*100,2,RoundingMode.DOWN));
        }

        covValues.put("cov1",firCov);
        covValues.put("cov2",secCov);
        covValues.put("cov3",thirdCov);
        diagMultResultInfo.setCovValues(covValues);

        Map<String,String> codeNamePair=KpiCacheManager.getInstance().getKpiCodeNamePair();
        covNames.put("cov1",codeNamePair.get(kpiCode)+"变异系数");
        covNames.put("cov2",codeNamePair.get(part1Code)+"变异系数");
        covNames.put("cov3",codeNamePair.get(part2Code)+"变异系数");

        diagMultResultInfo.setCovNames(covNames);
    }

    /**
     * 处理乘法 - 相关性数据
     */
    private void  processMultiRelateInfo(DiagMultResultInfo diagMultResultInfo,String kpiName,String part1Name,String part2Name,
                                            LinkedList<Double> firData,
                                            LinkedList<Double> secData,
                                            LinkedList<Double> thirdData)
    {
        //处理相关性
        JSONObject relObj=new JSONObject();
        JSONArray relArray=new JSONArray();
        JSONObject link=new JSONObject();

        //计算firData和secData的相关系数
        link.put("name",part1Name);
        link.put("data",ArithUtil.formatDoubleByMode(PearsonCorrelationUtil.getPearsonCorrelationScoreByList(firData,secData), 2,RoundingMode.DOWN));
        relArray.add(link);

        //计算firData和thridData的相关系数
        link=new JSONObject();
        link.put("name",part2Name);
        link.put("data",ArithUtil.formatDoubleByMode(PearsonCorrelationUtil.getPearsonCorrelationScoreByList(firData,thirdData), 2,RoundingMode.DOWN));
        relArray.add(link);

        relObj.put("data",relArray);
        relObj.put("name",kpiName);

        diagMultResultInfo.setRelate(relObj);
    }



    /**
     * 处理加法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processAdd(DiagHandleInfo diagHandleInfo,List<String> periodList)
    {
        DiagAddResultInfo diagAddResultInfo=new DiagAddResultInfo();

        //获取指标名称
        String kpiCode=diagHandleInfo.getKpiCode();

        //存放维度值名称的列表
        List<String> dimNames=Lists.newArrayList();
        //存放维度值编码的列表
        List<String> dimValues=null;
        //获取进行加法操作的维度编码
        String dimCode=diagHandleInfo.getAddDimCode();

        //当前维度下，其具有的值列表 <值编码，值名称>
        Map<String,String> diagDimValue=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode);

        //加法要插入的节点列表
        JSONArray nodeArray=new JSONArray();
        JSONObject node=null;

        //如果用户没选择维度值，则系统去取TOP5的
        if(null==diagHandleInfo.getAddDimValues()||"".equals(diagHandleInfo.getAddDimValues()))
        {
            dimValues=getTop5DimValues();
        }else
        {
            dimValues=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(diagHandleInfo.getAddDimValues());

        }

        //要插入的节点列表
        for(String v:dimValues)
        {
            dimNames.add(diagDimValue.get(v));
            node=new JSONObject();
            node.put("code",v);
            node.put("name",diagDimValue.get(v));
            nodeArray.add(node);
        }

        //设置图例名称列表
        diagAddResultInfo.setLegendData(dimNames);
        diagAddResultInfo.setXname("D".equals(diagHandleInfo.getPeriodType())?"天":"月份");
        //要插入的新节点信息
        diagAddResultInfo.setNodeList(nodeArray);
        //x轴的数据
        diagAddResultInfo.setXdata(periodList);


        //首先计算各周期，各维度值下GMV的值





        //用户选择
    //    int dimSize=dimValues.size();

//        Map<String,Integer> pctMap=Maps.newHashMap();
//        int pctSum=0;
//        //随机获得拆分比例
//        for(int i=0;i<dimSize;i++)
//        {
//              if(i==dimSize-1)
//              {
//                  pctMap.put(dimValues.get(i),100-pctSum);
//              }else
//              {
//                  pctSum+=5*i+10;
//                  pctMap.put(dimValues.get(i),5*i+10);
//              }
//        }






        //如果用户选择的指标非GMV，则计算当前选择维度值在此指标下的折线图 以及均线

        //计算总体 及各部分在此指标下的变异系数

        //计算总体及各部分的相关性

        //变异系数
        JSONArray covArray=new JSONArray();
        JSONObject covObj=new JSONObject();
        covObj.put("name","总体");
        covObj.put("data",getRandomKpiData(diagHandleInfo.getPeriodType(),"cov"));
        covArray.add(covObj);

        for(String dv:dimValues)
        {
            covObj=new JSONObject();
            covObj.put("name",diagDimValue.get(dv));
            covObj.put("data",getRandomKpiData(diagHandleInfo.getPeriodType(),"cov"));

            covArray.add(covObj);
        }

        //相关性
        JSONArray relateArray=new JSONArray();
        JSONObject relateObj=new JSONObject();
        relateObj.put("name","总体");
        relateObj.put("data",getRandomKpiData(diagHandleInfo.getPeriodType(),"relate"));
        relateArray.add(relateObj);

        for(String dv:dimValues)
        {
            relateObj=new JSONObject();
            relateObj.put("name",diagDimValue.get(dv));
            relateObj.put("data",getRandomKpiData(diagHandleInfo.getPeriodType(),"relate"));

            relateArray.add(relateObj);
        }


        //先获取到gmv的值集
        Map<String,Double> gmvMap=Maps.newHashMap();
        for(String period:periodList)
        {
            gmvMap.put(period,getRandomKpiData(period,"gmv"));
        }

        JSONArray areaData=new JSONArray();
        JSONObject tempObj=null;
        LinkedList<Double> tempValue=null;

        //计算gmv各部分的值 面积图
        //按维度拆分
//        for(String dv:dimValues)
//        {
//            tempObj=new JSONObject();
//            tempObj.put("name",diagDimValue.get(dv));
//            tempValue=Lists.newLinkedList();
//
//            for(String period:periodList)
//            {
//               double gmv=gmvMap.get(period);
//               int pct=  pctMap.get(dv);  //当前维度的拆分比例
//
//               tempValue.add(ArithUtil.formatDouble(gmv*pct/100.00,2));
//            }
//
//            tempObj.put("data",tempValue);
//            areaData.add(tempObj);
//        }

        JSONArray  othersData=new JSONArray();
        JSONObject othersObj=null;
        List<Double> othersValue=null;

        JSONArray lineAvgData=new JSONArray();
        JSONObject avgObj=null;

        //判断当前是否选择的gmv
        if(!"gmv".equals(kpiCode))   //非核心指标 其它指标各部分的趋势图与均线;
        {
            for(String dv:dimValues)  //遍历维度值
            {
                othersValue=Lists.newArrayList();
                othersObj=new JSONObject();
                avgObj=new JSONObject();

                for(String period:periodList)
                {
                    othersValue.add(getRandomKpiData(period,kpiCode));  //其它指标的值
                }

                othersObj.put("name",diagDimValue.get(dv));
                othersObj.put("data",othersValue);

                //计算均值
                avgObj.put("name",diagDimValue.get(dv));
                avgObj.put("data",othersValue.stream().mapToDouble(Double::doubleValue).average().getAsDouble());

                othersData.add(othersObj);
                lineAvgData.add(avgObj);
            }
        }

        //变异系数表格(仅针对GMV)
        diagAddResultInfo.setAreaData(areaData);
        diagAddResultInfo.setLineData(othersData);
        diagAddResultInfo.setLineAvgData(lineAvgData);
        diagAddResultInfo.setCovData(covArray);
        diagAddResultInfo.setRelateData(relateArray);



        //相关性

        return diagAddResultInfo;
    }


    /**
     * 加法 获取top5的维度名称
     */
    private List<String>  getTop5DimValues()
    {
        return null;
    }

    /**
     * 处理过滤运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processFilter(DiagHandleInfo diagHandleInfo)
    {
        DiagResultInfo resultInfo=new DiagResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();
        String kpiName=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode).getKpiName();

        double kpiValue=0d;

        KpiSqlTemplateVO kpiSqlTemplate=null;
        //判断用户选择的维度中是否含有品牌、SPU 如果有，则获取 从明细查询的模板
        if(isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+kpiCode.toUpperCase()+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+kpiCode.toUpperCase());
        }

       //判断是否拿到了模板
        if(null!=kpiSqlTemplate&& !StringUtils.isBlank(kpiSqlTemplate.getSqlTemplate())) {
            //构造参数 填充到模板中
            StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

            //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
            List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());
            TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters);

            //$DATE_RANGE$
            String data_range=buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

            stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                    .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",data_range);

            if(log.isDebugEnabled())
            {
                log.debug(stringTemplate.render());
            }
            //发送SQL到数据库中执行，并获取结果
            kpiValue=commonSelectMapper.selectOnlyDoubleValue(stringTemplate.render());
        }

        resultInfo.setKpiCode(kpiCode);
        resultInfo.setKpiName(kpiName);
        resultInfo.setKpiValue(kpiValue);
        return resultInfo;
    }

    /**
     * 根据周期类型 返回对应的列
     * @param periodType
     * @return
     */
     private String buildPeriodInfo(String periodType)
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

     /**
     * @param periodType 周期类型 M表示月 Y表示年 D表示天
     * @param  beginDt  YYYY-MM-DD格式  月份为YYYYMM格式  年为YYYY格式
     * @param  endDt
     * @return 构造完成的字符串
     */
    private String buildDateRange(String periodType,String beginDt,String endDt)
    {
        StringBuffer bf=new StringBuffer();
         if(UomsConstants.PERIOD_TYPE_MONTH.equals(periodType))
         {
            bf.append(" AND W_DATE.MONTH>=").append(StringUtils.replaceChars(beginDt,"-","")).append(" AND W_DATE.MONTH<=").append(StringUtils.replaceChars(endDt,"-",""));
         }else if(UomsConstants.PERIOD_TYPE_YEAR.equals(periodType))
         {
             bf.append(" AND W_DATE.MONTH>=").append(StringUtils.replaceChars(beginDt,"-","")).append(" AND W_DATE.MONTH<=").append(StringUtils.replaceChars(endDt,"-",""));
         }else if(UomsConstants.PERIOD_TYPE_DAY.equals(periodType))
         {
             bf.append(" AND W_DATE.YEAR>=").append(beginDt).append(" AND W_DATE.YEAR<=").append(endDt);
         }

         return bf.toString();
    }



    /**
     *
     * @param driverTableName  驱动表名称
     * @param filterInfo  所选的维度信息列表
     * @return TemplateResult 返回构建好的join信息和where信息
     */
    private TemplateResult buildWhereInfo(String driverTableName,List<TemplateFilter> filterInfo)
    {
        Map<String, DimJoinVO> dimJoin=KpiCacheManager.getInstance().getDimJoinList().row(driverTableName);

        StringBuffer joins=new StringBuffer();
        StringBuffer filters=new StringBuffer();

        Set dimTableAlias= Sets.newHashSet();

        StringBuffer join=new StringBuffer();
        StringBuffer filter=new StringBuffer();
        Joiner joiner = Joiner.on(",").skipNulls();

        for(TemplateFilter templateFilter :filterInfo)
        {
            //清空
            filter.setLength(0);
            join.setLength(0);

            //通过dimcode获取到其背后的信息
            DimJoinVO dimJoinVO=dimJoin.get(templateFilter.getDimCode());

            //判断dim table是否已经存在(通过DIM_TABLE_ALIAS判断)
            if(!dimTableAlias.contains(dimJoinVO.getDimTableAlias()))
            {
                //加入到判断重复的set中
                dimTableAlias.add(dimJoinVO.getDimTableAlias());
                //加入到拼接队列
                join.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());
            }

            //where条件
            List<String> values=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(templateFilter.getDimValues());
            //字符串类型
            if("STRING".equals(dimJoinVO.getDimWhereType()))
            {
                  if(values.size()==1)
                  {
                      filter.append(" AND ").append(dimJoinVO.getDimWhere()).append("='").append(values.get(0)).append("'");
                  }else
                  {
                      filter.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values.stream().map(a->"'"+a+"'").toArray())).append(")");
                  }
            }else if("NUMBER".equals(dimJoinVO.getDimWhereType())) {
                //数字类型
                if (values.size() == 1) {
                    filter.append(" AND ").append(dimJoinVO.getDimWhere()).append("=").append(values.get(0));
                } else {
                    filter.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values)).append(")");
                }
            }

            filters.append(filter.toString());
            joins.append(join.toString());
        }

        TemplateResult result=new TemplateResult();
        result.setFilterInfo(filters.toString());
        result.setJoinInfo(joins.toString());

        return result;
    }

    /**
     * 是否依赖于订单明细表
     * @return
     */
    private boolean isRelyOrderDetail(List<DiagConditionVO> whereInfo)
    {
        if(null==whereInfo||whereInfo.size()==0)
        {
            return false;
        }

        List<String> selectDimCodeList=whereInfo.stream().map(DiagConditionVO::getDimCode).collect(Collectors.toList());

        List<String> relyDimCodeList=KpiCacheManager.getInstance().getDimConfigList().stream()
                .filter(a->"Y".equals(a.getRelyOrderDetail())).map(DimConfigInfo::getDimCode).collect(Collectors.toList());


        List<String> intersection = selectDimCodeList.stream().filter(item -> relyDimCodeList.contains(item)).collect(Collectors.toList());
       return intersection.size()>0?true:false;
    }

    private double getRandomKpiData(String period,String kpiCode)
    {
        double  result =0d;
       if("gmv".equals(kpiCode))
       {
           if("M".equals(period))
           {
                result= RandomUtil.getIntRandom(1000000,1200000);
           }else
           {
               result= RandomUtil.getIntRandom(35000,50000);
           }

       }
       else if ("ucnt".equals(kpiCode))  //用户数
       {
            if("M".equals(period))
            {
                result= RandomUtil.getIntRandom(1800,2200);
            }else
            {
                result= RandomUtil.getIntRandom(50,80);
            }
       }
       else if ("uprice".equals(kpiCode))  //客单价
       {
           if("M".equals(period))
           {
               result= RandomUtil.getIntRandom(500,600);
           }else
           {
               result=RandomUtil.getIntRandom(500,600);
           }
       }
       else if ("price".equals(kpiCode))  //订单价
       {
           if("M".equals(period))
           {
               result=RandomUtil.getIntRandom(400,600);
           }else
           {
               result= RandomUtil.getIntRandom(400,600);
           }
       }
       else if ("pcnt".equals(kpiCode))  //订单数
       {
           if("M".equals(period))
           {
               result= RandomUtil.getIntRandom(2500,3000);
           }else
           {
               result= RandomUtil.getIntRandom(80,120);
           }
       }
       else if ("joinrate".equals(kpiCode))  //连带率  1.5-2.5
       {
           result=RandomUtil.getIntRandom(15,25)/10.00;
       }
       else if ("sprice".equals(kpiCode))  //件单价
       {
           result= RandomUtil.getIntRandom(200,300);
       }
       else if ("sprice2".equals(kpiCode))  //吊牌价
       {
           result= RandomUtil.getIntRandom(250,350);
       }
       else if ("disrate".equals(kpiCode))  //折扣率 0.7 -0.9
       {
           result= RandomUtil.getIntRandom(70,90)/100.00;
       }
       else if ("freq".equals(kpiCode))  //购买频次  1.1-1.5
       {
           result=RandomUtil.getIntRandom(11,15)/10.00;
       }
       else if ("tspan".equals(kpiCode))  //时间长度
       {
           result= 0.0d;
       }
       else if ("cov".equals(kpiCode))  //变异系数
       {
           result= RandomUtil.getIntRandom(40,55)/10.00;
       }
       else if ("relate".equals(kpiCode))  //相关性
       {
           result= RandomUtil.getIntRandom(10,100)/100.00;
       }

       return result;
    }

    /**
     * 判断指标在时间维度上是否具有可加性
     * @param kpiCode
     * @return true具有可加性 false 不具有可加性
     */
    private boolean isSum(String kpiCode)
    {
       if("uprice".equals(kpiCode)||"price".equals(kpiCode)||"joinrate".equals(kpiCode)||"sprice".equals(kpiCode)||"sprice2".equals(kpiCode)||"disrate".equals(kpiCode)||"tspan".equals(kpiCode)||"freq".equals(kpiCode))
       {
           return false;
       }else
       {
           return true;
       }
    }
}
