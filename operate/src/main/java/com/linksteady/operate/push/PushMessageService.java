package com.linksteady.operate.push;

import com.linksteady.operate.domain.PushListInfo;

import java.util.List;

public interface PushMessageService {

    void push(List<PushListInfo> list);

    /**
     * 指定用户标记和消息内容 （一般用户测试） 如果返回 0表示发送成功  -1表示发送失败
     * @param uid
     * @param messageContent
     * @return
     */
    int push(String uid,String messageContent);
}
