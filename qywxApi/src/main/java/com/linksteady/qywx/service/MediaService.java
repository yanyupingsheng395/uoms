package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxImage;
import com.linksteady.qywx.domain.QywxMediaImg;

import java.io.File;
import java.util.List;

public interface MediaService {

    /**
     * 获取欢迎语小程序卡片封面mediaId
     */
    String getMpMediaId(String identityType, Long identityId);

    int getImageCount();

    List<QywxImage> getImageList(int limit, int offset);

    void uploadImage(String title, File file, String opUserName) throws Exception;

    /**
     * 获取小程序封面列表数量
     * @return
     */
    int getMediaImageCount();

    /**
     * 获取小程序封面列表
     * @param limit
     * @param offset
     * @return
     */
    List<QywxMediaImg> getMediaImgList(int limit, int offset);

    /**
     *上传图片
     * @param title
     * @param file
     * @param username
     */
    void uploadQywxMaterial(String title, File file, String username);
}
