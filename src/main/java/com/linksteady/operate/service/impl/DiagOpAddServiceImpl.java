package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.StringTemplate;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.CommonSelectMapper;
import com.linksteady.operate.domain.DiagAddDataCollector;
import com.linksteady.operate.domain.DiagAddResultInfo;
import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagResultInfo;
import com.linksteady.operate.service.DiagOpService;
import com.linksteady.operate.util.PearsonCorrelationUtil;
import com.linksteady.operate.util.UomsConstants;
import com.linksteady.operate.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 诊断处理过程 - 加法
 * @author  huang
 */
@Service
public class DiagOpAddServiceImpl implements DiagOpService {

    @Autowired
    DiagOpCommonServiceImpl diagOpCommonService;

    @Autowired
    CommonSelectMapper commonSelectMapper;

    @Override
    public DiagResultInfo process(DiagHandleInfo diagHandleInfo) {
        DiagAddResultInfo diagAddResultInfo=new DiagAddResultInfo();
        List<String> periodList=diagOpCommonService.getPeriodList(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        List<String> dayPeriodList=diagOpCommonService.getPeriodList(UomsConstants.PERIOD_TYPE_DAY,diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        //获取主指标编码
        String mainKpiCode="gmv";

        //获取当前指标编码
        String kpiCode=diagHandleInfo.getKpiCode();
        //存放维度值名称的列表
        List<String> dimNames= Lists.newArrayList();
        //存放维度值编码的列表
        List<String> dimValues;
        //获取进行加法操作的维度编码
        String dimCode=diagHandleInfo.getAddDimCode();
        //在当前要进行加法操作的维度上 是否同时选了在这个维度上进行过滤，如果选择，那么这个变量就存这些过滤值
        List<String> selectDimValues=Lists.newArrayList();

        diagHandleInfo.getWhereinfo().stream().filter(s->dimCode.equals(s.getDimCode())).map(DiagConditionVO::getDimValues).forEach(s->{
            selectDimValues.addAll(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(s));
        });

        //当前维度下，其具有的值列表 <值编码，值名称>
        Map<String,String> diagDimValue= KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode);


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
            String dimName=KpiCacheManager.getInstance().getDiagDimList().get(dimCode);
            dimNames.add(diagDimValue.get(v));
            node=new JSONObject();
            node.put("code",v);
            node.put("name",diagDimValue.get(v));
            node.put("dimCode",dimCode);
            node.put("dimName",dimName);

            nodeArray.add(node);
        }

        //设置图例名称列表
        diagAddResultInfo.setLegendData(dimNames);
        diagAddResultInfo.setXname("D".equals(diagHandleInfo.getPeriodType())?"天":"月份");
        //要插入的新节点信息
        diagAddResultInfo.setNodeList(nodeArray);
        //x轴的数据
        diagAddResultInfo.setXdata(periodList);

       //构造主指标的数据
        buildMainKpi(mainKpiCode,diagHandleInfo,dimCode,dimValues,selectDimValues,periodList,diagAddResultInfo);

        //如果用户当前进行加法的指标非当前记录的主指标，则还需要计算当前指标的折线图
        if(!mainKpiCode.equals(kpiCode)) {
           buildCurrentKpi(diagHandleInfo,kpiCode,dimCode,dimValues,selectDimValues,periodList,diagAddResultInfo);
        }

        BuildCovAndRelate(mainKpiCode,diagHandleInfo,kpiCode,dimCode,dimValues,selectDimValues,dayPeriodList,diagAddResultInfo);
        return diagAddResultInfo;
    }

