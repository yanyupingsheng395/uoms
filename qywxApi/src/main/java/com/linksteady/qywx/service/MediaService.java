package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.domain.QywxMediaImg;

import java.io.File;
import java.util.List;

public interface MediaService {

    /**
     * 获取欢迎语小程序卡片封面mediaId
     */
    String getMpMediaId(String identityType, Long identityId) throws Exception;

    /**
     * 删除MpMedia数据
     */
    void deleteMpMediaId(String identityType, Long identityId);

    int getImageCount();

    List<QywxImage> getImageList(int limit, int offset);

    void uploadImage(String title, File file, String opUserName) throws Exception;

    /**
     * 获取临时素材数量
     * @return
     */
    int getMediaImgCount();

    /**
     * 获取临时素材列表
     * @param limit
     * @param offset
     * @return
     */
    List<QywxMediaImg> getMediaImgList(int limit, int offset);

    /**
     * 获取临时素材数量(有效)
     * @return
     */
    int getValidMediaImgCount();

    /**
     * 获取临时素材列表(有效)
     * @param limit
     * @param offset
     * @return
     */
    List<QywxMediaImg> getValidMediaImgList(int limit, int offset);

    /**
     *上传图片
     * @param title
     * @param file
     * @param username
     */
    void uploadQywxMaterial(String title, File file, String username);
}
