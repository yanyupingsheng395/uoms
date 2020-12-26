package com.linksteady.qywx.vo;

import com.alibaba.fastjson.JSON;
import com.linksteady.qywx.utils.WxOpenCryptUtil;
import com.linksteady.qywx.utils.XmlUtils;
import com.linksteady.qywx.utils.xml.XStreamCDataConverter;
import com.linksteady.qywx.utils.xml.XStreamTransformer;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * 微信事件信息
 */
@Data
@Slf4j
@XStreamAlias("xml")
public class WxXmlMessage implements Serializable {

    @XStreamAlias("ToUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String ToUserName;

    @XStreamAlias("FromUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String FromUserName;

    @XStreamAlias("CreateTime")
    private Long CreateTime;

    @XStreamAlias("MsgType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String MsgType;

    @XStreamAlias("Event")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String Event;

    @XStreamAlias("UserID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String userId;

    @XStreamAlias("ExternalUserID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String externalUserId;

    @XStreamAlias("WelcomeCode")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String welcomeCode;


    @XStreamAlias("ChangeType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String ChangeType;

    @XStreamAlias("State")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String state;

    @XStreamAlias("ChatId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String ChatId;


    protected static WxXmlMessage fromXml(String xml) {
        //修改微信变态的消息内容格式，方便解析
        xml = xml.replace("</PicList><PicList>", "");
        final WxXmlMessage xmlMessage = XStreamTransformer.fromXml(WxXmlMessage.class, xml);
        return xmlMessage;
    }

    protected static WxXmlMessage fromXml(InputStream is) {
        return XStreamTransformer.fromXml(WxXmlMessage.class, is);
    }

    /**
     * 从加密字符串转换
     *
     * @param encryptedXml        密文
     * @param timestamp           时间戳
     * @param nonce               随机串
     * @param msgSignature        签名串
     */
    public static WxXmlMessage fromEncryptedXml(String corpId,String token,String aesKey, String encryptedXml,
                                                String timestamp, String nonce, String msgSignature) {
        WxOpenCryptUtil cryptUtil = new WxOpenCryptUtil(corpId,token,aesKey);
        String plainText = cryptUtil.decrypt(msgSignature, timestamp, nonce, encryptedXml);
        log.debug("解密后的原始xml消息内容：{}", plainText);
        return fromXml(plainText);
    }

    /**
     * 验证URL有效性
     * @param echostr
     * @param timestamp
     * @param nonce
     * @param msgSignature
     * @return
     */
    public static String verifyURL(String corpId,String token,String aesKey, String echostr,
                                                  String timestamp, String nonce, String msgSignature) {
        WxOpenCryptUtil cryptUtil = new WxOpenCryptUtil(corpId,token,aesKey);
        String plainText = cryptUtil.verifyURL(msgSignature, timestamp, nonce, echostr);
        return plainText;
    }


    public static WxXmlMessage fromEncryptedMpXml(String corpId,String token,String aesKey, String encryptedXml,
                                                  String timestamp, String nonce, String msgSignature) {
        WxOpenCryptUtil cryptUtil = new WxOpenCryptUtil(corpId,token,aesKey);
        String plainText = cryptUtil.decrypt(msgSignature, timestamp, nonce, encryptedXml);
        return WxXmlMessage.fromXml(plainText);
    }

    @Override
    public String toString() {
        return JSON.toJSON(this).toString();
    }
}

