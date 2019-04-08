package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.DiagCondition;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DiagConditionMapper extends MyMapper<DiagCondition> {

    void save(@RequestParam("conditions") List<DiagCondition> conditions);
}