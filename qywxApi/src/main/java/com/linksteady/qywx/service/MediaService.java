package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxImage;

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

}
