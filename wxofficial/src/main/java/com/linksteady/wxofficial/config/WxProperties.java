package com.linksteady.wxofficial.config;

/**
 * @author hxcao
 * @date 2020/4/17
 */

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 微信接口配置信息
 */
@Component
@Data
public class WxProperties {

    /**
     * APPID
     */
    private String appId;

    /**
     * 服务的域名
     */
    private String serviceDomain = "http://wx.growth-master.com";

    /**
     * 新增图文的接口
     */
    private String addNewsUrl = "/api/materialNews";

    /**
     * 上传永久素材的接口
     */
    private String uploadFileUrl = "/api/materialFileUpload";

    /**
     * 获取素材列表的接口
     */
    private String materialPageUrl = "/api/page";

    /**
     * 删除素材url
     */
    private String deleteMaterialUrl = "/api/materialDel";

    /**
     * 编辑素材URL
     */
    private String editMaterialUrl = "/api/editMaterialNews";

    /**
     * 获取素材直接文件
     */
    private String materialOtherUrl = "/api/materialOther";

    /**
     * 获取视频文件
     */
    private String materialVideoUrl = "/api/materialVideo";

    /**
     * 获取公众号已创建的标签
     */
    private String tagListUrl = "/api/taglist";

    /**
     * 新增标签
     */
    private String addTagUrl = "/api/saveTag";

    /**
     * 更改标签
     */
    private String updateTagUrl = "/api/updateTagById";

    /**
     * 删除标签
     */
    private String deleteTagUrl = "/api/removeTagById";

    /**
     * 同步用户列表
     */
    private String syncUserListUrl = "/api/syncUser";

    /**
     * 获取自定义菜单数据
     */
    private String menuListUrl = "/api/getMenu";

    /**
     * 新增菜单
     */
    private String menuAddUrl = "/api/updateMenu";

    /**
     * 公众号群推送
     */
    private String batchPushUrl = "/api/massPushByOpenIds";
}
