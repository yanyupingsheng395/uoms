package com.linksteady.operate.service;

import com.linksteady.operate.domain.ManualHeader;
import com.linksteady.operate.exception.LinkSteadyException;
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

    List<ManualHeader> getHeaderListData(int limit, int offset, String scheduleDate);

    void pushMessage(Long headId, String pushType) throws Exception;

    void saveManualData(String smsContent, MultipartFile file, String sendType, String pushDate) throws IOException, LinkSteadyException;

    Map<String, Object> getPushInfo(Long headId);

    String getHeadStatus(Long headId);

    void deleteData(Long headId);
}
