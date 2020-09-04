package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxMdiaMapper;
import com.linksteady.operate.domain.QywxMediaImage;
import com.linksteady.operate.service.QywxMdiaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 企业微信素材
 */
@Service
@Slf4j
public class QywxMdiaServiceImpl implements QywxMdiaService {

    @Autowired
    QywxMdiaMapper qywxMdiaMapper;

    @Override
    public int getMediaImageCount() {
        return qywxMdiaMapper.getMediaImageCount();
    }

    @Override
    public List<QywxMediaImage> getMediaImageList(int limit, int offset) {
        return qywxMdiaMapper.getMediaImageList(limit,offset);
    }

    @Override
    public void uploadImage(String title, File file) {

    }
}
