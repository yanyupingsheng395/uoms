package com.linksteady.wxofficial.dao;
import com.linksteady.wxofficial.entity.po.ImageText;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/16
 */
public interface ImageTextMapper {
    void addImageText(ImageText imageText);

    int getCount();

    List<ImageText> getDataListPage(int limit, int offset);

    ImageText getImageText(int id);

    void deleteImageText(List<String> idList);

    void updateImageText(ImageText imageText);
}
