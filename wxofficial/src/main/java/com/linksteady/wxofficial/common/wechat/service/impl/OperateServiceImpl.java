package com.linksteady.wxofficial.common.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.wxofficial.common.exception.LinkSteadyException;
import com.linksteady.wxofficial.common.util.Base64Img;
import com.linksteady.wxofficial.common.util.OkHttpUtil;
import com.linksteady.wxofficial.common.wechat.entity.ImageTextInfo;
import com.linksteady.wxofficial.common.wechat.entity.MaterialInfo;
import com.linksteady.wxofficial.common.wechat.enums.MediaTypeEnum;
import com.linksteady.wxofficial.common.wechat.enums.ResultCodeEnum;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.bo.MaterialBo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hxcao
 * @date 2020/4/17
 */
@Service
@Slf4j
public class OperateServiceImpl implements OperateService {

    @Autowired
    private WxProperties wxProperties;

    public static void main(String[] args) {
        String s = "data:image/png;base64,";
        System.out.println(s.substring("data:image/".length(), s.lastIndexOf(";base64,")));
    }

    @Override
    public Map<String, String> uploadMaterial(MaterialInfo materialInfo, File file) throws Exception {
        String url = wxProperties.getServiceDomain() + wxProperties.getUploadFileUrl();
        String mediaType = materialInfo.getMediaType();
        Map<String, String> body = Maps.newHashMap();
        body.put("appId", wxProperties.getAppId());
        body.put("title", materialInfo.getTitle());
        body.put("introduction", materialInfo.getIntroduction());
        body.put("mediaType", mediaType);

        if(MediaTypeEnum.IMAGE.code.equalsIgnoreCase(mediaType)) {
            String fileSuffix = materialInfo.getBase64Code().substring("data:image/".length(), materialInfo.getBase64Code().lastIndexOf(";base64,"));
            materialInfo.setFileName("tmp." + fileSuffix);
            file = Base64Img.base64ToFile(materialInfo.getBase64Code(), materialInfo.getFileName());
        }else {
            if(null == file) {
                throw new LinkSteadyException("上传的文件为空！");
            }
        }
        String uploadResultStr = OkHttpUtil.postFileAndData(url, body, file);
        JSONObject uploadResultObject = JSON.parseObject(uploadResultStr);
        Map<String, String> result = Maps.newHashMap();
        String code = uploadResultObject.getString("code");
        result.put("code", code);
        if (ResultCodeEnum.RES_200.code.equalsIgnoreCase(code)) {
            result.put("mediaId", JSON.parseObject(uploadResultObject.getString("msg")).getString("mediaId"));
        }
        file.deleteOnExit();
        return result;
    }

    @Override
    public Map<String, String> addNews(ImageTextInfo imageTextInfo) {
        String url = wxProperties.getServiceDomain() + wxProperties.getAddNewsUrl();
        Map<String, Object> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        param.put("articles", new ArrayList<>().add(imageTextInfo));
        JSONObject jsonResult = JSON.parseObject(OkHttpUtil.postRequestBody(url, JSON.toJSONString(param)));
        log.info("新增图文结果：" + jsonResult.toJSONString());
        Map<String, String> result = Maps.newHashMap();
        String code = jsonResult.getString("code");
        result.put("code", code);
        if (ResultCodeEnum.RES_200.code.equalsIgnoreCase(code)) {
            result.put("mediaId", JSON.parseObject(jsonResult.getString("msg")).getString("mediaId"));
        }
        return result;
    }

    @Override
    public MaterialBo getMaterialList(Map<String, String> data) {
        MaterialBo materialBo;
        String url = wxProperties.getServiceDomain() + wxProperties.getMaterialPageUrl();
        JSONObject jsonObject = JSON.parseObject(OkHttpUtil.postFormBody(url, data));
        if (MediaTypeEnum.NEWS.code.equalsIgnoreCase(data.get("type"))) {
            materialBo = newsConvert(jsonObject);
        } else {
            materialBo = imageConvert(jsonObject);
        }
        return materialBo;
    }

    @Override
    public Map<String, String> deleteMaterial(String id) throws Exception {
        Map<String, String> result = Maps.newHashMap();
        String url = wxProperties.getServiceDomain() + wxProperties.getDeleteMaterialUrl();
        Map<String, String> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        param.put("id", id);
        String response = OkHttpUtil.postFormBody(url, param);
        log.info("删除素材结果：" + response);
        String code = JSON.parseObject(response).getString("code");
        result.put("code", code);
        return result;
    }

    /**
     * 更新素材
     *
     * @param imageTextInfo
     * @return
     */
    @Override
    public Map<String, String> editMaterial(ImageTextInfo imageTextInfo, String mediaId) {
        String url = wxProperties.getServiceDomain() + wxProperties.getEditMaterialUrl();
        List<ImageTextInfo> list = Lists.newArrayList();
        list.add(imageTextInfo);
        Map<String, Object> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        param.put("mediaId", mediaId);
        param.put("articles", list);
        JSONObject jsonResult = JSON.parseObject(OkHttpUtil.postRequestBody(url, JSON.toJSONString(param)));
        log.info("编辑素材结果：" + jsonResult.toJSONString());
        Map<String, String> result = Maps.newHashMap();
        String code = jsonResult.getString("code");
        result.put("code", code);
        return result;
    }

