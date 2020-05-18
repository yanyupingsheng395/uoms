package com.linksteady.wxofficial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.wxofficial.common.exception.LinkSteadyException;
import com.linksteady.wxofficial.common.util.Base64Img;
import com.linksteady.wxofficial.common.util.OkHttpUtil;
import com.linksteady.wxofficial.dao.ImageTextMapper;
import com.linksteady.wxofficial.entity.po.ImageText;
import com.linksteady.wxofficial.service.ImageTextService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hxcao
 * @date 2020/4/16
 */
@Service
public class ImageTextServiceImpl implements ImageTextService {

    private String appId = "wxa18e9ed9f8a213d8";

    private String serviceDomain = "http://wx.growth-master.com";

    private String addNewsUrl = "/api/materialNews";

    private String uploadFileUrl = "/api/materialFileUpload";

    @Autowired
    private ImageTextMapper imageTextMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addImageText(ImageText imageText) throws Exception {
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
        if (StringUtils.isNotEmpty(ids)) {
            List<String> idList = Arrays.asList(ids.split(","));
            if (idList.size() > 0) {
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
