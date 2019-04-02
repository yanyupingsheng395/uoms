package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.DiagDetailMapper;
import com.linksteady.operate.domain.DiagDetail;
import com.linksteady.operate.service.DiagDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiagDetailServiceImpl implements DiagDetailService {

    @Autowired
    private DiagDetailMapper diagDetailMapper;

    @Override
    public void save(DiagDetail diagDetail) {
        diagDetailMapper.save(diagDetail);
    }
}
