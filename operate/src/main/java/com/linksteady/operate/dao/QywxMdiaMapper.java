package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxImage;

import java.util.List;

public interface QywxMdiaMapper {

    int getImageCount();

    List<QywxImage> getImageList(int limit, int offset);

    void saveMediaImg(String title,String url,String insertBy);
}
