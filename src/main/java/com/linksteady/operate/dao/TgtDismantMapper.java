package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.TgtDismant;

import java.util.List;

public interface TgtDismantMapper extends MyMapper<TgtDismant> {

    void saveTargetDismant(List<TgtDismant> list);
}