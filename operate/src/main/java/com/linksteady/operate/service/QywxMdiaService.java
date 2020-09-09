package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxMediaImage;

import java.io.File;
import java.util.List;

public interface QywxMdiaService {

    int getMediaImageCount();

    List<QywxMediaImage>  getMediaImageList(int limit, int offset);

    void uploadImage(String title, File file,String opUserName) throws Exception;

    /**
     * 获取临时素材的media_id (供获取小程序卡片专用的方法)
     */
    String getMminiprogramMediaId();
}
