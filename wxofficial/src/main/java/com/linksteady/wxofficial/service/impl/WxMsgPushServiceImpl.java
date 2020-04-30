package com.linksteady.wxofficial.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.wxofficial.dao.WxMsgPushMapper;
import com.linksteady.wxofficial.dao.WxOfficialUserMapper;
import com.linksteady.wxofficial.entity.po.WxPushDetail;
import com.linksteady.wxofficial.entity.po.WxPushHead;
import com.linksteady.wxofficial.service.WxMsgPushService;
import com.linksteady.wxofficial.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/4/29
 */
@Service
public class WxMsgPushServiceImpl implements WxMsgPushService {

    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(8);

    @Autowired
    private WxMsgPushMapper wxMsgPushMapper;

    @Autowired
    private WxOfficialUserMapper wxOfficialUserMapper;

    @Override
    public int getCount() {
        return wxMsgPushMapper.getCount();
    }

    @Override
    public List<WxPushHead> getDataList(int limit, int offset) {
        return wxMsgPushMapper.getDataList(limit, offset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(WxPushHead wxPushHead) {
        wxPushHead.setCreateDt(new Date());
        // 写入head
        wxMsgPushMapper.saveData(wxPushHead);
        // 写入detail
        int id = wxPushHead.getId();
        List<String> openIdList = Lists.newArrayList();
        if("0".equalsIgnoreCase(wxPushHead.getIsTotalUser())) {
            // todo 读取的本地用户数据，需要考虑是否在此先进行用户同步？
            List<String> tagIds = Arrays.asList(wxPushHead.getTagId().split(","));
            openIdList = wxOfficialUserMapper.getUserListByTagId(tagIds);
        }else if("1".equalsIgnoreCase(wxPushHead.getIsTotalUser())) {
            openIdList = wxOfficialUserMapper.getUserListByTagId(null);
        }
        List<WxPushDetail> wxPushDetailList = Lists.newArrayList();
        openIdList.forEach(x->{
            WxPushDetail tmp = new WxPushDetail();
            tmp.setHeadId(id);
            tmp.setOpenId(x);
            tmp.setPushStatus("todo");
            // 根据实际推送设置的情况 update
            tmp.setPushDate(new Date());
            wxPushDetailList.add(tmp);
        });
        if(wxPushDetailList.size()>0) {
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
    public void pushMsg() {
        String headId = "3";
        List<WxPushDetail> toPushList = wxMsgPushMapper.getDetailDataByHeadId(headId);
        if(toPushList.size() > 0){
            toPushList.stream().collect(Collectors.groupingBy(WxPushDetail::getPushDate));
        }
    }

    /**
     * 将推送时间转换为
      */
    private Long getDelay(Date date) {
        return null;
    }



    public static void main(String[] args) {
        LocalTime localTime = LocalTime.parse("20200430 19:40", DateTimeFormatter.ofPattern("yyyyMMDD HH:MM"));
        System.out.println(localTime);
//        ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
//        MyTask<Integer> myTask = new MyTask<>(20);
//        ScheduledFuture<Integer> schedule = service.schedule(myTask, 5, TimeUnit.SECONDS);
//        System.out.println(schedule.isDone());
//        try {
//            Integer integer = schedule.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        service.schedule(new MyTask<>(21), 5, TimeUnit.SECONDS);

    }
}

class MyTask<T> implements Callable<T> {
    private Integer age = 18;

    public MyTask(Integer age){
        this.age = age;
    }

    @Override
    public T call() throws Exception {
        System.out.println(this.age);
        return null;
    }
}
