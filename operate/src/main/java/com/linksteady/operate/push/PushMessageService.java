package com.linksteady.operate.push;

import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.domain.PushListLager;
import com.linksteady.operate.sms.montnets.domain.Message;

import java.util.List;

public interface PushMessageService {

    int push(List<PushListInfo> list);

    /**
     * 指定用户标记和消息内容 （一般用户测试） 如果返回 0表示发送成功  -1表示发送失败
     * @param uid
     * @param messageContent
     * @return
     */
    int push(String uid,String messageContent);

    /**
     * 批量发送接口
     * @param messageContent 消息内容
     * @param pushList 推送列表
     */
    int batchPush(String messageContent, List<PushListLager> pushList);
}
