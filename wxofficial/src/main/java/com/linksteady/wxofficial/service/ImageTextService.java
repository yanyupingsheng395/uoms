package com.linksteady.wxofficial.service;

import com.linksteady.wxofficial.entity.po.ImageText;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/16
 */
public interface ImageTextService {

    void addImageText(ImageText imageText) throws Exception;

    int getCount();

    List<ImageText> getDataListPage(int limit, int offset);

    ImageText getImageText(int id);

    void deleteImageText(String ids);

    void updateImageText(ImageText imageText);
}
