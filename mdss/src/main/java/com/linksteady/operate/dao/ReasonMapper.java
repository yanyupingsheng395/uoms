package com.linksteady.operate.dao;

import com.linksteady.operate.domain.Reason;
import com.linksteady.operate.domain.ReasonKpisSnp;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ReasonMapper {

    List<Reason> getReasonList(@Param("startRow") int startRow,@Param("endRow") int endRow, @Param("username") String username, @Param("reasonName") String reasonName);

    int getReasonTotalCountByUserName(@Param("username") String username, @Param("reasonName") String reasonName);

    int getReasonTotalCount();

    int getReasonPrimaryKey();

    /**
     * 保存原因探究的主信息
     * @param reasonDo
     */
    void saveReasonData(Reason reasonDo);

    //保存原因探究的明细信息(维度及其值)
    void saveReasonDetail(@Param("primaryKey") int primaryKey,@Param("dimCode") String dimCode,@Param("dimValues") String dimValues,@Param("dimDisplay") String dimDisplay);

    //删除原因探究的明细信息
    void  deleteReasonDetail(@Param("ids") List<String> ids);

    //删除原因诊断 原因KPI的快照信息
    void  deleteReasonKpisSnp(@Param("ids") List<String> ids);

    //删除原因诊断主信息
    void  deleteReasonById(@Param("ids") List<String> ids);

    //删除原因诊断结果信息
    void deleteReasonResultById(@Param("ids") List<String> ids);

    //获取原因诊断主信息
    Reason  getReasonInfoById(@Param("reasonId") String reasonId);

    //获取原因诊断明细信息
    List<Map<String,String>>  getReasonDetailById(@Param("reasonId") String reasonId);

    void updateProgressAndStatusById(@Param("reasonId") String reasonId,@Param("status") String status,@Param("progress") int progress);

     List<ReasonKpisSnp> getReasonKpisSnp(@Param("reasonId") String reasonId, @Param("templateCode") String templateCode);

     int getResultTraceCount(@Param("reasonResultId") String reasonResultId);

    void addResultToTrace(@Param("reasonId") String reasonId,@Param("reasonResultId") String reasonResultId);

    void deleteResultToTrace(@Param("reasonResultId") String reasonResultId);

    void deleteReasonTrace(@Param("ids") List<String> ids);



}
