package com.linksteady.qywx.utils.xml;

import com.linksteady.qywx.vo.WxXmlMessage;
import com.thoughtworks.xstream.XStream;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XStreamTransformer {

  protected static final Map<Class, XStream> CLASS_2_XSTREAM_INSTANCE = configXStreamInstance();

  /**
   * xml -> pojo
   */
  @SuppressWarnings("unchecked")
  public static <T> T fromXml(Class<T> clazz, String xml) {
    T object = (T) CLASS_2_XSTREAM_INSTANCE.get(clazz).fromXML(xml);
    return object;
  }

  @SuppressWarnings("unchecked")
  public static <T> T fromXml(Class<T> clazz, InputStream is) {
    T object = (T) CLASS_2_XSTREAM_INSTANCE.get(clazz).fromXML(is);
    return object;
  }

  /**
   * 注册扩展消息的解析器.
   *
   * @param clz     类型
   * @param xStream xml解析器
   */
  public static void register(Class clz, XStream xStream) {
    CLASS_2_XSTREAM_INSTANCE.put(clz, xStream);
  }

  /**
   * pojo -> xml.
   */
  public static <T> String toXml(Class<T> clazz, T object) {
    return CLASS_2_XSTREAM_INSTANCE.get(clazz).toXML(object);
  }

  private static Map<Class, XStream> configXStreamInstance() {
    Map<Class, XStream> map = new HashMap<>();
    map.put(WxXmlMessage.class, configWxEpXmlMessage());
    return map;
  }

  private static XStream configWxEpXmlMessage() {
    XStream xstream = XStreamInitializer.getInstance();

    xstream.processAnnotations(WxXmlMessage.class);
//    xstream.processAnnotations(WxEpXmlMessage.ScanCodeInfo.class);
//    xstream.processAnnotations(WxEpXmlMessage.SendPicsInfo.class);
//    xstream.processAnnotations(WxEpXmlMessage.SendPicsInfo.Item.class);
//    xstream.processAnnotations(WxEpXmlMessage.SendLocationInfo.class);
    return xstream;
  }

//  private static XStream configWxCpXmlOutImageMessage() {
//    XStream xstream = XStreamInitializer.getInstance();
//
//    xstream.processAnnotations(WxCpXmlOutMessage.class);
//    xstream.processAnnotations(WxCpXmlOutImageMessage.class);
//    return xstream;
//  }
//
//  private static XStream configWxCpXmlOutNewsMessage() {
//    XStream xstream = XStreamInitializer.getInstance();
//
//    xstream.processAnnotations(WxCpXmlOutMessage.class);
//    xstream.processAnnotations(WxCpXmlOutNewsMessage.class);
//    xstream.processAnnotations(WxCpXmlOutNewsMessage.Item.class);
//    return xstream;
//  }
//
//  private static XStream configWxCpXmlOutTextMessage() {
//    XStream xstream = XStreamInitializer.getInstance();
//
//    xstream.processAnnotations(WxCpXmlOutMessage.class);
//    xstream.processAnnotations(WxCpXmlOutTextMessage.class);
//    return xstream;
//  }
//
//  private static XStream configWxCpXmlOutVideoMessage() {
//    XStream xstream = XStreamInitializer.getInstance();
//
//    xstream.processAnnotations(WxCpXmlOutMessage.class);
//    xstream.processAnnotations(WxCpXmlOutVideoMessage.class);
//    xstream.processAnnotations(WxCpXmlOutVideoMessage.Video.class);
//    return xstream;
//  }
//
//  private static XStream configWxCpXmlOutVoiceMessage() {
//    XStream xstream = XStreamInitializer.getInstance();
//
//    xstream.processAnnotations(WxCpXmlOutMessage.class);
//    xstream.processAnnotations(WxCpXmlOutVoiceMessage.class);
//    return xstream;
//  }
//
//  private static XStream configWxCpTpXmlPackage() {
//    XStream xstream = XStreamInitializer.getInstance();
//    xstream.processAnnotations(WxCpTpXmlPackage.class);
//
//    return xstream;
//  }
//
//  private static XStream configWxCpTpXmlMessage() {
//    XStream xstream = XStreamInitializer.getInstance();
//    xstream.processAnnotations(WxCpTpXmlMessage.class);
//
//    return xstream;
//  }

}
