package com.linksteady.operate.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.ReasonMapper;
import com.linksteady.operate.dao.ReasonRelMatrixMapper;
import com.linksteady.operate.dao.ReasonResultMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.thrift.ThriftClient;
import com.linksteady.operate.vo.ReasonVO;
import org.apache.thrift.TException;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的服务类
 * @author huang
 */
@Service
public class ReasonServiceImpl implements ReasonService {

    @Autowired
    private ReasonMapper reasonMapper;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    ReasonRelMatrixMapper reasonRelMatrixMapper;

    @Autowired
    ReasonResultMapper reasonResultMapper;

    @Autowired
    ThriftClient thriftClient;

    @Override
    public List<Reason> getReasonList(int startRow, int endRow)
    {
       return  reasonMapper.getReasonList(startRow,endRow);
    }

    @Override
    public int getReasonTotalCount()
    {
        return  reasonMapper.getReasonTotalCount();
    }

    @Override
    public void  saveReasonData(ReasonVO reasonVO,String curuser,int primaryKey) {
        //将VO转化成DO
        Reason reasonDo=dozerBeanMapper.map(reasonVO, Reason.class);

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now=new Date();

        reasonDo.setReasonId(primaryKey);
        reasonDo.setStatus("R");
        reasonDo.setProgress(0);
        reasonDo.setCreateDt(sf.format(now));
        reasonDo.setUpdateDt(sf.format(now));
        reasonDo.setCreateBy(curuser);
        reasonDo.setUpdateBy(curuser);
        reasonDo.setPeriodType(reasonVO.getPeriod());
        reasonDo.setKpiCode(reasonVO.getKpi());
        reasonMapper.saveReasonData(reasonDo);
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
            //主键 维度key 维度值 维度显示
            reasonMapper.saveReasonDetail(primaryKey,temp[0],temp[1],temp[2]);
        }
    }

    @Override
    public void deleteReasonById(String reasonId) {
        reasonMapper.deleteReasonDetail(reasonId);
        reasonMapper.deleteReasonKpisSnp(reasonId);
        reasonMapper.deleteReasonById(reasonId);
        reasonMapper.deleteReasonResultById(reasonId);


    }

    @Override
//    @Async
    public void findReasonKpisSnp(String reasonId)
    {
        try {
            thriftClient.open();
            thriftClient.getThriftService().submitReasonAanlysis(Integer.parseInt(reasonId));
            reasonMapper.updateProgressAndStatusById(reasonId,"F",100);
        } catch (TException e) {
            //todo 异常继续向上抛 同时需要将当前数据的状态更新为失败
            e.printStackTrace();
           //更新状态
            reasonMapper.updateProgressAndStatusById(reasonId,"E",1);
        } finally {
            //关闭
            thriftClient.close();
        }
    }


    /**
     * 根据原因ID获取到头信息
     * @param reasonId
     */
    @Override
    public  Reason getReasonHeaderInfoById(String reasonId) {

        return reasonMapper.getReasonInfoById(reasonId);
    }

    /**
     * 根据原因ID获取到详细信息
     * @param reasonId
     */
    @Override
    public  Map<String,Object> getReasonAllInfoById(String reasonId) {

        //构造返回的对象
        Map<String,Object> result=Maps.newHashMap();

       //获取到头表信息
      Reason reason=reasonMapper.getReasonInfoById(reasonId);


       result.put("REASON_ID",reason.getReasonId());
       result.put("REASON_NAME",reason.getReasonName());
       result.put("BEGIN_DT",reason.getBeginDt());
       result.put("END_DT",reason.getEndDt());
       result.put("PERIOD_TYPE",reason.getPeriodType());
       result.put("KPI_NAME",reason.getKpiName());
       result.put("KPI_CODE",reason.getKpiCode());
       result.put("SOURCE",reason.getSource());

      //获取到明细信息
      List<Map<String,String>> reasonDetail=reasonMapper.getReasonDetailById(reasonId);
      result.put("reasonDetail",reasonDetail);

      return result;
    }


    @Override
    public List<Map<String, Object>> getReasonKpisSnp(String reasonId, String templateCode) {
        return reasonMapper.getReasonKpisSnp(reasonId,templateCode);
    }

    @Override
    public List<Map<String,Object>> getConcernKpiList(String reasonId)
    {
        return reasonMapper.getConcernKpiList(reasonId);
    }

    @Override
    public int getConcernKpiCount(String reasonId, String templateCode, String reasonKpiCode) {
        return reasonMapper.getConcernKpiCount(reasonId,templateCode,reasonKpiCode);
    }

    @Override
    public void addConcernKpi(String reasonId, String templateCode, String reasonKpiCode) {
          reasonMapper.addConcernKpi(reasonId,templateCode,reasonKpiCode);
    }

    @Override
    public void deleteConcernKpi(String reasonId, String templateCode, String reasonKpiCode) {
          reasonMapper.deleteConcernKpi(reasonId,templateCode,reasonKpiCode);
    }

    @Override
    public List<ReasonResult> getReasonResultList(String reasonId)
    {
        return reasonResultMapper.getReasonResultList(reasonId);
    }

    @Override
    public void deleteReasonResult(String reasonId, String reasonCode) {
        reasonResultMapper.deleteReasonResult(reasonId,reasonCode);
    }

    @Override
    public int getReasonResultCount(String reasonId, String reasonCode) {
        return reasonResultMapper.getReasonResultCount(reasonId,reasonCode);
    }

    @Override
    public void saveReasonResult(String reasonId, String reasonCode, String formulaDesc, String formula, String business) {
        reasonResultMapper.saveReasonResult(reasonId,reasonCode,formulaDesc,formula,business);
    }

    @Override
    public List<ReasonRelMatrix> getReasonResultByCode(String reasonId, String fcode, String rfcode){
        return reasonRelMatrixMapper.getReasonRelateInfoByCode(reasonId,fcode,rfcode);
    }


}
