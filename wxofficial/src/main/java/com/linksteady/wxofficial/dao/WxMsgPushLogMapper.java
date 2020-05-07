package com.linksteady.wxofficial.dao;

import com.linksteady.wxofficial.entity.po.WxPushLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/6
 */
public interface WxMsgPushLogMapper {

    void saveDataList(@Param("xPushLogList") List<WxPushLog> wxPushLogList);
}
