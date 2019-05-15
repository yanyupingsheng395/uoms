package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Reason;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ReasonMapper {

    List<Reason> getReasonList(@Param("startRow") int startRow,@Param("endRow") int endRow);

    int getReasonTotalCount();

    int getReasonPrimaryKey();

    //保存原因探究的主信息
    void saveReasonData(Reason reasonDo);

    //保存原因探究的明细信息(维度及其值)
    void saveReasonDetail(@Param("primaryKey") int primaryKey,@Param("dimCode") String dimCode,@Param("dimValues") String dimValues,@Param("dimDisplay") String dimDisplay);

    //删除原因探究的明细信息
    void  deleteReasonDetail(@Param("reasonId") String reasonId);

    //删除原因诊断 原因KPI的快照信息
    void  deleteReasonKpisSnp(@Param("reasonId") String reasonId);

    //删除原因诊断主信息
    void  deleteReasonById(@Param("reasonId") String reasonId);

    //删除原因诊断结果信息
    void deleteReasonResultById(@Param("reasonId") String reasonId);

    //获取原因诊断主信息
    Reason  getReasonInfoById(@Param("reasonId") String reasonId);

    //获取原因诊断明细信息
    List<Map<String,String>>  getReasonDetailById(@Param("reasonId") String reasonId);

    void updateProgressAndStatusById(@Param("reasonId") String reasonId,@Param("status") String status,@Param("progress") int progress);

     List<Map<String, Object>> getReasonKpisSnp(@Param("reasonId") String reasonId,@Param("templateCode") String templateCode);

    List<Map<String, Object>> getConcernKpiList(@Param("reasonId") String reasonId);

    int getConcernKpiCount(@Param("reasonId") String reasonId, @Param("kpiCode")String templateCode, @Param("reasonKpiCode")String reasonKpiCode);

    void addConcernKpi(@Param("reasonId") String reasonId, @Param("kpiCode")String templateCode, @Param("reasonKpiCode")String reasonKpiCode);

    void deleteConcernKpi(@Param("reasonId") String reasonId, @Param("kpiCode")String templateCode, @Param("reasonKpiCode")String reasonKpiCode);


}
