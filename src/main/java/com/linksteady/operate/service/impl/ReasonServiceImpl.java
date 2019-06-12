package com.linksteady.operate.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.dao.ReasonMapper;
import com.linksteady.operate.dao.ReasonRelMatrixMapper;
import com.linksteady.operate.dao.ReasonResultMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.thrift.ThriftClient;
import com.linksteady.operate.vo.ReasonVO;
import com.linksteady.system.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.thrift.TException;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 原因探究相关的服务类
 * @author huang
 */
@Slf4j
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
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        List<Reason> dataList = reasonMapper.getReasonList(startRow,endRow, username);
        dataList.stream().forEach(x->{
            String reasonId = String.valueOf(x.getReasonId());
            List<Map<String,String>> reasonDetail=reasonMapper.getReasonDetailById(reasonId);
            String displayName = "";
            List<String> list = reasonDetail.stream().filter(z->null != z.get("DIM_DISPLAY_VALUE")).map(y->y.get("DIM_DISPLAY_VALUE")).collect(Collectors.toList());
            for(String l:list) {
                displayName += l + ";";
            }
            x.setDimDisplayName(displayName);
        });
       return  dataList;
    }

    @Override
    public int getReasonTotalCount()
    {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return  reasonMapper.getReasonTotalCountByUserName(username);
    }

    @Override
    public void  saveReasonData(ReasonVO reasonVO,String username,int primaryKey) {
        //将VO转化成DO
        Reason reasonDo=dozerBeanMapper.map(reasonVO, Reason.class);

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now=new Date();

        reasonDo.setReasonId(primaryKey);
        reasonDo.setStatus("R");
        reasonDo.setProgress(0);
        reasonDo.setCreateDt(sf.format(now));
        reasonDo.setUpdateDt(sf.format(now));
        reasonDo.setCreateBy(username);
        reasonDo.setUpdateBy(username);
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
        reasonMapper.deleteReasonTrace(reasonId);


    }

    @Override
    public void findReasonKpisSnp(String reasonId)
    {
        //todo 后续如果响应慢，考虑在python服务端进行处理
        try {
            thriftClient.open();
            thriftClient.getThriftService().submitReasonAanlysis(Integer.parseInt(reasonId));
            reasonMapper.updateProgressAndStatusById(reasonId,"F",100);
        } catch (TException e) {
            //todo 异常继续向上抛 同时需要将当前数据的状态更新为失败
            log.error("Exception:", e);
           //更新状态
            reasonMapper.updateProgressAndStatusById(reasonId,"E",0);
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

    @Override
    public int getResultTraceCount(String reasonResultId) {
        return reasonMapper.getResultTraceCount(reasonResultId);
    }

    @Override
    public void addResultToTrace(String reasonId, String reasonResultId) {
        reasonMapper.addResultToTrace(reasonId,reasonResultId);
    }

    @Override
    public void deleteResultToTrace(String reasonResultId) {
        reasonMapper.deleteResultToTrace(reasonResultId);
    }

    @Override
    public List<ReasonResultTrace> getReasonResultTraceList(String username) {
        List<ReasonResultTrace>  list=reasonResultMapper.getReasonResultTraceList(username);

        list.stream().forEach(x->{
            String reasonId = String.valueOf(x.getReasonId());
            List<Map<String,String>> reasonDetail=reasonMapper.getReasonDetailById(reasonId);
            String displayName = "";
            List<String> detailList = reasonDetail.stream().filter(z->null != z.get("DIM_DISPLAY_VALUE")).map(y->y.get("DIM_DISPLAY_VALUE")).collect(Collectors.toList());
            for(String l:detailList) {
                displayName += l + ";";
            }
            x.setDimDisplayName(displayName);
        });

        return list;
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
       result.put("PERIOD_NAME",reason.getPeriodName());
       result.put("KPI_NAME",reason.getKpiName());
       result.put("KPI_CODE",reason.getKpiCode());
       result.put("SOURCE",reason.getSource());

      //获取到明细信息
      List<Map<String,String>> reasonDetail=reasonMapper.getReasonDetailById(reasonId);
      result.put("reasonDetail",reasonDetail);

      return result;
    }


    @Override
    public List<ReasonKpisSnp> getReasonKpisSnp(String reasonId, String templateCode) {
        return reasonMapper.getReasonKpisSnp(reasonId,templateCode);
    }

    @Override
    public List<ReasonResult> getReasonResultList(String reasonId)
    {
        return reasonResultMapper.getReasonResultList(reasonId);
    }

    @Override
    public int getReasonResultCount(String reasonId, String reasonCode) {
        return reasonResultMapper.getReasonResultCount(reasonId,reasonCode);
    }

    @Override
    public void deleteReasonResult(String reasonResultId) {
        reasonResultMapper.deleteReasonResult(reasonResultId);
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