    /**
     * 构建主指标的数据
     */
    private void buildMainKpi(String mainKpiCode,DiagHandleInfo diagHandleInfo,String dimCode,List<String> dimValues,List<String> selectDimValues,List<String> periodList,DiagAddResultInfo diagResultInfo)
    {
        //主指标的面积图数据
        JSONArray areaData=new JSONArray();

        String areaFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(mainKpiCode).getValueFormat();

        //首先计算当前周期下，各维度值下GMV的值
        List<DiagAddDataCollector>  diagAddDataCollectorList=getMainKpiAreaData(mainKpiCode,diagHandleInfo,dimCode,diagHandleInfo.getPeriodType(),"N",dimValues);

        //按维度值编码进行分组进行分组
        Map<String,List<DiagAddDataCollector>> temp=diagAddDataCollectorList.parallelStream().collect(Collectors.groupingBy(DiagAddDataCollector::getDimValue, LinkedHashMap::new,Collectors.toList()));

        //组装到最后的返回对象中去 (这个循环的作用是 如果用户选择了按A,B分组，但是在过滤条件里选择了A，这时候返回的结果集temp里面是没有B的数据的，需要将B的数据补上，否则前端echarts图表显示会有问题)
        for(String dimValue:dimValues)
        {
            List<DiagAddDataCollector> t1=temp.get(dimValue);
            JSONObject tempObj=new JSONObject();
            tempObj.put("name",KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode).get(dimValue));

            //查询出来的数据里面还有当前维度值编码对应的数据，且当前维度值编码在用户选择的where条件中  否则按0处理 (如果用户没有选择任何条件即selectDimValues.size>0，则不做这个判断)
            boolean flag=selectDimValues.size()==0||selectDimValues.contains(dimValue);
            if(null!=t1&&t1.size()>0&& flag)
            {
                //对数据进行修补，如果出现某个周期上没有数据，则补0
                tempObj.put("data",diagOpCommonService.valueFormat(diagOpCommonService.fixData(t1.stream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),periodList),areaFormatType));
            }else
            {
                tempObj.put("data",diagOpCommonService.valueFormat(diagOpCommonService.fixData(Maps.newHashMap(),periodList),areaFormatType));
            }
            areaData.add(tempObj);

