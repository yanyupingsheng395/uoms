package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.common.util.StringTemplate;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.CommonSelectMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.DiagHandleService;
import com.linksteady.operate.vo.DiagConditionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        result.setKpiName(KpiCacheManager.getInstance().getDiagKpiList().get(diagHandleInfo.getKpiCode()));

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
        DiagMultResultInfo diagMultResultInfo=new DiagMultResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();
        String kpiName=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode);

        //获取其拆分为的两个指标
        KpiDismantInfo kpiDismant=KpiCacheManager.getInstance().getKpiDismant().get(kpiCode);
        String part1Code=kpiDismant.getDismantPart1Code();
        String part1Name=kpiDismant.getDismantPart1Name();
        String part2Code=kpiDismant.getDismantPart2Code();
        String part2Name=kpiDismant.getDismantPart2Name();

        diagMultResultInfo.setXData(periodList);
        diagMultResultInfo.setXName("D".equals(diagHandleInfo.getPeriodType())?"天":"月份");

        //获取三个指标的折线图
        diagMultResultInfo.setFirYName(kpiName+"(元)");
        diagMultResultInfo.setSecYName(part1Name);
        diagMultResultInfo.setThirdYName(part2Name);

        LinkedList<Double> firData= Lists.newLinkedList();
        LinkedList<Double> secData= Lists.newLinkedList();
        LinkedList<Double> thirdData= Lists.newLinkedList();

        //获取三个指标的数值

        //变异系数值
        List<Double> firCov=Lists.newArrayList();
        List<Double> secCov=Lists.newArrayList();
        List<Double> thirdCov=Lists.newArrayList();
        Map<String,List<Double>> covValues= Maps.newHashMap(); //变异系数值
        Map<String,String> covNames=Maps.newHashMap();

        for(String period:periodList)
        {
            firData.add(getRandomKpiData(diagHandleInfo.getPeriodType(),kpiCode));
            secData.add(getRandomKpiData(diagHandleInfo.getPeriodType(),part1Code));
            thirdData.add(getRandomKpiData(diagHandleInfo.getPeriodType(),part2Code));

            //按月
            if("M".equals(diagHandleInfo.getPeriodType()))
            {
                //获取变异系数值
                firCov.add(getRandomKpiData(diagHandleInfo.getPeriodType(),"cov"));
                secCov.add(getRandomKpiData(diagHandleInfo.getPeriodType(),"cov"));
                thirdCov.add(getRandomKpiData(diagHandleInfo.getPeriodType(),"cov"));
            }

        }

        covValues.put("cov1",firCov);
        covValues.put("cov2",secCov);
        covValues.put("cov3",thirdCov);
        diagMultResultInfo.setCovValues(covValues);

        covNames.put("cov1",kpiName+"变异系数");
        covNames.put("cov2",part1Name+"变异系数");
        covNames.put("cov3",part2Name+"变异系数");

        diagMultResultInfo.setCovNames(covNames);

        diagMultResultInfo.setFirData(firData);
        diagMultResultInfo.setSecData(secData);
        diagMultResultInfo.setThirdData(thirdData);

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

        //处理相关性
        JSONObject relObj=new JSONObject();
        JSONArray relArray=new JSONArray();

        relObj.put("name",getRandomKpiData("","relate"));

        JSONObject link=new JSONObject();
        link.put("name",part1Name);
        link.put("data",getRandomKpiData("","relate"));
        relArray.add(link);

        link=new JSONObject();
        link.put("name",part2Name);
        link.put("data",getRandomKpiData("","relate"));
        relArray.add(link);

        relObj.put("data",relArray);

        diagMultResultInfo.setRelate(relObj);
        return diagMultResultInfo;
    }

    /**
     * 处理加法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processAdd(DiagHandleInfo diagHandleInfo,List<String> periodList)
    {
        DiagAddResultInfo diagAddResultInfo=new DiagAddResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();

        List<String> dimNames=Lists.newArrayList();  //存放维度值名称的列表
        List<String> dimValues=null;    //存放维度值编码的列表
        String dimCode=diagHandleInfo.getAddDimCode();
        Map<String,String> diagDimValue=( Map<String,String>)KpiCacheManager.getInstance().getDiagDimValueList().get(dimCode);

        //加法要插入的节点
        JSONArray nodeArray=new JSONArray();
        JSONObject node=null;

        //概览信息
        if(null==diagHandleInfo.getAddDimValues()||"".equals(diagHandleInfo.getAddDimValues()))  //如果用户没选择维度值，则系统去取TOP5的
        {
            dimValues=Lists.newArrayList();
            //此处模拟随机取5个
            int i=0;
            for (String key : diagDimValue.keySet()) {
                if(i<5)
                {
                    dimValues.add(key);
                    dimNames.add(diagDimValue.get(key));

                    node=new JSONObject();
                    node.put("code",key);
                    node.put("name",diagDimValue.get(key));
                    nodeArray.add(node);
                }
                i++;
            }
        }else
        {
            dimValues=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(diagHandleInfo.getAddDimValues());
            for(String v:dimValues)
            {
                dimNames.add(diagDimValue.get(v));

                node=new JSONObject();
                node.put("code",v);
                node.put("name",diagDimValue.get(v));
                nodeArray.add(node);
            }
        }

        int dimSize=dimValues.size();

        Map<String,Integer> pctMap=Maps.newHashMap();
        int pctSum=0;
        //随机获得拆分比例
        for(int i=0;i<dimSize;i++)
        {
              if(i==dimSize-1)
              {
                  pctMap.put(dimValues.get(i),100-pctSum);
              }else
              {
                  pctSum+=5*i+10;
                  pctMap.put(dimValues.get(i),5*i+10);
              }
        }


        diagAddResultInfo.setLegendData(dimNames);
        diagAddResultInfo.setXname("D".equals(diagHandleInfo.getPeriodType())?"天":"月份");
        diagAddResultInfo.setXdata(periodList);

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
        for(String dv:dimValues)
        {
            tempObj=new JSONObject();
            tempObj.put("name",diagDimValue.get(dv));
            tempValue=Lists.newLinkedList();

            for(String period:periodList)
            {
               double gmv=gmvMap.get(period);
               int pct=  pctMap.get(dv);  //当前维度的拆分比例

               tempValue.add(ArithUtil.formatDouble(gmv*pct/100.00,2));
            }

            tempObj.put("data",tempValue);
            areaData.add(tempObj);
        }

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

        //要插入的新节点信息
        diagAddResultInfo.setNodeList(nodeArray);

        //相关性

        return diagAddResultInfo;
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
        String kpiName=KpiCacheManager.getInstance().getDiagKpiList().get(kpiCode);

        double kpiValue=0d;

        KpiSqlTemplate kpiSqlTemplate=null;
        //判断用户选择的维度中是否含有品牌、SPU 如果有，则获取 从明细查询的模板
        if(isRelyOrderDetail(diagHandleInfo.getWhereinfo()))
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(kpiCode+"_"+diagHandleInfo.getHandleType()+"_DETAIL");
        }else
        {
            kpiSqlTemplate=KpiCacheManager.getInstance().getKpiSqlTemplateList().get(kpiCode+"_"+diagHandleInfo.getHandleType());
        }


       //判断是否拿到了模板
        if(null!=kpiSqlTemplate&& !StringUtils.isBlank(kpiSqlTemplate.getSqlTemplate())) {
            //构造参数 填充到模板中
            StringTemplate stringTemplate=new StringTemplate(kpiSqlTemplate.getSqlTemplate());

            //构造where字符串
            String joinTable="";  //buildWhereInfo(diagHandleInfo.getWhereinfo());

            stringTemplate.add("$START$",diagHandleInfo.getBeginDt()).add("$END$",diagHandleInfo.getEndDt()).add("$JOIN_TABLES$",joinTable);

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

//    private String buildWhereInfo( List<Map<String,String>> whereinfo)
//    {
//        for(Map<String,String> info:whereinfo)
//        {
//            //通过dimcode获取到其背后的表  获取到目标表 同时获取到关联关系
//        }
//        return "";
//    }

    /**
     * 是否依赖于订单明细表
     * @return
     */
    private boolean isRelyOrderDetail(List<DiagConditionVO> whereInfo)
    {
       return true;
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
