package com.linksteady.wxofficial.service.impl;

import com.linksteady.wxofficial.dao.ImageTextMapper;
import com.linksteady.wxofficial.entity.ImageText;
import com.linksteady.wxofficial.service.ImageTextService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/16
 */
@Service
public class ImageTextServiceImpl implements ImageTextService {

    @Autowired
    private ImageTextMapper imageTextMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addImageText(ImageText imageText) {
        imageTextMapper.addImageText(imageText);
    }

    @Override
    public int getCount() {
        return imageTextMapper.getCount();
    }

    @Override
    public List<ImageText> getDataListPage(int limit, int offset) {
        return imageTextMapper.getDataListPage(limit, offset);
    }

    @Override
    public ImageText getImageText(int id) {
        return imageTextMapper.getImageText(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImageText(String ids) {
        if(StringUtils.isNotEmpty(ids)) {
            List<String> idList = Arrays.asList(ids.split(","));
            if(idList.size() > 0) {
                imageTextMapper.deleteImageText(idList);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateImageText(ImageText imageText) {
        imageTextMapper.updateImageText(imageText);
    }
}
