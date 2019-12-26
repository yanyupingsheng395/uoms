package com.linksteady.operate.service;

import com.linksteady.operate.domain.ManualHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019/12/25
 */
public interface ManualPushService {
    int getHeaderListCount(String scheduleDate);

    List<ManualHeader> getHeaderListData(int start, int end, String scheduleDate);

    void pushMessage(String headId, String pushType);

    void saveManualData(String smsContent, MultipartFile file, String sendType, String pushDate) throws IOException;

    Map<String, Object> getPushInfo(String headId);

    String getHeadStatus(String headId);
}
