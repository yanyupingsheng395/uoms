package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.KeyPointMapper;
import com.linksteady.operate.dao.ReasonMapper;
import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import com.linksteady.operate.service.KeyPointService;
import com.linksteady.operate.service.ReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class ReasonServiceImpl implements ReasonService {

    @Autowired
    private ReasonMapper reasonMapper;


    public List<Map<String,Object>> getReasonList(int startRow,int endRow)
    {
       return  reasonMapper.getReasonList(startRow,endRow);
    }

    public int getReasonTotalCount()
    {
        return  reasonMapper.getReasonTotalCount();
    }

}
