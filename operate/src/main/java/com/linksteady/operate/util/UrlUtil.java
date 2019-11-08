package com.linksteady.operate.util;

import java.io.UnsupportedEncodingException;

public class UrlUtil {
    private final static String ENCODE = "GBK";
    /**
     * URL 解码
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * URL 转码
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @return void
     */
    public static void main(String[] args) {
        String str = "https://uland.taobao.com/quan/detail?sellerId=1664976033&activityId=9f082bb4cda84399ac7d7d5c9463a324";
        System.out.println(getURLEncoderString(str));
        //System.out.println(getURLDecoderString(str));

    }
}
