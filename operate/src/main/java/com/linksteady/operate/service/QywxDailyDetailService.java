package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.vo.FollowUserVO;
import com.linksteady.operate.vo.RecProdVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /**
     * 删除选中的用户明细数据
     * @param headId
     * @param list
     */
    void delDetail(Long headId, List<Long> list);

    /**
     * 上传优惠券流水号
     * @param file
     * @param couponId
     */
    void uploadCoupon(MultipartFile file, Long couponId,String couponIdentity)throws LinkSteadyException, IOException;

    /**
     * 点击生成100个优惠券编码
     * @param couponId
     */
    void couponToSequence(Long couponId,String couponIdentity)throws LinkSteadyException;
}
