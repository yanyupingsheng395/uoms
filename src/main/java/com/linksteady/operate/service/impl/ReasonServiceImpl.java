package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.linksteady.common.util.DateUtil;
import com.linksteady.operate.dao.ReasonMapper;
import com.linksteady.operate.domain.Reason;
import com.linksteady.operate.domain.ReasonKpis;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import org.assertj.core.util.Lists;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReasonServiceImpl implements ReasonService {

    @Autowired
    private ReasonMapper reasonMapper;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    RedisTemplate redisTemplate;


    public List<Map<String,Object>> getReasonList(int startRow,int endRow)
    {
       return  reasonMapper.getReasonList(startRow,endRow);
    }

    public int getReasonTotalCount()
    {
        return  reasonMapper.getReasonTotalCount();
    }

    @Override
    public String  saveReasonData(ReasonVO reasonVO,String curuser,int primaryKey) {
        //将VO转化成DO
        Reason reasonDo=dozerBeanMapper.map(reasonVO, Reason.class);

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf2=new SimpleDateFormat("yyyyMMdd");
        Date now=new Date();

        String reasonName=sf2.format(now)+"-"+primaryKey;

        reasonDo.setReasonId(primaryKey);
        reasonDo.setReasonName(reasonName);
        reasonDo.setStatus("R");
        reasonDo.setProgress(0);
        reasonDo.setCreateDt(sf.format(now));
        reasonDo.setUpdateDt(sf.format(now));
        reasonDo.setCreateBy(curuser);
        reasonDo.setUpdateBy(curuser);
        reasonDo.setPeriod(reasonVO.getPeriod());
        reasonMapper.saveReasonData(reasonDo);
        return reasonName;
    }

    @Override
    public int getReasonPrimaryKey() {
        return reasonMapper.getReasonPrimaryKey();
    }

    @Override
    public void saveReasonDetail(int primaryKey, String[] dims) {
        for(String dim:dims)
        {
            String[] temp=dim.split("\\^");
            reasonMapper.saveReasonDetail(primaryKey,temp[0],temp[1],temp[2]);  //主键 维度key 维度值 维度显示
        }
    }

    @Override
    public void saveReasonTemplate(int primaryKey, String[] templates) {
        for(String t:templates)
        {
            reasonMapper.saveReasonTemplate(primaryKey,t);  //主键 模板code
        }
    }

    @Override
    public void deleteReasonById(String reasonId) {
        reasonMapper.deleteReasonDetail(reasonId);
        reasonMapper.deleteReasonTemplate(reasonId);
        reasonMapper.deleteReasonKpis(reasonId);
        reasonMapper.deleteReasonById(reasonId);

    }

    @Override
    public void updateProgressById(String reasonId,int progress) {


        if(progress==100)
        {   //更新进度和状态
            reasonMapper.updateProgressAndStatusById(reasonId,progress);
        }else
        {
            //更新进度
            reasonMapper.updateProgressById(reasonId,progress);
        }

    }

    @Override
    public void findReasonKpis(String reasonId)
    {
        //找到此方法下选择了那些模板 及其指标  todo 目前通过随机数模拟选择
        List<Map<String,Object>> rkpis=reasonMapper.getRelatedKpis(reasonId);

        //写入到UO_REASON_KPIS
        String period="";
        Date beginDt=null;
        Date endDt=null;
        String chartType="";

        ReasonKpis reasonKpis=null;
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String,Object> k:rkpis)
        {
             reasonKpis=new ReasonKpis();
            //获取到周期类型
            period=(String)k.get("PERIOD_TYPE");
            try {
                beginDt=sf.parse((String)k.get("BEGIN_DT"));
                endDt=sf.parse((String)k.get("END_DT"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            chartType=(String)k.get("CONFIG_INFO1");


            //获取到图表的数据和结果集
            String result="";  //todo 暂时不使用

            Map<String,List<String>> rdatas=generateData(period,(String)k.get("BEGIN_DT"),(String)k.get("END_DT"),chartType,(String)k.get("REASON_KPI_NAME"));

            StringBuffer keyName=new StringBuffer();
            // reason:{resonId}:{templateCode}:{reasonKeyCode}
            keyName.append("reason:").append(reasonId).append(":").append((String)k.get("TEMPLATE_CODE")).append(":").append((String)k.get("REASON_KPI_CODE"));
            redisTemplate.opsForValue().set(keyName.toString(),rdatas);

            reasonKpis.setReasonId(Integer.valueOf(k.get("REASON_ID").toString()));
            reasonKpis.setTemplateCode((String)k.get("TEMPLATE_CODE"));
            reasonKpis.setReasonKpiCode((String)k.get("REASON_KPI_CODE"));
            reasonKpis.setReasonKpiType((String)k.get("REASON_KPI_TYPE"));
            reasonKpis.setReasonKpiName((String)k.get("REASON_KPI_NAME"));

            reasonKpis.setBeginDate(beginDt);
            reasonKpis.setEndDate(endDt);

            reasonKpis.setPeriodType(period);
            reasonKpis.setChartType(chartType);
            reasonKpis.setConfigInfo2((String)k.get("CONFIG_INFO2"));

            reasonKpis.setResult(result);
            reasonKpis.setCreateDt(new Date());
            reasonKpis.setUpdateDt(new Date());

            //保存记录
            reasonMapper.saveRelatedKpis(reasonKpis);
        }
    }

    /**
     * 根据原因ID获取到详细信息
     * @param reasonId
     */
    @Override
    public  Map<String,Object> getReasonInfoById(String reasonId) {

        //构造返回的对象
        Map<String,Object> result=Maps.newHashMap();

       //获取到头表信息
       List<Map<String,Object>> reasonlist=reasonMapper.getReasonInfoById(reasonId);

       Map<String,Object> reason=reasonlist.get(0);

       result.put("REASON_ID",reason.get("REASON_ID"));
       result.put("REASON_NAME",reason.get("REASON_NAME"));
       result.put("BEGIN_DT",reason.get("BEGIN_DT"));
       result.put("END_DT",reason.get("END_DT"));
       result.put("PERIOD_TYPE",reason.get("PERIOD_TYPE"));
       result.put("KPI_NAME",reason.get("KPI_NAME"));
        result.put("KPI_CODE",reason.get("KPI_CODE"));

      //获取到明细信息
      List<Map<String,String>> reasonDetail=reasonMapper.getReasonDetailById(reasonId);

      result.put("reasonDetail",reasonDetail);

      //获取到模板信息
      List<Map<String,Object>> reasonTemplates=reasonMapper.getReasonTemplatesById(reasonId);

      result.put("template",reasonTemplates);

      return result;
    }

    @Override
    public List<Map<String, String>> getRelatedKpiList(String reasonId, String templateCode) {
        return reasonMapper.getRelatedKpiList(reasonId,templateCode);
    }

    @Override
    public List<Map<String, Object>> getReasonKpiHistroy(String reasonId,String kpiCode, String templateCode) {
        return reasonMapper.getReasonKpiHistroy(reasonId,kpiCode,templateCode);
    }

    @Override
    public Map<String, Object> getReasonRelatedKpi(String reasonId,String templateCode,String reasonKpiCode) {
        List<ReasonKpis>  result=reasonMapper.getReasonRelatedKpi(reasonId,templateCode,reasonKpiCode);

        Map<String, Object> map=Maps.newHashMap();
        if(null!=result&&result.size()>0)
        {
            map.put("chartType",result.get(0).getChartType());
            map.put("kpiName",result.get(0).getReasonKpiName());
            map.put("period",result.get(0).getPeriodType());
        }
        return map;
    }

    @Override
    public Map getReasonRelateKpiDataFromRedis(String reasonId,String templateCode,String reasonKpiCode)
    {
        StringBuffer key=new StringBuffer();
        key.append("reason:").append(reasonId).append(":").append(templateCode).append(":").append(reasonKpiCode);

        return (Map)redisTemplate.opsForValue().get(key.toString());
    }

    /**
    * 根据周期类型生成图表数据
     */
    private  Map<String,List<String>> generateData(String period,String beginDt,String endDt,String chartType,String reasonKpiName)
    {
        Map<String,List<String>> datas= Maps.newHashMap();
        List<String> xdata= Lists.newArrayList();
        List<String> ydata=Lists.newArrayList();

        double value=0d;
        Random random=new Random();

         //判断时间 period为D 表示按天 M表示按月
        if("M".equals(period))
        {
            //获取两个日期直接的所有月份
            List<String> months=DateUtil.getMonthBetween(beginDt.substring(0,7),endDt.substring(0,7));

            for(String mth:months)
            {
                //根据指标名称判断
                if(reasonKpiName.contains("率"))
                {
                     value=new BigDecimal(random.nextDouble()*100).setScale(2, RoundingMode.DOWN).doubleValue();
                }else if(reasonKpiName.contains("比"))
                {
                    value=new BigDecimal(random.nextDouble()).setScale(2, RoundingMode.DOWN).doubleValue();
                }else
                {
                    value=Math.round(random.nextDouble()*1000);
                }

                //将生成的值放入到list中
                xdata.add(mth);
                ydata.add(String.valueOf(value));

            }

        }else  //按天生成数据
        {
            List<String> days= DateUtil.getEveryday(beginDt,endDt);

            for(String dd :days)
            {
                //根据指标名称判断
                if(reasonKpiName.contains("率"))
                {
                    value=new BigDecimal(random.nextDouble()*100).setScale(2, RoundingMode.DOWN).doubleValue();
                }else if(reasonKpiName.contains("比"))
                {
                    value=new BigDecimal(random.nextDouble()).setScale(2, RoundingMode.DOWN).doubleValue();
                }else
                {
                    value=Math.round(random.nextDouble()*1000);
                }

                //将生成的值放入到list中
                xdata.add(dd);
                ydata.add(String.valueOf(value));
            }
        }

        datas.put("xdata",xdata);
        datas.put("ydata",ydata);

        return datas;
    }

    @Override
    public List<Map<String,Object>> getConcernKpiList(String reasonId)
    {
        return reasonMapper.getConcernKpiList(reasonId);
    }

    @Override
    public int getConcernKpiCount(String reasonId, String kpiCode, String templateCode, String reasonKpiCode) {
        return reasonMapper.getConcernKpiCount(reasonId,kpiCode,templateCode,reasonKpiCode);
    }

    @Override
    public void addConcernKpi(String reasonId, String kpiCode, String templateCode, String reasonKpiCode) {
          reasonMapper.addConcernKpi(reasonId,kpiCode,templateCode,reasonKpiCode);
    }

    @Override
    public void deleteConcernKpi(String reasonId, String kpiCode, String templateCode, String reasonKpiCode) {
          reasonMapper.deleteConcernKpi(reasonId,kpiCode,templateCode,reasonKpiCode);
    }


}