            diagResultInfo.setAreaData(areaData);
        }
    }

    /**
     * 加法获取主指标相关的数据 (按加法的维度值、时间进行group by的主指标数据)
     */
    private List<DiagAddDataCollector> getMainKpiAreaData(String mainKpiCode,DiagHandleInfo diagHandleInfo,String dimCode,String periodType,String isOverall,List<String> dimValues)
    {
        KpiSqlTemplateVO kpiSqlTemplate;
        if(diagOpCommonService.isRelyOrderDetail(diagHandleInfo.getWhereinfo())||diagOpCommonService.isRelyOrderDetail(dimCode))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+mainKpiCode+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+mainKpiCode);
        }

        StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

        //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
        List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());

        TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters,dimCode,dimValues);

        //$DATE_RANGE$
        String data_range=diagOpCommonService.buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        String dimCodeParam=KpiCacheManager.getInstance().getDimJoinList().row(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$")).get(dimCode).getDimWhere();

        String periodName=diagOpCommonService.buildPeriodInfo(periodType);

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
     * 加法获取当前指标的数据
     */
    private void buildCurrentKpi(DiagHandleInfo diagHandleInfo,String kpiCode,String dimCode,List<String> dimValues,List<String> selectDimValues,List<String> periodList,DiagAddResultInfo diagResultInfo)
    {
        //当前指标
        JSONArray lineData=new JSONArray();
        JSONArray lineAvgData=new JSONArray();

        //获取当前指标的数据格式化类型
        String lineFormatType=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode).getValueFormat();

        //如果用户选择的指标非GMV，则计算当前选择维度值在此指标下的折线图 以及均线
        List<DiagAddDataCollector> currentLineData=getCurrentKpiLineData(diagHandleInfo,dimCode,diagHandleInfo.getPeriodType(),"N",dimValues);

        //按dimCode分组
        Map<String,List<DiagAddDataCollector>> temp6=currentLineData.parallelStream().collect(Collectors.groupingBy(DiagAddDataCollector::getDimValue,LinkedHashMap::new,Collectors.toList()));

        //组装到最后的返回对象中去
        for(String dimValue:dimValues)
        {
            List<DiagAddDataCollector> t1=temp6.get(dimValue);
            JSONObject tempObj=new JSONObject();
            JSONObject avgObj=new JSONObject();

            String name=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode).get(dimValue);
            tempObj.put("name",name);
            avgObj.put("name",name);

            boolean flag=selectDimValues.size()==0||selectDimValues.contains(dimValue);
            if(null!=t1&&t1.size()>0&&flag)
            {
                //对数据进行修补，如果出现某个周期上没有数据，则补0
                tempObj.put("data",diagOpCommonService.valueFormat(diagOpCommonService.fixData(t1.stream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),periodList),lineFormatType));
                //求均值
                avgObj.put("data",diagOpCommonService.valueFormat(t1.stream().mapToDouble(DiagAddDataCollector::getValue).average().orElse(0d),lineFormatType));
            }else
            {
                tempObj.put("data",diagOpCommonService.valueFormat(diagOpCommonService.fixData(Maps.newHashMap(),periodList),lineFormatType));
                avgObj.put("data","0");
            }
            lineData.add(tempObj);
            lineAvgData.add(avgObj);
        }

        diagResultInfo.setLineData(lineData);
        diagResultInfo.setLineAvgData(lineAvgData);

        //这里还要构造条图数据 (仅按维度值，不按时间汇总)


    }

    /**
     * 加法获取其它指标的折线图
     * @param diagHandleInfo 操作对象
     * @param dimCode 维度编码
     * @return  加法操作的返回值对象
     */
    private List<DiagAddDataCollector> getCurrentKpiLineData(DiagHandleInfo diagHandleInfo,String dimCode,String periodType,String isOverall,List<String> dimValues)
    {
        KpiSqlTemplateVO kpiSqlTemplate;
        if(diagOpCommonService.isRelyOrderDetail(diagHandleInfo.getWhereinfo())||diagOpCommonService.isRelyOrderDetail(dimCode))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+diagHandleInfo.getKpiCode().toUpperCase()+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(diagHandleInfo.getHandleType()+"_"+diagHandleInfo.getKpiCode().toUpperCase());
        }

        StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

        //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
        List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());

        //$DATE_RANGE$
        String dataRange=diagOpCommonService.buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

        //构造$DIM_CODE$
        String dimCodeParam=KpiCacheManager.getInstance().getDimJoinList().row(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$")).get(dimCode).getDimWhere();

        //构造$PERIOD_NAME$
        String periodName=diagOpCommonService.buildPeriodInfo(periodType);

        //构造$GROUP_INFO$  $COLUMN_INFO$
        StringBuilder groupInfo=new StringBuilder();
        StringBuilder columnInfo=new StringBuilder();
        groupInfo.append(" GROUP BY ");

        TemplateResult templateResult=null;
        //获取汇总数据 汇总数据的情况下仅仅根据时间周期进行汇总，不根据维度值进行group by
        if("N".equals(isOverall))
        {
            templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters,dimCode,dimValues);

            groupInfo.append(dimCodeParam).append(",").append(periodName);
            columnInfo.append(" ").append(dimCodeParam).append(" DIM_VALUE,").append(periodName).append(" PERIOD_NAME,");
        }else
        {
            //如果获取的是总体数据的话，则只需要根据 时间进行汇总，并不需要根据维度值进行group by
            templateResult=buildWhereInfoforAddSummery(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters);

            groupInfo.append(periodName);
            columnInfo.append(" ").append(periodName).append(" PERIOD_NAME,");
        }

        stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",templateResult.getJoinInfo())
                .add("$WHERE_INFO$",templateResult.getFilterInfo()).add("$DATE_RANGE$",dataRange).add("$GROUP_INFO$",groupInfo.toString())
                .add("$COLUMN_INFO$",columnInfo.toString()).add("$PERIOD_NAME$",periodName);;

        return commonSelectMapper.selectAddData(stringTemplate.render());
    }

    private void BuildCovAndRelate(String mainKpiCode,DiagHandleInfo diagHandleInfo,String kpiCode,String dimCode,List<String> dimValues,List<String> selectDimValues,List<String> dayPeriodList,DiagAddResultInfo diagAddResultInfo)
    {
        JSONArray covArray=new JSONArray();
        JSONArray relateArray=new JSONArray();
        List<DiagAddDataCollector> covList;
        List<DiagAddDataCollector> overallList;

        //获取汇总的数据是否要去掉所有的条件呢?  答案是：要去掉所有的条件
        //计算 变异系数 获取当前指标按天的数据
        if(mainKpiCode.equals(kpiCode))
        {   //如果当前指标就是主指标
            //按天的明细数据
            covList=getMainKpiAreaData(mainKpiCode,diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"N",dimValues);
            //获取总体数据，仅有周期维度
            overallList=getMainKpiAreaData(mainKpiCode,diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"Y",dimValues);
        }else
        {    //其它指标
            covList=getCurrentKpiLineData(diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"N",dimValues);
            //获取总体数据
            overallList=getCurrentKpiLineData(diagHandleInfo,dimCode,UomsConstants.PERIOD_TYPE_DAY,"Y",dimValues);
        }

        List<Double> overallDoubleList=diagOpCommonService.fixData(overallList.parallelStream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),dayPeriodList);

        //按维度值编码进行分组进行分组
        Map<String,List<DiagAddDataCollector>> temp7=covList.parallelStream().collect(Collectors.groupingBy(DiagAddDataCollector::getDimValue,LinkedHashMap::new,Collectors.toList()));

        for(String dimValue:dimValues)
        {
            JSONObject covObj=new JSONObject();
            String dimValueName=KpiCacheManager.getInstance().getDiagDimValueList().row(dimCode).get(dimValue);
            covObj.put("name",dimValueName);

            JSONObject relateObj=new JSONObject();
            relateObj.put("name",dimValueName);

            List<DiagAddDataCollector> t3=temp7.get(dimValue);
            boolean flag=selectDimValues.size()==0||selectDimValues.contains(dimValue);
            if(null!=t3&&t3.size()>0&&flag)
            {
                //计算变异系数
                List<Double> temp8=diagOpCommonService.fixData(t3.stream().collect(Collectors.toMap(DiagAddDataCollector::getPeriodName,DiagAddDataCollector::getValue)),dayPeriodList);
                double avg=t3.stream().mapToDouble(DiagAddDataCollector::getValue).average().orElse(0d);
                double stand= DataStatisticsUtils.getStandardDevitionByList(temp8);
                covObj.put("data",(stand==0?0.00: ArithUtil.formatDoubleByMode(stand/avg*100,2, RoundingMode.DOWN)));

                //计算当前数据集与 总体数据的相关系数
                double relateValue= PearsonCorrelationUtil.getPearsonCorrelationScoreByList(temp8,overallDoubleList);
                relateObj.put("data",ArithUtil.formatDoubleByMode(relateValue,2,RoundingMode.DOWN));
            }else
            {
                covObj.put("data","0.00");
                relateObj.put("data","0.00");
            }
            covArray.add(covObj);
            relateArray.add(relateObj);
        }

        //计算总体的变异系数
        double avg=overallDoubleList.parallelStream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double stand=DataStatisticsUtils.getStandardDevitionByList(overallDoubleList);
        JSONObject covObj=new JSONObject();
        covObj.put("name","总体");
        covObj.put("data",(stand==0?"0.00":diagOpCommonService.valueFormat(stand/avg*100,"D2")));
        covArray.add(covObj);

        diagAddResultInfo.setCovData(covArray);
        diagAddResultInfo.setRelateData(relateArray);

    }

    /**
     * 加法 获取top5的维度名称
     */
    private List<String>  getTop5DimValues(DiagHandleInfo diagHandleInfo,String dimCode)
    {
        KpiSqlTemplateVO kpiSqlTemplate;
        if(diagOpCommonService.isRelyOrderDetail(diagHandleInfo.getWhereinfo())||diagOpCommonService.isRelyOrderDetail(dimCode))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get("A_TOP5_DIM_VALUE_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get("A_TOP5_DIM_VALUE");
        }

        StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

        //构造$JOIN_TABLE$字符串和 $WHERE_INFO$
        List<TemplateFilter> fiters=diagHandleInfo.getWhereinfo().stream().map(e->new TemplateFilter(e.getDimCode(),e.getDimValues())).collect(Collectors.toList());

        TemplateResult templateResult=buildWhereInfo(kpiSqlTemplate.getDriverTableMapping().get("$JOIN_TABLES$"),fiters,dimCode,null);

        //$DATE_RANGE$
        String dataRange=diagOpCommonService.buildDateRange(diagHandleInfo.getPeriodType(),diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());

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
                }
                i++;
            }
        }
        return top5Values;
    }

    /**
     *  (加法构造join和where的方法)   对于加法操作，即使在条件中没选择这个维度，也要将这个维度增加到join信息中去
     * @param driverTableName  驱动表名称
     * @param filterInfo  所选的过滤条件列表
     * @param dimCode 维度类型
     * @return TemplateResult 返回构建好的join信息和where信息
     */
    private TemplateResult buildWhereInfo(String driverTableName,List<TemplateFilter> filterInfo,String dimCode,List<String> dimValues)
    {
        Map<String, DimJoinVO> dimJoin=KpiCacheManager.getInstance().getDimJoinList().row(driverTableName);

        //汇总的join信息和filter信息
        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        Set dimTableAliasSet= Sets.newHashSet();

        StringBuilder join=new StringBuilder();
        StringBuilder filter=new StringBuilder();
        Joiner joiner = Joiner.on(",").skipNulls();

        //合并用户选择的条件和 加法操作时用户选择分组的条件
        Map<String,List<String>> mergetMap=Maps.newHashMap();

        filterInfo.stream().forEach(s->{
            mergetMap.put(s.getDimCode(),Splitter.on(",").trimResults().omitEmptyStrings().splitToList(s.getDimValues()));
        });

        //以用户选择的过滤值为准线,同时合并用户选择的维度，这是为了防止 spu cate等一些值非常多的维度进行group by 会崩溃。
        if(null!=dimValues)
        {
            //判断当前用户选的条件中是否已经存在做加法的维度
            if(mergetMap.containsKey(dimCode))
            {
                mergetMap.put(dimCode,mergeValues(dimValues,mergetMap.get(dimCode)));
            }else
            {
                mergetMap.put(dimCode,dimValues);
            }
        }

        for(Map.Entry<String,List<String>> entry:mergetMap.entrySet())
        {
            //清空
            filter.setLength(0);
            join.setLength(0);

            //通过dimcode获取到其背后的信息
            DimJoinVO dimJoinVO=dimJoin.get(entry.getKey());

            //判断dim table是否已经存在(通过DIM_TABLE_ALIAS判断)
            if(!dimTableAliasSet.contains(dimJoinVO.getDimTableAlias()))
            {
                //加入到判断重复的set中
                dimTableAliasSet.add(dimJoinVO.getDimTableAlias());
                //加入到拼接队列
                join.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());
            }

            //where条件
            List<String> values=entry.getValue();
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

        //判断当前做加法的维度是否已经加入到join信息，如果没有，则增加
        DimJoinVO dimJoinVO=dimJoin.get(dimCode);
        if(!dimTableAliasSet.contains(dimJoinVO.getDimTableAlias()))
        {
            //如果还没加入，则增加
            join.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());
            joins.append(join.toString());
        }

        TemplateResult result=new TemplateResult();
        result.setFilterInfo(filters.toString());
        result.setJoinInfo(joins.toString());

        return result;
    }

    /**
     *  加法获取汇总数据，此时仅限制除了当前做加法的维度之前的其它条件，而不考虑当前做加法操作的这个维度
     *  (加法构造join和where的方法)   对于加法操作，即使在条件中没选择这个维度，也要将这个维度增加到join信息中去
     * @param driverTableName  驱动表名称
     * @param filterInfo  所选的过滤条件列表
     * @return TemplateResult 返回构建好的join信息和where信息
     */
    private TemplateResult buildWhereInfoforAddSummery(String driverTableName,List<TemplateFilter> filterInfo)
    {
        Map<String, DimJoinVO> dimJoin=KpiCacheManager.getInstance().getDimJoinList().row(driverTableName);

        //汇总的join信息和filter信息
        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        Set dimTableAliasSet= Sets.newHashSet();

        StringBuilder join=new StringBuilder();
        StringBuilder filter=new StringBuilder();
        Joiner joiner = Joiner.on(",").skipNulls();

        //合并用户选择的条件和 加法操作时用户选择分组的条件
        Map<String,List<String>> mergetMap=Maps.newHashMap();

        filterInfo.stream().forEach(s->{
            mergetMap.put(s.getDimCode(),Splitter.on(",").trimResults().omitEmptyStrings().splitToList(s.getDimValues()));
        });

        for(TemplateFilter templateFilter:filterInfo)
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
     * 合并两个list的值，对于重复的值只返回一个 (并集去重)
     */
    private List<String> mergeValues(List<String> list1,List<String> list2)
    {
        List<String> list=Lists.newArrayList();
        list.addAll(list1);
        list.addAll(list2);
        return list.stream().distinct().collect(Collectors.toList());
    }
}
