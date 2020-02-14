package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.config.ConfigCacheManager;
import com.linksteady.operate.dao.ConfigMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.DailyService;
import com.linksteady.operate.vo.DailyPersonalVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    
    @Autowired
    private ConfigMapper configMapper;


    @Autowired
    RedisTemplate<String,String> redisTemplate;

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
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String headId, String status) {
        dailyMapper.updateStatus(headId, status);
    }


    @Override
    public DailyHead getDailyHeadById(String headId) {
        return dailyMapper.getDailyHeadById(headId);
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
        ConfigCacheManager cacheManager = ConfigCacheManager.getInstance();
        String active = cacheManager.getConfigMap().get("op.daily.pathactive.list");
        List<String> activeList = null;
        if(StringUtils.isNotEmpty(active)){
            activeList = Arrays.asList(active.split(","));
        }
        return dailyMapper.getUserGroupList(activeList);
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
     * 验证用户群组：先进行一遍验证，更新配置表的CHECK_FLAG 然后再返回校验的状态
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
        dailyMapper.updateCheckFlagY();
        // 获取短信内容为空的情况
        // 含券：券名称为空
        String whereInfo = " and t1.IS_COUPON = 1 AND t4.COUPON_DISPLAY_NAME IS NULL";
        dailyMapper.updateCheckFlagAndRemark(whereInfo, "文案含补贴，补贴信息不能为空");

        // 不含券：券名称不为空
        whereInfo = " and t1.IS_COUPON = 0 and t4.COUPON_DISPLAY_NAME is not null";
        dailyMapper.updateCheckFlagAndRemark(whereInfo, "文案不含补贴，补贴信息不能出现");
        // 验证券的有效期
        whereInfo = " and to_number(to_char(t4.VALID_END, 'YYYYMMDD')) < to_number(to_char(sysdate, 'YYYYMMDD'))";
        dailyMapper.updateCheckFlagAndRemark(whereInfo, "补贴有效期已过期");

        // 短信：不为空
        whereInfo = " and t2.SMS_CONTENT IS NULL";
        dailyMapper.updateCheckFlagAndRemark(whereInfo, "尚未为群组配置文案");

        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Map<String, String> configMap = configCacheManager.getConfigMap();
        String active = configMap.get("op.daily.pathactive.list");
        int result = 0;
        if(StringUtils.isNotEmpty(active)) {
            List<String> activeList = Arrays.asList(active.split(","));
            if(activeList.size() > 0) {
                result = dailyMapper.validCheckedUserGroup(Arrays.asList(active.split(",")));
            }
        }else {
            throw new RuntimeException("活跃度数据表配置有误！");
        }
        return result > 0;
    }

    @Override
    public void updateHeaderOpChangeDate(String headId, Long opChangeDate) {
        dailyMapper.updateHeaderOpChangeDate(headId,opChangeDate);
    }

    @Override
    public boolean getTransContentLock(String headId) {
        ValueOperations valueOperations=redisTemplate.opsForValue();
        //key 已经存在，则不做任何动作 否则将 key 的值设为 value 设置成功，返回true 否则返回false
        boolean flag=valueOperations.setIfAbsent("daily_trans_lock",headId);
        //key的失效时间60秒 此处有bug 通过升级api可以解决
        redisTemplate.expire("daily_trans_lock",60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("daily_trans_lock");
    }

    @Override
    public int validateOpChangeTime(String headId, Long opChangeDate) {
        return dailyMapper.validateOpChangeTime(headId,opChangeDate);
    }

    @Override
    public List<DailyUserStats> getUserStats(String headerId) {
        return dailyMapper.getUserStats(headerId);
    }

    @Override
    public List<DailyUserStats> getUserStatsBySpu(String headerId, String userValue, String pathActive, String lifecycle) {
        return dailyMapper.getUserStatsBySpu(headerId,userValue,pathActive,lifecycle);
    }

    @Override
    public List<DailyUserStats> getUserStatsByProd(String headerId, String userValue, String pathActive, String lifecycle, String spuName) {
        return dailyMapper.getUserStatsByProd(headerId,userValue,pathActive,lifecycle,spuName);
    }

    @Override
    public Map<String, Object> getSelectedUserGroup(String groupId) {
        return dailyMapper.getSelectedUserGroup(groupId);
    }

    @Override
    public int getSmsIsCoupon(String smsCode, String isCoupon) {
        return dailyMapper.getSmsIsCoupon(smsCode, isCoupon);
    }

    @Override
    public int getValidDailyHead() {
        return dailyMapper.getValidDailyHead();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSmsCodeNull(String smsCode) {
        dailyMapper.updateSmsCodeNull(smsCode);
    }
}
