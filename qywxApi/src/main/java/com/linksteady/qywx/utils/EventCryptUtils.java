package com.linksteady.qywx.utils;

import com.linksteady.common.util.crypto.PKCS7Encoder;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.utils.xml.XStreamTransformer;
import com.linksteady.qywx.vo.WxXmlMessage;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 处理系统事件加解密的工具类
 */
public class EventCryptUtils {

    private static final ThreadLocal<DocumentBuilder> BUILDER_LOCAL = new ThreadLocal<DocumentBuilder>() {
        @Override
        protected DocumentBuilder initialValue() {
            try {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setExpandEntityReferences(false);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                return factory.newDocumentBuilder();
            } catch (ParserConfigurationException exc) {
                throw new IllegalArgumentException(exc);
            }
        }
    };

    public static String decrypt(String providerAesKey,String echoStr)
    {
        // 解密
        byte[] aesKey= Base64.decodeBase64(providerAesKey + "=");
        Charset CHARSET = StandardCharsets.UTF_8;
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(echoStr);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String xmlContent;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和AppId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = bytesNetworkOrder2Number(networkOrder);

            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return xmlContent;
    }

    /**
     * 4个字节的网络字节序bytes数组还原成一个数字.
     */
    private static int bytesNetworkOrder2Number(byte[] bytesInNetworkOrder) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= bytesInNetworkOrder[i] & 0xff;
        }
        return sourceNumber;
    }

    private static String extractEncryptPart(String xml) {
        try {
            DocumentBuilder db = BUILDER_LOCAL.get();
            Document document = db.parse(new InputSource(new StringReader(xml)));

            Element root = document.getDocumentElement();
            return root.getElementsByTagName("Encrypt").item(0).getTextContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static WxXmlMessage fromXml(String xml) {
        //修改微信变态的消息内容格式，方便解析
        xml = xml.replace("</PicList><PicList>", "");
        final WxXmlMessage xmlMessage = XStreamTransformer.fromXml(WxXmlMessage.class, xml);
        return xmlMessage;
    }



    public static WxXmlMessage decrypt(String providerToken,
                                       String providerAesKey,
                                       String msgSignature,
                                       String timestamp,
                                       String nonce,
                                       String encryptedXml)
    {
        // 提取密文
        String cipherText = extractEncryptPart(encryptedXml);

        // 验证安全签名
        String signature = SHA1.gen(providerToken, timestamp, nonce, cipherText);
        if (!signature.equals(msgSignature)) {
            throw new RuntimeException("加密消息签名校验失败");
        }

        String text=decrypt(providerAesKey,cipherText);
        return fromXml(text);
    }
}
