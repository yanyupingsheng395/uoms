package com.linksteady.operate.service;

import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagResultInfo;

public interface DiagHandleService {

    DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo);

    void saveResultToRedis(DiagResultInfo diagResultInfo);

    DiagResultInfo getResultFromRedis(int diagId, int kpiLevelId);

    DiagHandleInfo getDiagHandleInfoFromRedis(int diagId, int kpiLevelId);

    void setDiagHandleInfoToRedis(DiagHandleInfo diagHandleInfo);
}
