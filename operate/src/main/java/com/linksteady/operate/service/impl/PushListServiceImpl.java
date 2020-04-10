package com.linksteady.operate.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.operate.dao.PushListMapper;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.service.PushListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PushListServiceImpl implements PushListService {

    @Autowired
    private PushListMapper pushListMapper;


    @Override
    public List<PushListInfo> getPushInfoListPage(int limit, int offset, String sourceCode, String pushStatus, String pushDateStr) {
        return pushListMapper.getPushInfoListPage(limit,offset, sourceCode, pushStatus, pushDateStr);
    }

    @Override
    public int getTotalCount(String sourceCode, String pushStatus, String pushDateStr) {
        return pushListMapper.getTotalCount(sourceCode, pushStatus, pushDateStr);
    }

    @Override
    public Map<String, Object> getPushData(int day) {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, Object> dayMap = pushListMapper.getPushDataOfDay(day);
        LocalDate currentDate = LocalDate.now().minusDays(day);
        String startDt = currentDate.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        String endDt = LocalDate.now().minusDays(day).format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        Map<String, Object> monthMap = pushListMapper.getPushDataOfMonth(startDt, endDt);
        result.put("day", dayMap);
        result.put("month", monthMap);
        return result;
    }

    @Override
    public Map<String, Object> getRptAndBlackData(int day) {
        LocalDate currentDate = LocalDate.now().minusDays(day);
        String startDt = currentDate.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        String endDt = LocalDate.now().minusDays(day).format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        return pushListMapper.getRptAndBlackData(day, startDt, endDt);
    }
}
