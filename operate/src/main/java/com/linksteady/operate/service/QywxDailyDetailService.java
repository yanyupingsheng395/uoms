package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.vo.FollowUserVO;
import com.linksteady.operate.vo.RecProdVo;

import java.util.List;
import java.util.Map;

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
    List<QywxDailyDetail> getQywxDetailList(Long headId, int limit, int offset, String followUserId,long recProdId);

    /**
     * 推送列表记录数
     * @param headId
     * @return
     */
    int getQywxDetailCount(Long headId,String followUserId,long recProdId);


    /**
     * 转化明细
     * @param limit
     * @param offset
     * @param headId
     * @return
     */
    List<QywxDailyDetail> getConversionList(Long headId, int limit, int offset, String followUserId);

    /**
     * 转化记录数
     * @param headId
     * @return
     */
    int getConversionCount(Long headId,String followUserId);


    /**
     * 获取微信企业成员列表
     */
    List<FollowUserVO> getAllFollowUserList(Long headId);

    /**
     * 查询商品列表
     * @param headId
     * @return
     */
    List<RecProdVo> getRecProdList(Long headId);

    void resetPushDel(Long headId, List<Long> list);
}
