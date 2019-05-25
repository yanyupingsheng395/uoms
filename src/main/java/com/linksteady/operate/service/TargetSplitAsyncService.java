package com.linksteady.operate.service;

import com.linksteady.operate.domain.TgtDismant;

import java.util.List;
import java.util.Map;

/**
 * 目标拆解异步任务
 */
public interface TargetSplitAsyncService {

     void targetSplit(Long targetId);

     Map<String, Object> getDismantData(Long targetId);
}