    @Override
    public ResponseBody getMaterialOther(String mediaId, String fileName) {
        String url = wxProperties.getServiceDomain() + wxProperties.getMaterialOtherUrl();
        Map<String, String> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        param.put("mediaId", mediaId);
        param.put("fileName", fileName);
        return OkHttpUtil.postFormBodyOfResponse(url, param);
    }

    @Override
    public Map<String, String> getMaterialVideo(String mediaId) {
        String url = wxProperties.getServiceDomain() + wxProperties.getMaterialVideoUrl();
        Map<String, String> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        param.put("mediaId", mediaId);
        JSONObject jsonObject = JSON.parseObject(OkHttpUtil.postFormBody(url, param));
        log.info("获取微信视频直接素材文件：" + jsonObject);
        Map<String, String> result = Maps.newHashMap();
        String code = jsonObject.getString("code");
        String downUrl = JSON.parseObject(jsonObject.getString("msg")).getString("downUrl");
        result.put("code", code);
        result.put("downUrl", downUrl);
        return result;
    }

    @Override
    public String getDataList(String url) {
        Map<String, String> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        String result = OkHttpUtil.postFormBody(url, param);
        log.info("获取列表数据结果:" + result);
        return result;
    }

    @Override
    public String deleteById(String url, String id) {
        Map<String, String> param = Maps.newHashMap();
        param.put("appId", wxProperties.getAppId());
        param.put("tagId", id);
        String result = OkHttpUtil.postFormBody(url, param);
        log.info("删除数据结果:" + result);
        return result;
    }

    @Override
    public String saveData(String url, Map<String, Object> data) {
        Map<String, Object> param = data;
        param.put("appId", wxProperties.getAppId());
        String result = OkHttpUtil.postRequestBody(url, JSON.toJSONString(param));
        log.info("新增数据结果:" + result);
        return result;
    }

    @Override
    public String updateData(String url, Map<String, String> data) {
        Map<String, String> param = data;
        param.put("appId", wxProperties.getAppId());
        String result = OkHttpUtil.postRequestBody(url, JSON.toJSONString(param));
        log.info("更新数据结果:" + result);
        return result;
    }

    @Override
    public String callPostBody(String url, Map<String, Object> data) {
        Map<String, Object> param = data;
        param.put("appId", wxProperties.getAppId());
        String result = OkHttpUtil.postRequestBody(url, JSON.toJSONString(param));
        log.info("post结果:" + result);
        return result;
    }

    /**
     * 图文消息转化
     *
     * @return
     */
    private MaterialBo newsConvert(JSONObject jsonObject) {
        MaterialBo materialBo = new MaterialBo();
        String code = jsonObject.getString("code");
        if (ResultCodeEnum.RES_200.code.equalsIgnoreCase(code)) {
            materialBo = jsonObject.toJavaObject(MaterialBo.class);
            JSONObject msgJSON = JSONObject.parseObject(jsonObject.getString("msg"));
            String itemCount = msgJSON.getString("itemCount");
            String itemsStr = msgJSON.getString("items");
            JSONArray jsonArray = JSONArray.parseArray(itemsStr);
            List<MaterialBo.Article> articleList = Lists.newArrayList();
            jsonArray.forEach(x -> {
                String mediaId = ((JSONObject) x).getString("mediaId");
                String content = ((JSONObject) x).getString("content");
                MaterialBo.Article article = ((JSONObject) JSONArray.parseArray(JSONObject.parseObject(content).getString("articles")).get(0)).toJavaObject(MaterialBo.Article.class);
                article.setMediaId(mediaId);
                articleList.add(article);
            });
            materialBo.setItemCount(itemCount);
            materialBo.setCode(code);
            materialBo.setArticles(articleList);
        }
        return materialBo;
    }

    /**
     * 图片转化
     *
     * @return
     */
    private MaterialBo imageConvert(JSONObject jsonObject) {
        MaterialBo materialBo = new MaterialBo();
        String code = jsonObject.getString("code");
        if (ResultCodeEnum.RES_200.code.equalsIgnoreCase(code)) {
            materialBo = jsonObject.toJavaObject(MaterialBo.class);
            JSONObject msgJSON = JSONObject.parseObject(jsonObject.getString("msg"));
            String itemCount = msgJSON.getString("itemCount");
            String itemsStr = msgJSON.getString("items");
            JSONArray itemArray = JSONArray.parseArray(itemsStr);
            List<MaterialBo.Item> items = itemArray.toJavaList(MaterialBo.Item.class);
            materialBo.setItemCount(itemCount);
            materialBo.setCode(code);
            materialBo.setItems(items);
        }
        return materialBo;
    }

    @Override
    public String callPostForm(String url, Map<String, String> param) {
        param.put("appId", wxProperties.getAppId());
        String result = OkHttpUtil.postFormBody(url, param);
        return result;
    }
}
