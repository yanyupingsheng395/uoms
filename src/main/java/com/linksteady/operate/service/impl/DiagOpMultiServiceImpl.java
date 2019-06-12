package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.StringTemplate;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.CommonSelectMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.DiagOpService;
import com.linksteady.operate.util.PearsonCorrelationUtil;
import com.linksteady.operate.util.UomsConstants;
import com.linksteady.operate.vo.DimJoinVO;
import com.linksteady.operate.vo.KpiSqlTemplateVO;
import com.linksteady.operate.vo.TemplateFilter;
import com.linksteady.operate.vo.TemplateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 诊断处理过程 - 乘法
 * @author  huang
 */
@Slf4j
@Service
public class DiagOpMultiServiceImpl implements DiagOpService {

    @Autowired
    DiagOpCommonServiceImpl diagOpCommonService;

    @Autowired
    CommonSelectMapper commonSelectMapper;

    @Override
    public DiagResultInfo process(DiagHandleInfo diagHandleInfo) {
        //用户选择的周期类型内所有的列表 （如果为月，则为月列表，如果为天，则为天列表)
        List<String> periodList=diagOpCommonService.getPeriodList(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        List<String> dayPeriodList=diagOpCommonService.getPeriodList(UomsConstants.PERIOD_TYPE_DAY,diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        //返回的结果集对象
        DiagMultResultInfo diagMultResultInfo=new DiagMultResultInfo();

        //获取当前要进行乘法的指标编码和指标名称
        String kpiCode=diagHandleInfo.getKpiCode();
        //获取其拆分为的两个指标
        KpiDismantInfo kpiDismantInfo=KpiCacheManager.getInstance().getKpiDismant().get(kpiCode);
        //如果第一个指标是tspan，需要特殊处理
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
        if(diagOpCommonService.isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
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
            String data_range=diagOpCommonService.buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

            //$PERIOD_NAME$
            String periodName=diagOpCommonService.buildPeriodInfo(diagHandleInfo.getPeriodType());

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
                    .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",data_range).add("$PERIOD_NAME$",diagOpCommonService.buildPeriodInfo(UomsConstants.PERIOD_TYPE_DAY));

            covData=commonSelectMapper.selectCollectorDataBySql(covTemplate.render());
            //计算变异系数和相关系数
            processMultiCovInfo(diagMultResultInfo,kpiCode,part1Code,part2Code,dayPeriodList,covData);
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
                    log.error("获取信息失败：", e);
                } catch (NoSuchFieldException e) {
                    log.error("获取信息失败：", e);
                }
            }else
            {
                firData.add(0.0d);
                secData.add(0.0d);
                thirdData.add(0.0d);
            }
        });

        diagMultResultInfo.setFirData(diagOpCommonService.valueFormat(firData,firFormatType));
        diagMultResultInfo.setSecData(diagOpCommonService.valueFormat(secData,secFormatType));
        diagMultResultInfo.setThirdData(diagOpCommonService.valueFormat(thirdData,thirdFormatType));
        diagMultResultInfo.setXData(periodList);

        //末期比基期的变化率
        Double firChangeRate=firData.getFirst()==0?null:(firData.getLast()-firData.getFirst())/firData.getFirst()*100;
        diagMultResultInfo.setFirChangeRate(diagOpCommonService.valueFormat(firChangeRate,"D2"));

        if(UomsConstants.DIAG_KPI_CODE_TSPAN.equals(part1Code))
        {
            diagMultResultInfo.setSecChangeRate("");
        }else
        {
            Double secChangeRate=secData.getFirst()==0?null:(secData.getLast()-secData.getFirst())/secData.getFirst()*100;
            diagMultResultInfo.setSecChangeRate(diagOpCommonService.valueFormat(secChangeRate,"D2"));
        }

        Double thirdChangeRate=thirdData.getFirst()==0?null:(thirdData.getLast()-thirdData.getFirst())/thirdData.getFirst()*100;
        diagMultResultInfo.setThirdChangeRate(diagOpCommonService.valueFormat(thirdChangeRate,"D2"));

        //均值及上下5%区域；
        double firavg=firData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double secavg=secData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double thirdavg=thirdData.stream().mapToDouble(Double::doubleValue).average().getAsDouble();


        diagMultResultInfo.setFirAvg(diagOpCommonService.valueFormat(firavg,firFormatType));
        diagMultResultInfo.setFirUp(diagOpCommonService.valueFormat(firavg*1.05,firFormatType));
        diagMultResultInfo.setFirDown(diagOpCommonService.valueFormat(firavg*0.95,firFormatType));

