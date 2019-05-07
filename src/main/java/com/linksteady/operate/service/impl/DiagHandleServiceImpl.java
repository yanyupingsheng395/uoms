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
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
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

        //乘法
        if(UomsConstants.DIAG_OPERATION_TYPE_MULTI.equals(handleType))
        {
             result=processMultiple(diagHandleInfo);
        }else if(UomsConstants.DIAG_OPERATION_TYPE_ADD.equals(handleType))
        {   //加法
             result=processAdd(diagHandleInfo);
        }else if(UomsConstants.DIAG_OPERATION_TYPE_FILTER.equals(handleType))
        {   //过滤
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
    private DiagResultInfo processMultiple(DiagHandleInfo diagHandleInfo)
    {
        List<String> periodList=getPeriodList(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        //返回的结果集对象
        DiagMultResultInfo diagMultResultInfo=new DiagMultResultInfo();

        Map<String,String> codeNamePair=KpiCacheManager.getInstance().getKpiCodeNamePair();
        //获取当前要进行乘法的指标编码和指标名称
        String kpiCode=diagHandleInfo.getKpiCode();
        //获取其拆分为的两个指标
        KpiDismantInfo kpiDismantInfo=KpiCacheManager.getInstance().getKpiDismant().get(kpiCode);
        String part1Code=kpiDismantInfo.getDismantPart1Code();
        String part2Code=kpiDismantInfo.getDismantPart2Code();

        //获取三个指标的值格式化类型
        String firFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode).getValueFormat();
        String secFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(part1Code).getValueFormat();
        String thirdFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(part2Code).getValueFormat();

        //设置轴的名称
        diagMultResultInfo.setFirYName(KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode).getAxisName());
        diagMultResultInfo.setSecYName(KpiCacheManager.getInstance().getDiagKpiList().get(part1Code).getAxisName());
        diagMultResultInfo.setThirdYName(KpiCacheManager.getInstance().getDiagKpiList().get(part2Code).getAxisName());
        diagMultResultInfo.setXName("D".equals(diagHandleInfo.getPeriodType())?"天":"月份");

        //根据配置的模板读取模板数据 判断用户选择的维度中是否含有品牌、SPU 如果有，则获取 从明细查询的模板
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


            //固定按天获取明细数据
            covTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                    .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",data_range).add("$PERIOD_NAME$",buildPeriodInfo(UomsConstants.PERIOD_TYPE_DAY));

            covData=commonSelectMapper.selectCollectorDataBySql(covTemplate.render());
            processMultiCovInfo(diagMultResultInfo,kpiCode,part1Code,part2Code,periodList,covData);
        }

        LinkedList<Double> firData= Lists.newLinkedList();
        LinkedList<Double> secData= Lists.newLinkedList();
        LinkedList<Double> thirdData= Lists.newLinkedList();

        Map<String,DiagComnCollector> diagComnCollectorMap=collectorData.stream().collect(Collectors.toMap(DiagComnCollector::getPeriodName, Function.identity()));
        periodList.stream().forEach(p->{
            if(null!=diagComnCollectorMap.get(p))
            {
                 DiagComnCollector s=diagComnCollectorMap.get(p);
                try {
                    firData.add((Double)s.getClass().getDeclaredField(kpiCode).get(s));
                    secData.add((Double)s.getClass().getDeclaredField(part1Code).get(s));
                    thirdData.add((Double)s.getClass().getDeclaredField(part2Code).get(s));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }else
            {
                firData.add(0.0d);
                secData.add(0.0d);
                thirdData.add(0.0d);
            }
        });

        diagMultResultInfo.setFirData(valueFormat(firData,firFormatType));
        diagMultResultInfo.setSecData(valueFormat(secData,secFormatType));
        diagMultResultInfo.setThirdData(valueFormat(thirdData,thirdFormatType));
        diagMultResultInfo.setXData(periodList);

        //末期比基期的变化率
        diagMultResultInfo.setFirChangeRate(valueFormat((firData.getLast()-firData.getFirst())/firData.getFirst()*100,firFormatType));
        diagMultResultInfo.setSecChangeRate(valueFormat((secData.getLast()-secData.getFirst())/secData.getFirst()*100,secFormatType));
        diagMultResultInfo.setThirdChangeRate(valueFormat((thirdData.getLast()-thirdData.getFirst())/thirdData.getFirst()*100,thirdFormatType));

        //均值及上下5%区域；
        double firavg=firData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double secavg=secData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double thirdavg=thirdData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();


        diagMultResultInfo.setFirAvg(valueFormat(firavg,firFormatType));
        diagMultResultInfo.setFirUp(valueFormat(firavg*1.05,firFormatType));
        diagMultResultInfo.setFirDown(valueFormat(firavg*0.95,firFormatType));

        diagMultResultInfo.setSecAvg(valueFormat(secavg,secFormatType));
        diagMultResultInfo.setSecUp(valueFormat(secavg*1.05,secFormatType));
        diagMultResultInfo.setSecDown(valueFormat(secavg*0.95,secFormatType));

        diagMultResultInfo.setThirdAvg(valueFormat(thirdavg,thirdFormatType));
        diagMultResultInfo.setThirdUp(valueFormat(thirdavg*1.05,thirdFormatType));
        diagMultResultInfo.setThirdDown(valueFormat(thirdavg*0.95,thirdFormatType));

        //计算相关系数
        processMultiRelateInfo(diagMultResultInfo,codeNamePair.get(kpiCode),codeNamePair.get(part1Code),codeNamePair.get(part2Code),firData,secData,thirdData);
        return diagMultResultInfo;
    }

    /**
     * 处理乘法的变异系数
     */
    private void processMultiCovInfo(DiagMultResultInfo diagMultResultInfo,String kpiCode,String part1Code,String part2Code,List<String> periodList, List<DiagComnCollector> covData)
    {
        //计算变异系数的数据集
        List<Double> firData=Lists.newArrayList();
        List<Double> secData=Lists.newArrayList();
        List<Double> thirdData=Lists.newArrayList();

        //变异系数值
        Map<String,Double> covValues= Maps.newHashMap();

        Map<String,DiagComnCollector> diagComnCollectorMap=covData.stream().collect(Collectors.toMap(DiagComnCollector::getPeriodName, Function.identity()));
        //从所有数据中获取当前周期所有的数据
        periodList.stream().forEach(p->{
            if(null!=diagComnCollectorMap.get(p))
            {
                DiagComnCollector s=diagComnCollectorMap.get(p);
                try {
                    firData.add((Double) s.getClass().getDeclaredField(kpiCode).get(s));
                    secData.add((Double)s.getClass().getDeclaredField(part1Code).get(s));
                    thirdData.add((Double)s.getClass().getDeclaredField(part2Code).get(s));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }else
            {
                firData.add(0.0d);
                secData.add(0.0d);
                thirdData.add(0.0d);
            }
        });

        //计算变异系数
        //均值
        Double firavg=firData.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        Double secavg=secData.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        Double thirdavg=thirdData.stream().mapToDouble(Double::doubleValue).average().orElse(0d);

        //标准差
        Double firStand=DataStatisticsUtils.getStandardDevitionByList(firData);
        Double secStand=DataStatisticsUtils.getStandardDevitionByList(secData);
        Double thirdStand=DataStatisticsUtils.getStandardDevitionByList(thirdData);

        double firCov=(firStand==0?0:ArithUtil.formatDoubleByMode(firStand/firavg*100,2,RoundingMode.DOWN));
        double secCov=(secStand==0?0:ArithUtil.formatDoubleByMode(secStand/secavg*100,2,RoundingMode.DOWN));
        double thirdCov=(thirdStand==0?0:ArithUtil.formatDoubleByMode(thirdStand/thirdavg*100,2,RoundingMode.DOWN));

        Map<String,String> codeNamePair=KpiCacheManager.getInstance().getKpiCodeNamePair();
        covValues.put(codeNamePair.get(kpiCode)+"变异系数",firCov);
        covValues.put(codeNamePair.get(part1Code)+"变异系数",secCov);
        covValues.put(codeNamePair.get(part2Code)+"变异系数",thirdCov);
        diagMultResultInfo.setCovValues(covValues);

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
    private DiagResultInfo processAdd(DiagHandleInfo diagHandleInfo)
    {
        DiagAddResultInfo diagAddResultInfo=new DiagAddResultInfo();
        List<String> periodList=getPeriodList(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        List<String> dayPeriodList=getPeriodList(UomsConstants.PERIOD_TYPE_DAY,diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        //获取指标名称
        String kpiCode=diagHandleInfo.getKpiCode();
        //存放维度值名称的列表
        List<String> dimNames=Lists.newArrayList();
        //存放维度值编码的列表
        List<String> dimValues;
        //获取进行加法操作的维度编码
        String dimCode=diagHandleInfo.getAddDimCode();

        //当前维度下，其具有的值列表 <值编码，值名称>
        Map<String,String> diagDimValue=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode);

        //获取当前指标的数据格式化类型
        String lineFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode).getValueFormat();
        String areaFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(UomsConstants.DIAG_KPI_CODE_GMV).getValueFormat();

        //加法要插入的节点列表
        JSONArray nodeArray=new JSONArray();
        JSONObject node;

        //如果用户没选择维度值，则系统去取TOP5的
        if(null==diagHandleInfo.getAddDimValues()||"".equals(diagHandleInfo.getAddDimValues()))
        {
            dimValues=getTop5DimValues(diagHandleInfo,dimCode);
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

        JSONArray areaData=new JSONArray();
        JSONArray lineData=new JSONArray();
        JSONArray lineAvgData=new JSONArray();

        //首先计算当前周期下，各维度值下GMV的值
        List<DiagAddDataCollector>  diagAddDataCollectorList=getGmvAreaData(diagHandleInfo,dimCode,diagHandleInfo.getPeriodType(),"N");

        //按维度值编码进行分组进行分组
        Map<String,List<DiagAddDataCollector>> temp=diagAddDataCollectorList.parallelStream().collect(Collectors.groupingBy(DiagAddDataCollector::getDimValue,LinkedHashMap::new,Collectors.toList()));

        //组装到最后的返回对象中去
        temp.forEach((k,v)->{
            JSONObject tempObj=new JSONObject();
            tempObj.put("name",KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode).get(k));
            //对数据进行修补，如果出现某个周期上没有数据，则补0
            tempObj.put("data",valueFormat(fixData(v.stream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),periodList),areaFormatType));
            areaData.add(tempObj);
        });

        //如果用户选择的非gmv指标，则还需要计算其它指标的折线图
        if(!UomsConstants.DIAG_KPI_CODE_GMV.equals(kpiCode))
        {
            //如果用户选择的指标非GMV，则计算当前选择维度值在此指标下的折线图 以及均线
            List<DiagAddDataCollector> otherLineData=getOthersLineData(diagHandleInfo,dimCode,diagHandleInfo.getPeriodType(),"N");

            //按dimCode分组
            Map<String,List<DiagAddDataCollector>> temp6=otherLineData.parallelStream().collect(Collectors.groupingBy(DiagAddDataCollector::getDimValue,LinkedHashMap::new,Collectors.toList()));

            //组装到最后的返回对象中去
            temp6.forEach((k,v)->{
                String name=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode).get(k);

                JSONObject tempObj=new JSONObject();
                tempObj.put("name",name);
                List<Double> temp7=v.stream().map(DiagAddDataCollector::getValue).collect(Collectors.toList());
                tempObj.put("data",valueFormat(fixData(v.stream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),periodList),lineFormatType));
                lineData.add(tempObj);

                JSONObject avgObj=new JSONObject();
                avgObj.put("name",name);
                //求均值
                avgObj.put("data",valueFormat(temp7.stream().mapToDouble(Double::doubleValue).average().orElse(0d),lineFormatType));
                lineAvgData.add(avgObj);
            });
        }

        JSONArray covArray=new JSONArray();
        JSONArray relateArray=new JSONArray();
        List<DiagAddDataCollector> covList;
        List<DiagAddDataCollector> overallList;

        //计算 变异系数 获取当前指标按天的数据
        if(UomsConstants.DIAG_KPI_CODE_GMV.equals(kpiCode))
        {
            //按天的明细数据
            covList=getGmvAreaData(diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"N");
            //获取总体数据，仅有周期维度
            overallList=getGmvAreaData(diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"Y");
        }else
        {
            covList=getOthersLineData(diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"N");
            overallList=getGmvAreaData(diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"Y");
        }

        List<Double> overallDoubleList=fixData(overallList.parallelStream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),dayPeriodList);

        //按维度值编码进行分组进行分组
        Map<String,List<DiagAddDataCollector>> temp7=covList.parallelStream().collect(Collectors.groupingBy(DiagAddDataCollector::getDimValue,LinkedHashMap::new,Collectors.toList()));

        temp7.forEach((k,v)->{
            JSONObject covObj=new JSONObject();
            String dimValueName=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode).get(k);
            covObj.put("name",dimValueName);
            //计算变异系数
            List<Double> temp8=fixData(v.stream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),dayPeriodList);

            Double avg=temp8.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
            Double stand=DataStatisticsUtils.getStandardDevitionByList(temp8);
            covObj.put("data",(stand==0?0:ArithUtil.formatDoubleByMode(stand/avg*100,2,RoundingMode.DOWN)));
            covArray.add(covObj);

            //计算当前数据集与 总体数据的相关系数
            double relateValue=PearsonCorrelationUtil.getPearsonCorrelationScoreByList(temp8,overallDoubleList);
            JSONObject relateObj=new JSONObject();
            relateObj.put("name",dimValueName);
            relateObj.put("data",ArithUtil.formatDoubleByMode(relateValue,2,RoundingMode.DOWN));

            relateArray.add(relateObj);
        });

       //计算总体的变异系数
        double avg=overallDoubleList.parallelStream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double stand=DataStatisticsUtils.getStandardDevitionByList(overallDoubleList);
        JSONObject covObj=new JSONObject();
        covObj.put("name","总体");
        covObj.put("data",(stand==0?0:ArithUtil.formatDoubleByMode(stand/avg*100,2,RoundingMode.DOWN)));
        covArray.add(covObj);

        diagAddResultInfo.setAreaData(areaData);
        diagAddResultInfo.setLineData(lineData);
        diagAddResultInfo.setLineAvgData(lineAvgData);
        diagAddResultInfo.setCovData(covArray);
        diagAddResultInfo.setRelateData(relateArray);

        return diagAddResultInfo;
    }


    /**
     * 加法获取gmv面积图相关的数据
     */
    private List<DiagAddDataCollector> getGmvAreaData(DiagHandleInfo diagHandleInfo,String dimCode,String periodType,String isOverall)
    {
        KpiSqlTemplateVO kpiSqlTemplate;
        if(isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_GMV_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_GMV");
        }

        StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

        //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
        List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());

        TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters,dimCode);

        //$DATE_RANGE$
        String data_range=buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        String dimCodeParam=KpiCacheManager.getInstance().getDimJoinList().row(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$")).get(dimCode).getDimWhere();

        String periodName=buildPeriodInfo(periodType);

        //构造$GROUP_INFO$  $COLUMN_INFO$
        StringBuffer groupInfo=new StringBuffer();
        StringBuffer columnInfo=new StringBuffer();
        groupInfo.append(" GROUP BY ");
        if("N".equals(isOverall))
        {
            groupInfo.append(dimCodeParam).append(",").append(periodName);
            columnInfo.append(" ").append(dimCodeParam).append(" DIM_VALUE,").append(periodName).append(" PERIOD_NAME,");
        }else
        {
            //如果获取的是总体数据的话，则只需要根据 时间进行汇总，并不需要根据维度值进行group by
            groupInfo.append(periodName);
            columnInfo.append(" ").append(periodName).append(" PERIOD_NAME,");
        }

        stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",data_range).add("$COLUMN_INFO$",columnInfo.toString())
                .add("$GROUP_INFO$",groupInfo.toString()).add("$PERIOD_NAME$",periodName);

        List<DiagAddDataCollector> addDataCollectorList=commonSelectMapper.selectAddData(stringTemplate.render());
        return addDataCollectorList;
    }

    /**
     * 加法获取其它指标的折线图
     * @param diagHandleInfo 操作对象
     * @param dimCode 维度编码
     * @return  加法操作的返回值对象
     */
    private List<DiagAddDataCollector> getOthersLineData(DiagHandleInfo diagHandleInfo,String dimCode,String periodType,String isOverall)
    {
        KpiSqlTemplateVO kpiSqlTemplate;
        if(isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+dimCode.toUpperCase()+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+dimCode.toUpperCase());
        }

        StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

        //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
        List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());

        TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters,dimCode);

        //$DATE_RANGE$
        String dataRange=buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        //构造$DIM_CODE$
        String dimCodeParam=KpiCacheManager.getInstance().getDimJoinList().row(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$")).get(dimCode).getDimWhere();

        //构造$PERIOD_NAME$
        String periodName=buildPeriodInfo(periodType);

        //构造$GROUP_INFO$  $COLUMN_INFO$
        StringBuilder groupInfo=new StringBuilder();
        StringBuilder columnInfo=new StringBuilder();
        groupInfo.append(" GROUP BY ");
        if("N".equals(isOverall))
        {
            groupInfo.append(dimCodeParam).append(",").append(periodName);
            columnInfo.append(" ").append(dimCodeParam).append(" DIM_VALUE,").append(periodName).append(" PERIOD_NAME,");
        }else
        {
            //如果获取的是总体数据的话，则只需要根据 时间进行汇总，并不需要根据维度值进行group by
            groupInfo.append(periodName);
            columnInfo.append(" ").append(periodName).append(" PERIOD_NAME,");
        }

        stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",dataRange).add("$GROUP_INFO$",groupInfo.toString())
                .add("$COLUMN_INFO$",columnInfo.toString()).add("$PERIOD_NAME$",periodName);;

        return commonSelectMapper.selectAddData(stringTemplate.render());
    }

    /**
     * 加法 获取top5的维度名称
     */
    private List<String>  getTop5DimValues(DiagHandleInfo diagHandleInfo,String dimCode)
    {
        KpiSqlTemplateVO kpiSqlTemplate;
        if(isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get("A_TOP5_DIM_VALUE_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get("A_TOP5_DIM_VALUE");
        }

        StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

        //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
        List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());

        TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters,dimCode);

        //$DATE_RANGE$
        String dataRange=buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        //构造$DIM_CODE$
        String dimCodeParam=KpiCacheManager.getInstance().getDimJoinList().row(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$")).get(dimCode).getDimWhere();

        stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",dataRange).add("$DIM_CODE$",dimCodeParam);

        List<String> top5Values=commonSelectMapper.selectStringBySql(stringTemplate.render());

        //判断如果此时取不到 则默认取前5个
        if(null==top5Values || top5Values.size()==0)
        {
            top5Values=Lists.newArrayList();
            Map<String,String> diagDimValue=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode);
            int i=0;
            for (String key : diagDimValue.keySet()) {
                if(i<5)
                {
                    top5Values.add(key);
                    top5Values.add(diagDimValue.get(key));
                }
                i++;
            }
        }
        return top5Values;
    }

    /**
     * 处理过滤运算
     * @param diagHandleInfo 操作对象
     * @return 结果对象
     */
    private DiagResultInfo processFilter(DiagHandleInfo diagHandleInfo)
    {
        DiagResultInfo resultInfo=new DiagResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();
        KpiConfigInfo kpiConfigInfo=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode);
        String kpiName=kpiConfigInfo.getKpiName();
        String formatType=kpiConfigInfo.getValueFormat();

        double kpiValue=0d;

        KpiSqlTemplateVO kpiSqlTemplate;
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
            String dataRange=buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

            stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                    .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",dataRange);

            if(log.isDebugEnabled())
            {
                log.debug(stringTemplate.render());
            }
            //发送SQL到数据库中执行，并获取结果
            kpiValue=commonSelectMapper.selectOnlyDoubleValue(stringTemplate.render());
        }

        //对结果进行格式化

        resultInfo.setKpiCode(kpiCode);
        resultInfo.setKpiName(kpiName);
        resultInfo.setKpiValue(valueFormat(kpiValue,formatType));
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
     *  对于加法操作，即使在条件中没选择这个维度，也要将这个维度增加到join信息中去
     * @param driverTableName  驱动表名称
     * @param filterInfo  所选的维度信息列表
     * @param dimCode 维度类型
     * @return TemplateResult 返回构建好的join信息和where信息
     */
    private TemplateResult buildWhereInfo(String driverTableName,List<TemplateFilter> filterInfo,String dimCode)
    {
        Map<String, DimJoinVO> dimJoin=KpiCacheManager.getInstance().getDimJoinList().row(driverTableName);

        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        Set dimTableAliasSet= Sets.newHashSet();

        StringBuilder join=new StringBuilder();
        StringBuilder filter=new StringBuilder();
        Joiner joiner = Joiner.on(",").skipNulls();

        for(TemplateFilter templateFilter :filterInfo)
        {
            //清空
            filter.setLength(0);
            join.setLength(0);

            //通过dimcode获取到其背后的信息
            DimJoinVO dimJoinVO=dimJoin.get(templateFilter.getDimCode());

            //判断dim table是否已经存在(通过DIM_TABLE_ALIAS判断)
            if(!dimTableAliasSet.contains(dimJoinVO.getDimTableAlias()))
            {
                //加入到判断重复的set中
                dimTableAliasSet.add(dimJoinVO.getDimTableAlias());
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

        DimJoinVO  dimJoinVO=dimJoin.get(dimCode);
        //判断dim table是否已经存在(通过DIM_TABLE_ALIAS判断)
        if(!dimTableAliasSet.contains(dimJoinVO.getDimTableAlias()))
        {
            //加入到拼接队列
            join.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());
            joins.append(join.toString());
        }

        TemplateResult result=new TemplateResult();
        result.setFilterInfo(filters.toString());
        result.setJoinInfo(joins.toString());

        return result;
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

        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        Set dimTableAliasSet= Sets.newHashSet();

        StringBuilder join=new StringBuilder();
        StringBuilder filter=new StringBuilder();
        Joiner joiner = Joiner.on(",").skipNulls();

        for(TemplateFilter templateFilter :filterInfo)
        {
            //清空
            filter.setLength(0);
            join.setLength(0);

            //通过dimcode获取到其背后的信息
            DimJoinVO dimJoinVO=dimJoin.get(templateFilter.getDimCode());

            //判断dim table是否已经存在(通过DIM_TABLE_ALIAS判断)
            if(!dimTableAliasSet.contains(dimJoinVO.getDimTableAlias()))
            {
                //加入到判断重复的set中
                dimTableAliasSet.add(dimJoinVO.getDimTableAlias());
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
     * @return 如果依赖于订单明细，则返回true 否则返回false
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
       return intersection.size()>0;
    }

    /**
     * 修复数据  遍历连续的周期列表，判断当前周期列表是否在第一个数据集中有值，如果有，则返回第一个数据集中的值，如果没有，则填充0
     * @param datas 其key为周期字段，如YYYY-MM格式 或YYYY-MM-DD格式
     ** @param periodList 连续的周期类型列表
     * @return
     */
    private List<Double> fixData(Map<String,Double> datas,List<String> periodList)
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
     * 获取周期起始之间的每一个周期 如果period为M，则startDt和endDt必须为YYYY-MM格式，如果periodType为D,则startDt和endDt必须为YYYY-MM-DD格式
     */
    private List<String> getPeriodList(String periodType,String beginDt,String endDt)
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
     * 对指标结果进行格式化
     */
    private String valueFormat(Double value,String formatType)
    {
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
        if("D2".equals(formatType)||"D2F".equals(formatType))
        {
            val=ArithUtil.formatDoubleByMode(value,2,RoundingMode.DOWN);
        }else if("D4".equals(formatType)||"D4F".equals(formatType))
        {
            val=ArithUtil.formatDoubleByMode(value,4,RoundingMode.DOWN);
        }
        DecimalFormat decimalFormat= new DecimalFormat(pattern);

        return decimalFormat.format(value);
    }

    /**
     * 对指标结果进行格式化
     */
    private List<String> valueFormat(List<Double> value,String formatType)
    {
        return value.stream().map(s->valueFormat(s,formatType)).collect(Collectors.toList());
    }
}
