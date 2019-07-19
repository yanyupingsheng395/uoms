package com.linksteady.mdss.dao;

import com.linksteady.mdss.config.MyMapper;
import com.linksteady.mdss.domain.TgtDismant;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface TgtDismantMapper extends MyMapper<TgtDismant> {

    void saveTargetDismant(List<TgtDismant> list);

    List<TgtDismant> findByTgtId(Long targetId);

    void updateTgtDismantBatch(@Param(value = "tgtDismants")List<TgtDismant> tgtDismants);

    void updateTgtDismantPastFlag(@Param("targetId") long targetId,@Param("currentDt") String currentDt);

    void updateTgtDismantFinishFlag(@Param("targetId") long targetId);

    List<TgtDismant>  getPastDismantInfo(@Param("targetId") long targetId);

    void updateGrowthRate(@Param(value = "growthRate")List<TgtDismant> list);

    void deleteTgtDismantById(@Param("ids") List<String> ids);
}