        diagMultResultInfo.setSecAvg(diagOpCommonService.valueFormat(secavg,secFormatType));
        diagMultResultInfo.setSecUp(diagOpCommonService.valueFormat(secavg*1.05,secFormatType));
        diagMultResultInfo.setSecDown(diagOpCommonService.valueFormat(secavg*0.95,secFormatType));

        diagMultResultInfo.setThirdAvg(diagOpCommonService.valueFormat(thirdavg,thirdFormatType));
        diagMultResultInfo.setThirdUp(diagOpCommonService.valueFormat(thirdavg*1.05,thirdFormatType));
        diagMultResultInfo.setThirdDown(diagOpCommonService.valueFormat(thirdavg*0.95,thirdFormatType));

        return diagMultResultInfo;
    }

    /**
     * 处理乘法的变异系数 和 相关系数
     */
    private void processMultiCovInfo(DiagMultResultInfo diagMultResultInfo,String kpiCode,String part1Code,String part2Code,List<String> periodList, List<DiagComnCollector> covData)
    {
        //计算变异系数的数据集
        List<Double> firData=Lists.newArrayList();
        List<Double> secData=Lists.newArrayList();
        List<Double> thirdData=Lists.newArrayList();

        //变异系数值
        Map<String,String> covValues= Maps.newHashMap();

        Map<String,DiagComnCollector> diagComnCollectorMap=covData.stream().collect(Collectors.toMap(DiagComnCollector::getPeriodName, Function.identity()));
        //从所有数据中获取当前周期所有的数据
        diagComnCollectorMap.forEach((k,v)->{
            try {
                firData.add((Double)v.getClass().getDeclaredField(kpiCode).get(v));
                secData.add((Double)v.getClass().getDeclaredField(part1Code).get(v));
                thirdData.add((Double)v.getClass().getDeclaredField(part2Code).get(v));
            } catch (IllegalAccessException e) {
                log.error("获取当前周期所有数据:", e);
            } catch (NoSuchFieldException e) {
                log.error("获取当前周期所有数据:", e);
            }
        });

        //计算变异系数
        //均值
        Double firavg=firData.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double secavg=secData.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double thirdavg=thirdData.stream().mapToDouble(Double::doubleValue).average().orElse(0d);

        //标准差
        double firStand= DataStatisticsUtils.getStandardDevitionByList(firData);
        double secStand=DataStatisticsUtils.getStandardDevitionByList(secData);
        double thirdStand=DataStatisticsUtils.getStandardDevitionByList(thirdData);

        String firCov=diagOpCommonService.valueFormat(firavg==0?null:firStand/firavg*100,"D2");
        String secCov="";
        if(UomsConstants.DIAG_KPI_CODE_TSPAN.equals(part1Code))
        {
            secCov="";
        }else
        {
            secCov=diagOpCommonService.valueFormat(secavg==0?null:secStand/secavg*100,"D2");
        }

        String thirdCov=diagOpCommonService.valueFormat(thirdavg==0?null:thirdStand/thirdavg*100,"D2");

        Map<String,String> codeNamePair=KpiCacheManager.getInstance().getKpiCodeNamePair();
        covValues.put(codeNamePair.get(kpiCode)+"变异系数",firCov);
        covValues.put(codeNamePair.get(part1Code)+"变异系数",secCov);
        covValues.put(codeNamePair.get(part2Code)+"变异系数",thirdCov);
        diagMultResultInfo.setCovValues(covValues);


        //处理相关性
        JSONObject relObj=new JSONObject();
        JSONArray relArray=new JSONArray();
        JSONObject link=new JSONObject();

        //计算firData和secData的相关系数
        link.put("name",codeNamePair.get(part1Code));
        if(UomsConstants.DIAG_KPI_CODE_TSPAN.equals(part1Code))
        {
            link.put("data","");
        }else
        {
            link.put("data",diagOpCommonService.valueFormat(PearsonCorrelationUtil.getPearsonCorrelationScoreByList(firData,secData), "D2"));
        }

        relArray.add(link);

        //计算firData和thridData的相关系数
        link=new JSONObject();
        link.put("name",codeNamePair.get(part2Code));
        link.put("data",diagOpCommonService.valueFormat(PearsonCorrelationUtil.getPearsonCorrelationScoreByList(firData,thirdData),"D2"));
        relArray.add(link);

        relObj.put("data",relArray);
        relObj.put("name",codeNamePair.get(kpiCode));
        diagMultResultInfo.setRelate(relObj);

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
            List<String> values= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(templateFilter.getDimValues());
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
}
