package com.linksteady.wxofficial.common.wechat.service;

import com.linksteady.wxofficial.common.wechat.entity.ImageTextInfo;
import com.linksteady.wxofficial.common.wechat.entity.MaterialInfo;
import com.linksteady.wxofficial.entity.bo.MaterialBo;
import okhttp3.ResponseBody;

import java.io.File;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/4/17
 */
public interface OperateService {

    /**
     * 上传永久素材
     * @param materialInfo: 素材类
     * @return
     */
    Map<String, String> uploadMaterial(MaterialInfo materialInfo, File file) throws Exception;

    /**
     * 新增图文消息
     * @param imageTextInfo: 图文类
     * @return
     */
    Map<String, String> addNews(ImageTextInfo imageTextInfo);

    /**
     * 获取图文列表
     * @return
     */
    MaterialBo getMaterialList(Map<String, String> data);


    /**
     * 删除素材
     * @param id
     * @return
     * @throws Exception
     */
    Map<String, String> deleteMaterial(String id) throws Exception;


    /**
     * 编辑微信素材
     * @param imageTextInfo
     * @return
     */
    Map<String, String> editMaterial(ImageTextInfo imageTextInfo, String mediaId);

    /**
     * 获取微信直接文件
     * @param mediaId
     * @param fileName
     * @return
     */
    ResponseBody getMaterialOther(String mediaId, String fileName);

    /**
     * 获取微信视频文件
     * @param mediaId
     * @return
     */
    Map<String, String> getMaterialVideo(String mediaId);
}
