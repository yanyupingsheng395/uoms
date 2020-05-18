package com.linksteady.wxofficial.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.dao.WxMsgPushMapper;
import com.linksteady.wxofficial.dao.WxOfficialUserMapper;
import com.linksteady.wxofficial.entity.po.WxPushDetail;
import com.linksteady.wxofficial.entity.po.WxPushHead;
import com.linksteady.wxofficial.service.WxMsgPushService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/4/29
 */
@Slf4j
@Service
public class WxMsgPushServiceImpl implements WxMsgPushService {

    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(8);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private WxMsgPushMapper wxMsgPushMapper;

    @Autowired
    private WxOfficialUserMapper wxOfficialUserMapper;

    @Autowired
    private OperateService operateService;

    @Autowired
    private WxProperties wxProperties;

    @Override
    public int getCount() {
        return wxMsgPushMapper.getCount();
    }

    @Override
    public List<WxPushHead> getDataList(int limit, int offset) {
        return wxMsgPushMapper.getDataList(limit, offset);
    }

    @PostConstruct
    public void init() {
        // 获取所有的todo,doing 的头表记录，加到线程池。不用考虑行表的状态，行表会通过状态筛选
        List<String> headIds = wxMsgPushMapper.getToPushMsg();
        log.info("检查数据库中共有[{}]条记录待执行.", headIds.size());
        headIds.forEach(this::pushMsg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(WxPushHead wxPushHead) {
        wxPushHead.setCreateDt(new Date());
        // 写入head
        wxPushHead.setStatus("todo");
        wxMsgPushMapper.saveData(wxPushHead);
        // 写入detail
        int id = wxPushHead.getId();
        List<String> openIdList = Lists.newArrayList();
        if ("0".equalsIgnoreCase(wxPushHead.getIsTotalUser())) {
            // todo 读取的本地用户数据，需要考虑是否在此先进行用户同步？
            List<String> tagIds = Arrays.asList(wxPushHead.getTagId().split(","));
            openIdList = wxOfficialUserMapper.getUserListByTagId(tagIds);
        } else if ("1".equalsIgnoreCase(wxPushHead.getIsTotalUser())) {
            openIdList = wxOfficialUserMapper.getUserListByTagId(null);
        }
        List<WxPushDetail> wxPushDetailList = Lists.newArrayList();
        openIdList.forEach(x -> {
            WxPushDetail tmp = new WxPushDetail();
            tmp.setHeadId(id);
            tmp.setOpenId(x);
            tmp.setPushStatus("todo");
            // 根据实际推送设置的情况 update
            tmp.setPushDate(new Date());
            wxPushDetailList.add(tmp);
        });
        if (wxPushDetailList.size() > 0) {
            wxMsgPushMapper.saveDetailData(wxPushDetailList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        wxMsgPushMapper.deleteById(id);
    }

    /**
     * 提交到线程池
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushMsg(String pushMethod, String pushPeriod, String headId) {
        wxMsgPushMapper.updateHeadStatus(headId, "doing");
        // 立刻推送
        Date date = null;
        if (pushMethod.equalsIgnoreCase("IMME")) {
            // 3分钟之后发送
            LocalDateTime now = LocalDateTime.now().plusMinutes(3);
            pushPeriod = now.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"));
            date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        }
        // 定时推送
        if (pushMethod.equalsIgnoreCase("FIXED")) {
            LocalDateTime old = LocalDateTime.parse(pushPeriod, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            date = Date.from(old.atZone(ZoneId.systemDefault()).toInstant());
        }
        wxMsgPushMapper.updatePushDate(date, headId);
        pushMsg(headId);
    }

    @Override
    public WxPushHead getMsgHeadById(String headId) {
        return wxMsgPushMapper.getHeadById(headId);
    }

    private void pushMsg(String headId) {
        WxPushHead wxPushHead = wxMsgPushMapper.getHeadById(headId);
        String sendMsgUrl = wxProperties.getServiceDomain() + wxProperties.getBatchPushUrl();
        String getStatusUrl = wxProperties.getServiceDomain() + wxProperties.getMessageMassGetUrl();
        List<WxPushDetail> toPushList = wxMsgPushMapper.getDetailDataByHeadId(headId);
        // 按标签来，不是全部用户
        if (wxPushHead.getIsTotalUser().equalsIgnoreCase("0")) {
            List<String> openIds = wxOfficialUserMapper.getUserListByTagId(Arrays.asList(wxPushHead.getTagId().split(",")));
            toPushList = wxMsgPushMapper.getDetailDataByHeadId(headId);
            toPushList = toPushList.stream().filter(x -> openIds.indexOf(x.getOpenId()) > -1).collect(Collectors.toList());
        }
        if (toPushList.size() > 0) {
            Map<Date, List<WxPushDetail>> dataList = toPushList.stream().collect(Collectors.groupingBy(WxPushDetail::getPushDate));
            int total = dataList.entrySet().size();
            AtomicInteger tmp = new AtomicInteger(0);
            List<WxPushDetail> finalToPushList = toPushList;
            for (Map.Entry<Date, List<WxPushDetail>> entry : dataList.entrySet()) {
                Date key = entry.getKey();
                List<WxPushDetail> value = entry.getValue();
                tmp.getAndIncrement();
                // 必须是大于当前日期的
                List<String> toUsers = value.stream().map(WxPushDetail::getOpenId).collect(Collectors.toList());
                if (toUsers.size() >= 2) {
                    int pagesize = 9999;
                    int pagenum = toUsers.size() % pagesize == 0 ? toUsers.size() / pagesize : (toUsers.size() / pagesize) + 1;
                    for (int i = 0; i < pagenum; i++) {
                        List<String> newToUsers = toUsers.stream().skip(pagesize * i).limit(pagesize).collect(Collectors.toList());
                        WxMpMassOpenIdsMessage message = new WxMpMassOpenIdsMessage();
                        message.setToUsers(newToUsers);
                        message.setMsgType(wxPushHead.getMsgType());
                        message.setContent(wxPushHead.getMsgContent());
                        message.setMediaId(wxPushHead.getMediaId());
                        // todo 在此根据toUsers.size()考虑分页的情况
                        Map<String, Object> param1 = Maps.newHashMap();
                        param1.put("message", message);
                        ScheduledFuture<String> schedule = scheduledThreadPool.schedule(
                                new PushMsgTask(operateService, sendMsgUrl, param1),
                                Duration.between(LocalTime.now(), LocalDateTime.ofInstant(key.toInstant(), ZoneId.systemDefault())).toMinutes(),
                                TimeUnit.MINUTES);
                        int finalI = i;
                        // 单线程获取执行结果的状态，以免阻塞主进程无法继续执行。
                        executorService.submit(() -> {
                            try {
                                String result = schedule.get();
                                WxMpMassSendResult wxMpMassSendResult = JSON.parseObject(result).toJavaObject(WxMpMassSendResult.class);
                                log.info("获取到发送结果", wxMpMassSendResult);
                                String msgId = wxMpMassSendResult.getMsgId();
                                Map<String, String> param2 = Maps.newHashMap();
                                param2.put("msgId", msgId);
                                String statusResult = operateService.callPostForm(getStatusUrl, param2);
                                finalToPushList.stream().filter(x -> newToUsers.indexOf(x.getOpenId()) > -1).forEach(
                                        y ->
                                        {
                                            y.setPushStatus(JSON.parseObject(statusResult).getString("msg"));
                                        }
                                );
                                wxMsgPushMapper.updateDetailList(finalToPushList);
                                if (tmp.get() == total && finalI == (pagenum - 1)) {
                                    wxMsgPushMapper.updateHeadStatus(headId, "done");
                                }
                            } catch (InterruptedException e) {
                                log.error("获取任务执行结果失败", e);
                            } catch (ExecutionException e) {
                                log.error("获取任务执行结果失败", e);
                            }
                        });
                    }
                } else {
                    log.info("推送人数不足2条，不予推送！");
                }
            }
        }
    }
}

/**
 * 推送消息的线程任务
 */
class PushMsgTask implements Callable<String> {

    private OperateService operateService;

    private String url;

    private Map<String, Object> param;

    public PushMsgTask(OperateService operateService, String url, Map<String, Object> param) {
        this.operateService = operateService;
        this.url = url;
        this.param = param;
    }

    @Override
    public String call() {
        return operateService.callPostBody(url, param);
    }
}
