package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.DiagDetail;

public interface DiagDetailMapper extends MyMapper<DiagDetail> {

    void save(DiagDetail diagDetail);
}