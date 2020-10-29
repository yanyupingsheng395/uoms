package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxImage;

import java.io.File;
import java.util.List;

public interface QywxMdiaService {

    int getImageCount();

    List<QywxImage>  getImageList(int limit, int offset);

    void uploadImage(String title, File file,String opUserName) throws Exception;

    /**
     * 获取商品的mediaId
     */
    String getMpMediaId(String productId)  throws Exception;



}
