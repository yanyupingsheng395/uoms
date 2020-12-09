package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.domain.QywxMediaImg;
import com.linksteady.qywx.domain.QywxParam;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MediaMapper {
    /**
     * 根据dentityType和identityId获取QywxMediaImg
     */
    QywxMediaImg getQywxMediaImg(Long identityId, String identityType);

    /**
     * 根据商品id查w_product_media中的prod_pic_url
     * @param identityId
     * @return
     */
    String getProductMediaContent(Long identityId);

    /**
     * 根据商品id取uo_qywx_param中的media_content
     */
    QywxParam getMediaContent(@Param("type")String type);

    /**
     * 更新
     * @param identityId
     * @param mediaId
     * @param expreDt
     */
    void updateQywxMediaImgBymediaId(Long identityId,String identityType,String mediaId, LocalDateTime expreDt,byte[] mediaContent);

    /**
     * 新增uo_qywx_media_img数据
     * @param mediaContent
     * @param nowtime
     * @param mediaId
     * @param expreDt
     * @param identityId
     * @param identityType
     */
    void saveQywxMediaImg(byte[] mediaContent, String title,LocalDateTime nowtime, String mediaId, LocalDateTime expreDt, Long identityId, String identityType);

    int getImageCount();

    List<QywxImage> getImageList(int limit, int offset);

    void saveMediaImg(String title,String url,String insertBy);

    int getMediaImageCount();

    List<QywxMediaImg> getMediaImgList(int limit, int offset);
}
