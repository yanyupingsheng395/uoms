package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyPersonal;
import com.linksteady.operate.domain.DailyStatis;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.DailyPersonalVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class DailyServiceImpl implements DailyService {

    @Autowired
    private DailyMapper dailyMapper;

    /**
     * 效果统计最大时间长度
     */
    private final int MAX_TASK_DAY = 10;

    @Override
    public List<DailyHead> getPageList(int start, int end, String touchDt) {
        return dailyMapper.getPageList(start, end, touchDt);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return dailyMapper.getTotalCount(touchDt);
    }

    @Override
    public int getTouchTotalCount(String touchDt) {
        return dailyMapper.getTouchTotalCount(touchDt);
    }


    @Override
    public Map<String, Object> getTipInfo(String headId) {
        return dailyMapper.getTipInfo(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String headId, String status) {
        dailyMapper.updateStatus(headId, status);
    }


    @Override
    public String getStatusById(String headId) {
        return dailyMapper.getStatusById(headId);
    }

    @Override
    public DailyHead getEffectById(String id) {
        return dailyMapper.getEffectById(id);
    }

    @Override
    public Map<String, Object> getCurrentAndTaskDate(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        String touchDt = dailyMapper.getTouchDt(headId);
        result.put("taskDt", touchDt);
        result.put("currentDt", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        return result;
    }

    /**
     * 获取推送的数据 转化人数，转化率
     *
     * @param headId
     * @return
     */
    @Override
    public Map<String, Object> getPushData(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<String> xdatas = Lists.newLinkedList();
        // 提交任务日期
        String taskDt = dailyMapper.getTouchDt(headId);
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));
        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(MAX_TASK_DAY + 1);
        // 判断当前时间与最后时间
        boolean after = LocalDate.now().isAfter(taskDtDate.plusDays(MAX_TASK_DAY + 1));
        String endDate;
        if(after) {
            endDate = maxDate.format(DateTimeFormatter.ofPattern(dateFormat));
        }else {
            endDate = LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));
        }
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(dateFormat));
        while(taskDtDate.isBefore(end.plusDays(1))) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = dailyMapper.getDailyStatisList(headId);
        Map<String, Long> convertNumMap = dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr, DailyStatis::getConvertNum));
        Map<String, Double> convertRateMap = dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr, DailyStatis::getConvertRate));
        Map<String, Long> convertSpuNumMap = dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr, DailyStatis::getConvertSpuNum));
        Map<String, Double> convertSpuRateMap = dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr, DailyStatis::getConvertSpuRate));

        xdatas.forEach(x->{
            convertNumMap.putIfAbsent(x, 0L);
            convertRateMap.putIfAbsent(x, 0D);
            convertSpuNumMap.putIfAbsent(x, 0L);
            convertSpuRateMap.putIfAbsent(x, 0D);
        });

        result.put("xdata", xdatas);
        result.put("ydata1", new ArrayList<>(convertNumMap.values()));
        result.put("ydata2", new ArrayList<>(convertRateMap.values()));
        result.put("ydata3", new ArrayList<>(convertSpuNumMap.values()));
        result.put("ydata4", new ArrayList<>(convertSpuRateMap.values()));
        return result;
    }

    @Override
    public List<DailyGroupTemplate> getUserGroupListPage(int start, int end) {
        return dailyMapper.getUserGroupListPage(start, end);
    }

    @Override
    public int getUserGroupCount() {
        return dailyMapper.getUserGroupCount();
    }

    @Override
    public void setSmsCode(String groupId, String smsCode) {
        if(StringUtils.isNotEmpty(groupId)) {
            List<String> groupIds = Arrays.asList(groupId.split(","));
            dailyMapper.setSmsCode(groupIds, smsCode);
        }
    }

    @Override
    public List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int start, int end, String headId) {
        return dailyMapper.getDailyPersonalEffect(dailyPersonalVo, start, end, headId);
    }

    @Override
    public int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId) {
        return dailyMapper.getDailyPersonalEffectCount(dailyPersonalVo, headId);
    }
}
