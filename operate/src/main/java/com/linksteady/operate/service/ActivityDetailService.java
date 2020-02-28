package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.vo.ActivityContentVO;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityDetailService {

    int getDataCount(String headId, String planDtWid,String groupId);

    List<ActivityDetail> getPageList(int start, int end, String headId, String planDtWid,String groupId);
}
