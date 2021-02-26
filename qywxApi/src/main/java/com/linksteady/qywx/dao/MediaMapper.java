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
     * 更新临时素材
     * @param identityId
     * @param mediaId
     * @param expreDt
     */
    void updateQywxMediaImgBymediaId(String fileName,Long identityId,String identityType,String mediaId, LocalDateTime expreDt);

    /**
     * 保存临时素材
     * @param fileName
     * @param nowtime
     * @param mediaId
     * @param expreDt
     * @param identityId
     * @param identityType
     */
    void saveQywxMediaImg(String fileName, String title,LocalDateTime nowtime, String mediaId, LocalDateTime expreDt, Long identityId, String identityType,String userName);

    /**
     * 获取所有的临时素材 (所有 包括失效的)
     * @return
     */
    int getMediaImgCount();

    /**
     * 获取所有的临时素材明细 (所有 包括失效的)
     * @param limit
     * @param offset
     * @return
     */
    List<QywxMediaImg> getMediaImgList(int limit, int offset);

    /**
     * 获取有效的临时素材 (界面上传)
     * @return
     */
    int getValidMediaImgCount();

    /**
     * 获取有效的临时素材明细 (界面上传)
     * @param limit
     * @param offset
     * @return
     */
    List<QywxMediaImg> getValidMediaImgList(int limit, int offset);

    ///////////////////////////////////永久素材 ////////////////////////////////////
    /**
     * 获取永久素材的数量
     * @return
     */
    int getImageCount();

    /**
     * 获取永久素材的明细
     * @param limit
     * @param offset
     * @return
     */
    List<QywxImage> getImageList(int limit, int offset);

    /**
     * 保存永久素材
     * @param title
     * @param url
     * @param insertBy
     * @param fileName
     */
    void saveQywxImages(String title,String url,String insertBy,String fileName);
}
