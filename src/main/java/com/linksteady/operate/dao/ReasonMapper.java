package com.linksteady.operate.dao;
import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import com.linksteady.operate.domain.Reason;
import com.linksteady.operate.domain.ReasonKpis;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface ReasonMapper {

    List<Map<String,Object>> getReasonList(@Param("startRow") int startRow,@Param("endRow") int endRow);

    int getReasonTotalCount();

    void saveReasonData(Reason reasonDo);

    int getReasonPrimaryKey();

    void saveReasonDetail(@Param("primaryKey") int primaryKey,@Param("dimCode") String dimCode,@Param("dimValues") String dimValues,@Param("dimDisplay") String dimDisplay);

    void saveReasonTemplate(@Param("primaryKey") int primaryKey,@Param("templateCode") String  templateCode);

    void  deleteReasonDetail(@Param("reasonId") String reasonId);
    void  deleteReasonTemplate(@Param("reasonId") String reasonId);
    void  deleteReasonKpis(@Param("reasonId") String reasonId);
    void  deleteReasonById(@Param("reasonId") String reasonId);

    void updateProgressById(@Param("reasonId") String reasonId,@Param("progress") int progress);

    void updateProgressAndStatusById(@Param("reasonId") String reasonId,@Param("progress") int progress);

    List<Map<String,Object>> getRelatedKpis(@Param("reasonId") String reasonId);

    void saveRelatedKpis(ReasonKpis reasonKpis);

    List<Map<String,Object>>  getReasonInfoById(@Param("reasonId") String reasonId);

    List<Map<String,String>>  getReasonDetailById(@Param("reasonId") String reasonId);

    List<Map<String,Object>>  getReasonTemplatesById(@Param("reasonId") String reasonId);

    List<Map<String, String>> getRelatedKpiList(@Param("reasonId") String reasonId,@Param("templateCode") String templateCode);

    List<Map<String, Object>> getReasonKpiHistroy(@Param("kpiCode") String kpiCode,@Param("templateCode") String templateCode);

    List<ReasonKpis>  getReasonRelatedKpi(@Param("reasonId") String reasonId,@Param("kpiCode") String kpiCode,@Param("templateCode") String templateCode);






}
