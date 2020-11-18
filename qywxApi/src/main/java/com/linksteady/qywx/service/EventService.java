package com.linksteady.qywx.service;

import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.vo.WxXmlMessage;

public interface EventService {

    /**
     * 对接收到的指令事件进行处理
     */
    void handlerEvent(WxXmlMessage inMessage) throws WxErrorException;

}
