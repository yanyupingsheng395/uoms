package com.linksteady.mdss.service;

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
