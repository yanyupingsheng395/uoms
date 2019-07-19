package com.linksteady.mdss.service;

import com.linksteady.mdss.domain.DiagHandleInfo;
import com.linksteady.mdss.domain.DiagResultInfo;

public interface DiagHandleService {

    DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo);

    void saveResultToRedis(DiagResultInfo diagResultInfo);

    DiagResultInfo getResultFromRedis(int diagId, int kpiLevelId);

    DiagHandleInfo getDiagHandleInfoFromRedis(int diagId, int kpiLevelId);

    void setDiagHandleInfoToRedis(DiagHandleInfo diagHandleInfo);
}
