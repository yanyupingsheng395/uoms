package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxMediaImage;

import java.util.List;

public interface QywxMdiaMapper {

    int getMediaImageCount();

    List<QywxMediaImage> getMediaImageList(int limit, int offset);
}
