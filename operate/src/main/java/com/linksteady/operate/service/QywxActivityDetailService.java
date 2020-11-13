package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxActivityDetail;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface QywxActivityDetailService {

    int getDataCount(Long planId);

    List<QywxActivityDetail> getPageList(int limit, int offset, Long planId);
}
