package com.linksteady.wxofficial.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信公众号用户
 * @author hxcao
 * @date 2020/4/20
 */
@Data
public class WxOfficialUserVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createDt;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateDt;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 逻辑删除标记（0：显示；1：隐藏）
     */
    private String delFlag;

    /**
     * 应用类型(1:小程序，2:公众号)
     */
    private String appType;

    /**
     * 是否订阅（1：是；0：否；2：网页授权用户）
     */
    private String subscribe;

    /**
     * 返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他
     */
    private String subscribeScene;

    /**
     * 关注时间
     */
    private Date subscribeTime;

    /**
     * 关注次数
     */
    private Integer subscribeNum;

    /**
     * 取消关注时间
     */
    private Date cancelSubscribeTime;

    /**
     * 用户标识
     */
    private String openId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别（1：男，2：女，0：未知）
     */
    private String sex;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 用户语言
     */
    private String language;

    /**
     * 头像
     */
    private String headimgUrl;

    /**
     * union_id
     */
    private String unionId;

    /**
     * 用户组
     */
    private String groupId;

    /**
     * 标签列表
     */
    private String tagidList;

    /**
     * 二维码扫码场景
     */
    private String qrSceneStr;

    /**
     * 地理位置纬度
     */
    private Short latitude;

    /**
     * 地理位置经度
     */
    private Short longitude;

    /**
     * 地理位置精度
     */
    private Short precision;

    public static final long serialVersionUID = 1L;
}
