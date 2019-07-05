package com.linksteady.operate.service;

import java.util.List;
import java.util.Map;

public interface CacheService {

       void procKpiLoad();

       void procDiagDimLoad();

       void procReasonDimLoad();

       void procReasonRelateKpiLoad();

       void procKpiSqlTemplate();

       void procDimListLoad();

       void procDimJoinInfoLoad();

       void procAllLoad();
}
