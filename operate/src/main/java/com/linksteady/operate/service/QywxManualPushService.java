package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxManualError;
import com.linksteady.operate.domain.QywxManualHeader;
import com.linksteady.operate.exception.LinkSteadyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface QywxManualPushService {
    /**
     * 插入数据
     */
    QywxManualError saveManualData(MultipartFile file, QywxManualHeader qywxManualHeader) throws IOException, LinkSteadyException;

    /**
     * 获取数量
     * @return
     */
    int getHeaderListCount();

    /**
     * 分页查询微信手动推送记录
     * @param limit
     * @param offset
     * @return
     */
    List<QywxManualHeader> getHeaderListData(int limit, int offset);

    /**
     * 推送信息
     * @throws Exception
     */
    void pushMessage(QywxManualHeader qywxManualHeader) throws Exception;

    /**
     *获得状态
     * @param headId
     * @return
     */
    String getHeadStatus(Long headId);

    /**
     * 删除记录
     * @param headId
     */
    void deleteData(Long headId);

    /**
     * 获取QywxManualHeader头表信息，用于校验内容是否填完整
     * @param headId
     * @return
     */
    QywxManualHeader getManualHeader(Long headId);

    /**
     * 获取活动效果详细信息
     * @param headId
     * @return
     */
    Map<String,Object> getHeaderEffectInfo(Long headId, String status);
}
