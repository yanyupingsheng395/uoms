package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.QywxDailyDetailMapper;
import com.linksteady.operate.dao.QywxDailyMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.QywxDailyService;
import com.linksteady.operate.service.QywxMdiaService;
import com.linksteady.operate.service.QywxMessageService;
import com.linksteady.operate.vo.QywxUserStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * 日运营头表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Slf4j
@Service
public class QywxDailyServiceImpl implements QywxDailyService {

    @Autowired
    private QywxDailyMapper qywxDailyMapper;

    @Autowired
    private QywxDailyDetailMapper qywxDailyDetailMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Autowired
    private QywxMessageService qywxMessageService;

    @Autowired
    private QywxMdiaService qywxMdiaService;

    @Override
    public List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate) {
        return qywxDailyMapper.getHeadList(limit, offset, taskDate);
    }

    @Override
    public int getTotalCount(String touchDt) {
        return qywxDailyMapper.getTotalCount(touchDt);
    }

    @Override
    public Map<String, Object> getTaskOverViewData(Long headId) {
        Map<String, Object> result = Maps.newHashMap();

        //获取按商品、spu的统计数据
        List<QywxUserStatsVO> statsBySpu = qywxDailyMapper.getTargetInfoBySpu(headId);

        List<String> spuList = statsBySpu.stream().map(QywxUserStatsVO::getSpuName).collect(Collectors.toList());
        List<Integer> spuCountList = statsBySpu.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        //获取最后一个SPU下的商品信息
        List<QywxUserStatsVO> statsByProd = qywxDailyMapper.getTargetInfoByProd(headId, spuList.get(spuList.size() - 1));

        List<String> prodList = statsByProd.stream().map(QywxUserStatsVO::getProdName).collect(Collectors.toList());
        List<Integer> prodCountList = statsByProd.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        //按价值的用户分布
        List<QywxUserStatsVO> statsByUserValue = qywxDailyMapper.getTargetInfoByUserValue(headId);
        List<String> userValueList = statsByUserValue.stream().map(QywxUserStatsVO::getUserValue).collect(Collectors.toList());
        List<String> userValueLabelList = statsByUserValue.stream().map(QywxUserStatsVO::getUserValueLabel).collect(Collectors.toList());
        List<Integer> userValueCountList = statsByUserValue.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        Map<String, Object> matrixResult = getMatrixData(headId, statsByUserValue.get(statsByUserValue.size() - 1).getUserValue());

        //按成员的分布
        List<QywxUserStatsVO> statsByUser = qywxDailyMapper.getTargetInfoByUser(headId);
        List<String> qywxUserList = statsByUser.stream().map(QywxUserStatsVO::getQywxUser).collect(Collectors.toList());
        List<Integer> qywxCountList = statsByUser.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        //按个性化补贴的分布
        List<QywxUserStatsVO> statsByCoupon = qywxDailyMapper.getTargetInfoByCoupon(headId);

        //按用户成长目标的分布
        List<QywxUserStatsVO> statsByGrowthType = qywxDailyMapper.getTargetInfoByGrowthType(headId);
        List<String> growthTypeLabelList = statsByGrowthType.stream().map(QywxUserStatsVO::getGrowthType).collect(Collectors.toList());
        List<Integer> growthTypeCountList = statsByGrowthType.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        //按用户成长目标【序列】的分布
        List<QywxUserStatsVO> statsByGrowthTypeSeries = qywxDailyMapper.getTargetInfoByGrowthSeriesType(headId);
        List<String> growthSeriesTypeLabelList = statsByGrowthTypeSeries.stream().map(QywxUserStatsVO::getGrowthSeriesType).collect(Collectors.toList());
        List<Integer> growthSeriesTypeCountList = statsByGrowthTypeSeries.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        QywxDailyHeader qywxDailyHeader = qywxDailyMapper.getHeadInfo(headId);

        result.put("spuList", spuList);
        result.put("spuCountList", spuCountList);

        result.put("prodList", prodList);
        result.put("prodCountList", prodCountList);

        result.put("userValueList", userValueList);
        result.put("userValueLabelList", userValueLabelList);
        result.put("userValueCountList", userValueCountList);

        result.put("matrixResult", matrixResult);

        result.put("qywxUserList", qywxUserList);
        result.put("qywxCountList", qywxCountList);

        result.put("growthTypeLabelList", growthTypeLabelList);
        result.put("growthTypeCountList", growthTypeCountList);

        result.put("growthSeriesTypeLabelList", growthSeriesTypeLabelList);
        result.put("growthSeriesTypeCountList", growthSeriesTypeCountList);

        result.put("statsByCoupon", statsByCoupon);

        result.put("taskDate", new SimpleDateFormat("yyyy年MM月dd日").format(qywxDailyHeader.getTaskDate()));
        result.put("userNum", qywxDailyHeader.getTotalNum());
        return result;
    }

    /**
     * 获取给定SPU下的商品分布数据
     *
     * @param headId
     * @param spuName
     * @return
     */
    @Override
    public Map<String, Object> getProdCountBySpu(Long headId, String spuName) {
        Map<String, Object> result = Maps.newHashMap();
        List<QywxUserStatsVO> statsByProd = qywxDailyMapper.getTargetInfoByProd(headId, spuName);

        List<String> prodList = statsByProd.stream().map(QywxUserStatsVO::getProdName).collect(Collectors.toList());
        List<Integer> prodCountList = statsByProd.stream().map(QywxUserStatsVO::getUcnt).collect(Collectors.toList());

        result.put("prodList", prodList);
        result.put("prodCountList", prodCountList);

        return result;
    }

    /**
     * 根据给定 用户价值 获取 活跃度和生命周期的交叉表格数据
     *
     * @param headId
     * @param userValue
     * @return
     */
    @Override
    public Map<String, Object> getMatrixData(Long headId, String userValue) {
        Map<String, String> pathActiveMap = configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> lifeCycleMap = configService.selectDictByTypeCode("LIFECYCLE");

        //给定价值下 在生命周期和活跃度上的分布表格
        List<QywxUserStatsVO> statsMatrix = qywxDailyMapper.getTargetInfoMatrix(headId, userValue);
        //转化成table 方便同时通过活跃度和生命周期进行查找
        Table<String, String, Integer> tables = HashBasedTable.create();
        statsMatrix.forEach(i -> {
            tables.put(i.getPathActivity(), i.getLifecycle(), i.getUcnt());
        });

        Map<String, Object> matrixResult = Maps.newHashMap();
        matrixResult.put("columnTitle", lifeCycleMap.values().toArray());
        List<List<String>> rows = Lists.newArrayList();

        List<String> row = null;
        //遍历活跃度
        for (Map.Entry<String, String> pathActive : pathActiveMap.entrySet()) {
            row = Lists.newArrayList();
            row.add(pathActive.getValue());

            //对每一个活跃度遍历 生命周期
            for (Map.Entry<String, String> lifeCycle : lifeCycleMap.entrySet()) {
                //判断是否存在对应的值
                if (tables.contains(pathActive.getKey(), lifeCycle.getKey())) {
                    row.add(String.valueOf(tables.get(pathActive.getKey(), lifeCycle.getKey())));
                } else {
                    row.add("0");
                }

            }
            rows.add(row);
        }
        matrixResult.put("rows", rows);

        return matrixResult;
    }

    @Override
    public boolean getTransContentLock(String headId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //如果key不存在，则将key的值设置为value，同时返回true. 如果key不存在，则什么也不做，返回false.
        boolean flag = valueOperations.setIfAbsent("qywx_daily_trans_lock", headId, 60, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public void delTransLock() {
        redisTemplate.delete("ywx_daily_trans_lock");
    }

    @Override
    public QywxDailyHeader getHeadInfo(Long headId) {
        return qywxDailyMapper.getHeadInfo(headId);
    }

    /**
     * 对每日运营进行推送
     *
     * @param qywxDailyHeader
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void push(QywxDailyHeader qywxDailyHeader, Long effectDays) throws Exception {
        //更新状态为已执行
        int count = qywxDailyMapper.updateStatus(qywxDailyHeader.getHeadId(), "done", qywxDailyHeader.getVersion());
        if (count == 0) {
            throw new OptimisticLockException("记录已被其他用户修改，请返回刷新后重试");
        }
        Long headId = qywxDailyHeader.getHeadId();
        //更新效果计算天数到头表
        qywxDailyMapper.updateHeaderPushInfo(headId, effectDays);
        //按导购和消息分组 (follow_user_id,qywx_msg_sign)
        List<QywxDailyDetail> userList = qywxDailyDetailMapper.getQywxUserListByHeadId(headId);
        // 写入push_list
        String mediaId = qywxMdiaService.getMminiprogramMediaId();
        String appId = configService.getValueByName(ConfigEnum.qywxMiniProgramAppId.getKeyCode());

        Map<String, List<QywxDailyDetail>> followUserIdList = userList.stream().collect(Collectors.groupingBy(QywxDailyDetail::getFollowUserId));
        followUserIdList.entrySet().forEach(x -> {
            String followUserId = x.getKey();
            List<QywxDailyDetail> qywxDailyDetails = x.getValue();
            // 推送消息
            Map<String, List<QywxDailyDetail>> signUserList = qywxDailyDetails.stream().collect(Collectors.groupingBy(QywxDailyDetail::getQywxMsgSign));
            signUserList.entrySet().forEach(y -> {
                List<QywxDailyDetail> lastUserList = y.getValue();
                int pageSize = 10000;
                if (lastUserList.size() <= pageSize) {
                    log.info("当前推送数据量<=10000");
                    if(lastUserList.size() > 0) {
                        //获取逗号分割的外部联系人ID列表
                        List<String> contactIdList=lastUserList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList());
                        QywxPushList qywxPushList=new QywxPushList();
                        qywxPushList.setTextContent(lastUserList.get(0).getTextContent());
                        qywxPushList.setMpTitle(lastUserList.get(0).getMpTitle());
                        qywxPushList.setMpUrl(lastUserList.get(0).getMpUrl());
                        qywxPushList.setMpMediaId(mediaId);
                        qywxPushList.setMpAppid(appId);
                        qywxPushList.setExternalContactIds(StringUtils.join(contactIdList,","));
                        qywxPushList.setFollowUserId(followUserId);
                        qywxPushList.setSourceId(lastUserList.get(0).getDetailId());
                        qywxDailyMapper.insertPushList(qywxPushList);
                        //推送并更新状态
                        pushQywxMsg(qywxPushList,contactIdList);
                    }
                } else {
                    int totalSize = lastUserList.size();
                    int pageNum = totalSize % pageSize == 0 ? (totalSize / pageSize) : ((totalSize / pageSize) + 1);
                    for (int i = 0; i < pageNum; i++) {
                        int start = i * pageSize;
                        int end = (i + 1) * pageSize - 1;
                        end = Math.min(end, totalSize);
                        log.info("当前文本推送数据页[{}]", start + ":" + end);
                        List<QywxDailyDetail> tmpUserList = lastUserList.subList(start, end);
                        if(tmpUserList.size() > 0) {
                            //获取逗号分割的外部联系人ID列表
                            List<String> contactIdList=tmpUserList.stream().map(QywxDailyDetail::getQywxContractId).collect(Collectors.toList());
                            String contactIds=StringUtils.join(contactIdList,",");
                            QywxPushList qywxPushList=new QywxPushList();
                            qywxPushList.setTextContent(tmpUserList.get(0).getTextContent());
                            qywxPushList.setMpTitle(tmpUserList.get(0).getMpTitle());
                            qywxPushList.setMpUrl(tmpUserList.get(0).getMpUrl());
                            qywxPushList.setMpMediaId(mediaId);
                            qywxPushList.setMpAppid(appId);
                            qywxPushList.setExternalContactIds(contactIds);
                            qywxPushList.setFollowUserId(followUserId);
                            qywxPushList.setSourceId(tmpUserList.get(0).getDetailId());
                            qywxDailyMapper.insertPushList(qywxPushList);
                            //推送并更新状态
                            pushQywxMsg(qywxPushList,contactIdList);
                        }
                    }
                }
            });
        });
    }

    /**
     * 推送企业微信消息
     *
     * @param qywxPushList (待推送的对象)
     */
    private void pushQywxMsg(QywxPushList qywxPushList,List<String> contactIdList) {
        if(null==contactIdList||contactIdList.size()==0)
        {
            qywxDailyMapper.updatePushList(qywxPushList.getPushId(),"F","","","推送列表为空");
            return;
        }

        String msgContent = qywxPushList.getTextContent();
        String mpTitle = qywxPushList.getMpTitle();
        String mpUrl = qywxPushList.getMpUrl();
        String mediaId =qywxPushList.getMpMediaId();
        String appId = qywxPushList.getMpAppid();

        boolean flag=true;
        QywxMessage qywxMessage = new QywxMessage();

        if (StringUtils.isNotEmpty(msgContent)) {
            qywxMessage.setText(msgContent);
            flag=false;
        }
        if(StringUtils.isNotEmpty(mpTitle))
        {
            qywxMessage.setMpTitle(mpTitle);
            qywxMessage.setMpPicMediaId(mediaId);
            qywxMessage.setMpAppid(appId);
            qywxMessage.setMpPage(mpUrl);
            flag=false;
        }

        //消息中至少有文本、小程序中的任何一个，才进行推送
        if(flag)
        {
            qywxDailyMapper.updatePushList(qywxPushList.getPushId(),"F","","","消息为空");

        }else
        {
            String result = qywxMessageService.pushQywxMessage(qywxMessage, qywxPushList.getFollowUserId(), contactIdList);
            log.info("日运营企微：推送结果【{}】", result);
            JSONObject jsonObject = JSON.parseObject(result);
            String msgId = jsonObject.getString("msg");
            String code = jsonObject.getString("code");
            String failList = jsonObject.getString("data");

            if(StringUtils.isNotEmpty(code)&&code.equalsIgnoreCase("200"))
            {
                qywxDailyMapper.updatePushList(qywxPushList.getPushId(),"S",msgId,failList,"推送成功");
            }else
            {
                qywxDailyMapper.updatePushList(qywxPushList.getPushId(),"F","","","调用企业微信接口失败");
            }
        }
    }

    @Override
    public Map<String, Object> getPushEffectChange(Long headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<LocalDate> xdatas = Lists.newLinkedList();

        QywxDailyHeader qywxDailyHeader = qywxDailyMapper.getHeadInfo(headId);
        // 提交任务日期
        String taskDt = qywxDailyHeader.getTaskDateStr();

        //获取任务观察的天数
        int effectDays = qywxDailyHeader.getEffectDays().intValue();

        //任务提交的日期
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));

        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(effectDays + 1);

        //时间轴的数据
        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate);
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<DailyStatis> dataList = Lists.newArrayList();
        Map<String, DailyStatis> dailyStatisMap = dataList.stream().collect(Collectors.toMap(DailyStatis::getConversionDateStr, a -> a));

        //转化人数
        List<Long> convertNumList = Lists.newArrayList();
        List<Double> convertRateList = Lists.newArrayList();
        List<Long> convertSpuNumList = Lists.newArrayList();
        List<Double> convertSpuRateList = Lists.newArrayList();

        xdatas.forEach(x -> {
            //判断当前是否有数据
            DailyStatis dailyStatis = dailyStatisMap.get(x.format(DateTimeFormatter.ofPattern(dateFormat)));

            //找不到转化数据
            if (null == dailyStatis) {
                if (x.isAfter(LocalDate.now()) || x.isEqual(LocalDate.now())) {
                    //填充空值
                    convertNumList.add(null);
                    convertRateList.add(null);
                    convertSpuNumList.add(null);
                    convertSpuRateList.add(null);
                } else {
                    //填充0
                    convertNumList.add(0L);
                    convertRateList.add(0D);
                    convertSpuNumList.add(0L);
                    convertSpuRateList.add(0D);
                }
            } else {
                convertNumList.add(dailyStatis.getConvertNum());
                convertRateList.add(dailyStatis.getConvertRate());
                convertSpuNumList.add(dailyStatis.getConvertSpuNum());
                convertSpuRateList.add(dailyStatis.getConvertSpuRate());
            }
        });

        result.put("xdata", xdatas.stream().map(x -> x.format(DateTimeFormatter.ofPattern(dateFormat))).collect(Collectors.toList()));
        result.put("ydata1", convertNumList);
        result.put("ydata2", convertRateList);
        result.put("ydata3", convertSpuNumList);
        result.put("ydata4", convertSpuRateList);
        return result;
    }

    @Override
    public QywxDailyStaffEffect getDailyStaffEffect(Long headId, String followUserId) {
        return qywxDailyDetailMapper.getDailyStaffEffect(headId, followUserId);
    }

}
