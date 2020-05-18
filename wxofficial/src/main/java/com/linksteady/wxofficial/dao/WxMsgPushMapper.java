package com.linksteady.wxofficial.dao;

import com.linksteady.wxofficial.entity.po.WxPushDetail;
import com.linksteady.wxofficial.entity.po.WxPushHead;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/29
 */
public interface WxMsgPushMapper {

    List<WxPushHead> getDataList(int limit, int offset);

    int getCount();

    void saveData(WxPushHead wxPushHead);

    void deleteById(String id);

    WxPushHead getHeadById(String id);

    void saveDetailData(@Param("wxPushDetails") List<WxPushDetail> wxPushDetails);

    List<WxPushDetail> getDetailDataByHeadId(String headId);

    void updateDetailList(@Param("toPushList") List<WxPushDetail> toPushList);

    void updatePushDate(Date date, String headId);

    void updateHeadStatus(String headId, String status);

    List<String> getToPushMsg();
}
