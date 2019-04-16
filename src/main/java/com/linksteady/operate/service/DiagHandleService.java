package com.linksteady.operate.service;

import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagResultInfo;

public interface DiagHandleService {

    void saveHandleInfoToRedis(DiagHandleInfo diagHandleInfo);

    DiagHandleInfo getHandleInfoFromRedis(int diagId,int kpiLevelId);

    DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo);
}
