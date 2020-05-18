package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.vo.QywxUserVO;

import java.util.List;

/**
 * @author huang
 * @date 2020-05-12
 */
public interface QywxDailyDetailService {

    /**
     * 转化文案 （替换变量、匹配券)
     * @param headerId
     */
    void generate(Long headerId) throws Exception;

    /**
     * 推送详情
     * @param limit
     * @param offset
     * @param headId
     * @return
     */
    List<QywxDailyDetail> getQywxDetailList(Long headId, int limit, int offset, String qywxUserId);

    /**
     * 推送列表记录数
     * @param headId
     * @return
     */
    int getQywxDetailCount(Long headId,String qywxUserId);


    /**
     * 转化明细
     * @param limit
     * @param offset
     * @param headId
     * @return
     */
    List<QywxDailyDetail> getConversionList(Long headId, int limit, int offset, String qywxUserId);

    /**
     * 转化记录数
     * @param headId
     * @return
     */
    int getConversionCount(Long headId,String qywxUserId);


    /**
     * 获取微信企业成员列表
     */
    List<QywxUserVO> getQywxUserList(Long headId);

}
