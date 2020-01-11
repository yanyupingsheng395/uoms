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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        Map<String, Object> taskInfo = dailyMapper.getTaskInfo(headId);
        String touchDt = (String) taskInfo.get("TOUCH_DT");
        BigDecimal successNum = (BigDecimal) taskInfo.get("SUCCESS_NUM");
        result.put("taskDt", touchDt);
        result.put("successNum", successNum);
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
        if (after) {
            endDate = maxDate.format(DateTimeFormatter.ofPattern(dateFormat));
        } else {
            endDate = LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));
        }
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(dateFormat));
        while (taskDtDate.isBefore(end.plusDays(1))) {
            xdatas.add(taskDtDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = dailyMapper.getDailyStatisList(headId);
        Map<String, Long> convertNumMap = dataList.stream().collect(
                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertNum).orElse(0L),
                        (k1, k2) -> k2
                )
        );
        Map<String, Double> convertRateMap = dataList.stream().collect(
                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertRate).orElse(0D),
                        (k1, k2) -> k2
                )
        );
        Map<String, Long> convertSpuNumMap = dataList.stream().collect(

                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertSpuNum).orElse(0L),
                        (k1, k2) -> k2
                )
        );
        Map<String, Double> convertSpuRateMap = dataList.stream().collect(
                Collectors.toMap(
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConversionDateStr).orElse("0"),
                        dailyStatis -> Optional.ofNullable(dailyStatis).map(DailyStatis::getConvertSpuRate).orElse(0D),
                        (k1, k2) -> k2
                )
        );
        xdatas.forEach(x -> {
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
    public List<DailyGroupTemplate> getUserGroupList() {
        return dailyMapper.getUserGroupList();
    }

    @Override
    public void setSmsCode(String groupId, String smsCode) {
        if (StringUtils.isNotEmpty(groupId)) {
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

    /**
     * 验证用户群组：
     * 1. 含券：券名称为空
     * 2. 不含券：券名称不为空
     * 3. 短信：不为空
     * 4. 验证券有效期是否合法，系统不对失效的券进行更新，由用户自行更新
     * 5. 获取其他groupId, check_flag = 'Y'的设置CHECK_COMMENTS 为null
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean validUserGroup() {
        // 获取短信内容为空的情况
        // 含券：券名称为空
        String whereInfo = " and t1.IS_COUPON = 1 AND t4.COUPON_NAME IS NULL";
        int count1 = dailyMapper.updateCheckFlagAndRemark(whereInfo, "群组含券，券信息不能为空");

        // 不含券：券名称不为空
        whereInfo = " and t1.IS_COUPON = 0 and t4.coupon_name is not null";
        int count2 = dailyMapper.updateCheckFlagAndRemark(whereInfo, "群组不含券，券信息不能出现");

        // 短信：不为空
        whereInfo = " and t2.SMS_CONTENT IS NULL";
        int count3 = dailyMapper.updateCheckFlagAndRemark(whereInfo, "消息模板不能为空");

        // 验证券的有效期
        whereInfo = " and to_number(to_char(t4.VALID_END, 'YYYYMMDD')) < to_number(to_char(sysdate, 'YYYYMMDD'))";
        int count4 = dailyMapper.updateCheckFlagAndRemark(whereInfo, "券有效期已过期");

        whereInfo = " and t4.VALID_STATUS = 'N'";
        int count5 = dailyMapper.updateCheckFlagAndRemark(whereInfo, "券已失效");

        // 其他群组的校验字段更新为'Y'
        whereInfo = " and (" +
                "(t1.IS_COUPON = 1 AND t4.COUPON_NAME IS NULL)" +
                " or (t1.IS_COUPON = 0 and t4.coupon_name is not null)" +
                " or (t2.SMS_CONTENT IS NULL)" +
                " or (to_number(to_char(t4.VALID_END, 'YYYYMMDD')) < to_number(to_char(sysdate, 'YYYYMMDD')))" +
                " or t4.VALID_STATUS = 'N'" +
                ")";
        dailyMapper.updateCheckFlagY(whereInfo);
        int result = count1 + count2 + count3 + count4 + count5;

        // 更新头表的有效状态
        dailyMapper.updateValidStatus();
        return result > 0;
    }
}
