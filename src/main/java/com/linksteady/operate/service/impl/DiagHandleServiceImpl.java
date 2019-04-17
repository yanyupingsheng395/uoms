package com.linksteady.operate.service.impl;

import com.linksteady.common.util.DateUtil;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagMultResultInfo;
import com.linksteady.operate.domain.DiagResultInfo;
import com.linksteady.operate.service.DiagHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DiagHandleServiceImpl implements DiagHandleService {

    @Autowired
    RedisTemplate redisTemplate;

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
        return (DiagResultInfo)redisTemplate.opsForValue().get("diag:"+diagId+":"+kpiLevelId);
    }


    @Override
    public DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo) {
        //获取到操作类型
        String handleType=diagHandleInfo.getHandleType();
        DiagResultInfo result=null;

        List<String> periodList=null;
        //获取周期内的每个明细单位
        if("M".equals(diagHandleInfo.getPeriodType()))  //按月
        {
            periodList= DateUtil.getMonthBetween(diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        }else  //按天
        {
            periodList=DateUtil.getEveryday(diagHandleInfo.getBeginDt(),diagHandleInfo.getEndDt());
        }

        if("M".equals(handleType)) //乘法
        {
             result=processMultiple(diagHandleInfo,periodList);
        }else if("A".equals(handleType))  //加法
        {
             result=processAdd(diagHandleInfo,periodList);
        }else if("F".equals(handleType))  //过滤
        {
            result=processFilter(diagHandleInfo,periodList);
        }

        result.setDiagId(diagHandleInfo.getDiagId());
        result.setKpiLevelId(diagHandleInfo.getKpiLevelId());
        result.setBeginDt(diagHandleInfo.getBeginDt());
        result.setEndDt(diagHandleInfo.getEndDt());
        result.setPeriodType(diagHandleInfo.getPeriodType());
        result.setHandleDesc(diagHandleInfo.getHandleDesc());

        result.setKpiCode(diagHandleInfo.getKpiCode());
        result.setKpiName(KpiCacheManager.getInstance().getCodeNamePair().get(diagHandleInfo.getKpiCode()));

        //增加条件信息
        result.setWhereinfo(diagHandleInfo.getWhereinfo()); //todo 考虑对map重构，仅返回必要信息，减少数据传输量

        //result信息持久化到redis
        //saveResultToRedis(result);

        return result;
    }


    /**
     * 处理乘法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processMultiple(DiagHandleInfo diagHandleInfo,List<String> periodList)
    {
        DiagMultResultInfo diagMultResultInfo=new DiagMultResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();
        String kpiName=KpiCacheManager.getInstance().getCodeNamePair().get(kpiCode);

        //获取其拆分为的两个指标
        Map<String,Object> kpiDismant=KpiCacheManager.getInstance().getKpiDismant();

        String part1Code=(String)kpiDismant.get("DISMANT_PART1_CODE");
        String part1Name=(String)kpiDismant.get("DISMANT_PART1_NAME");
        String part2Code=(String)kpiDismant.get("DISMANT_PART2_CODE");
        String part2Name=(String)kpiDismant.get("DISMANT_PART2_NAME");

        diagMultResultInfo.setxData(periodList);
        diagMultResultInfo.setxName("D".equals(diagHandleInfo)?"天":"月份");

        //获取三个指标的折线图

        //末期比基期的变化率；

        //均值及上下5%区域；

        //计算三个指标的变异系数

        return new DiagResultInfo();
    }

    /**
     * 处理加法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processAdd(DiagHandleInfo diagHandleInfo,List<String> periodList)
    {
        String kpiCode=diagHandleInfo.getKpiCode();
        String kpiName=KpiCacheManager.getInstance().getCodeNamePair().get(kpiCode);

        //概览信息
        if(null==diagHandleInfo.getAddDimValues()||"".equals(diagHandleInfo.getAddDimValues()))  //如果用户没选择维度值，则系统去取TOP5的
        {

        }else  //否则以用户选择的为准
        {

        }

        //计算gmv各部分的值 面积图

        //判断当前是否选择的gmv
        if(!"gmv".equals(kpiCode))   //非核心指标 其它指标各部分的趋势图与均线;
        {

        }

        //变异系数表格(仅针对GMV)

        return new DiagResultInfo();
    }

    /**
     * 处理过滤运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processFilter(DiagHandleInfo diagHandleInfo,List<String> periodList)
    {
        DiagResultInfo resultInfo=new DiagResultInfo();

        String kpiCode=diagHandleInfo.getKpiCode();
        String kpiName=KpiCacheManager.getInstance().getCodeNamePair().get(kpiCode);

        double kpiValue=0d;
        if(isSum(kpiCode))  //可累加
        {
            for(String period:periodList)
            {
                kpiValue+=Double.parseDouble(getRandomKpiData(diagHandleInfo.getPeriodType(),kpiCode));
            }
        }else  //不可累加
        {
            kpiValue=Double.parseDouble(getRandomKpiData(diagHandleInfo.getPeriodType(),kpiCode));
        }

        resultInfo.setKpiCode(kpiCode);
        resultInfo.setKpiName(kpiName);
        resultInfo.setKpiValue(kpiValue);
        return new DiagResultInfo();
    }

    private String getRandomKpiData(String period,String kpiCode)
    {
        String  result ="";
       if("gmv".equals(kpiCode))
       {
           if("M".equals(period))
           {
                result= String.valueOf(RandomUtil.getIntRandom(1000000,1200000));
           }else
           {
               result= String.valueOf(RandomUtil.getIntRandom(35000,50000));
           }

       }
       else if ("ucnt".equals(kpiCode))  //用户数
       {
            if("M".equals(period))
            {
                result= String.valueOf(RandomUtil.getIntRandom(1800,2200));
            }else
            {
                result= String.valueOf(RandomUtil.getIntRandom(50,80));
            }
       }
       else if ("uprice".equals(kpiCode))  //客单价
       {
           if("M".equals(period))
           {
               result= String.valueOf(RandomUtil.getIntRandom(500,600));
           }else
           {
               result= String.valueOf(RandomUtil.getIntRandom(500,600));
           }
       }
       else if ("price".equals(kpiCode))  //订单价
       {
           if("M".equals(period))
           {
               result= String.valueOf(RandomUtil.getIntRandom(400,600));
           }else
           {
               result= String.valueOf(RandomUtil.getIntRandom(400,600));
           }
       }
       else if ("pcnt".equals(kpiCode))  //订单数
       {
           if("M".equals(period))
           {
               result= String.valueOf(RandomUtil.getIntRandom(2500,3000));
           }else
           {
               result= String.valueOf(RandomUtil.getIntRandom(80,120));
           }
       }
       else if ("joinrate".equals(kpiCode))  //连带率  1.5-2.5
       {
           result= String.valueOf(RandomUtil.getIntRandom(15,25)/10.00);
       }
       else if ("sprice".equals(kpiCode))  //件单价
       {
           result= String.valueOf(RandomUtil.getIntRandom(200,300));
       }
       else if ("sprice2".equals(kpiCode))  //吊牌价
       {
           result= String.valueOf(RandomUtil.getIntRandom(250,350));
       }
       else if ("disrate".equals(kpiCode))  //折扣率 0.7 -0.9
       {
           result= String.valueOf(RandomUtil.getIntRandom(70,90)/10.00);
       }
       else if ("freq".equals(kpiCode))  //购买频次  1.1-1.5
       {
           result= String.valueOf(RandomUtil.getIntRandom(11,15)/10.00);
       }
       else if ("tspan".equals(kpiCode))  //时间长度
       {
           result= "0";
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
