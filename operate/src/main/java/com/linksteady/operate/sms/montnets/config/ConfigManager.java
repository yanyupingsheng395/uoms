package com.linksteady.operate.sms.montnets.config;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author hxcao
 * @date 2019-09-11
 */
public class ConfigManager {

    /**
     * 请求路径
     */
    public static String REQUEST_PATH = "/sms/v2/std/";

    /**
     * 主IP和端口
     */
    public static String masterIpAndPort = null;

    /**
     * 主域名和端口    主域名和端口可能为空
     */
    public static String masterDomainAndPort = null;

    /**
     * 备用IP端口信息 IP和端口号以:号连接
     */
    public static List<String> ipAndPortBak = new ArrayList<String>();

    /**
     * 备IP和备域名的对应关系。key:备用IP和端口   value:域名和端口
     */
    public static Map<String, String> ipAndDomainBakMap = new HashMap<String, String>();

    /**
     * 主IP状态 0正常 1异常
     */
    public static int masterIPState = 0;

    /**
     * 可用IP
     */
    public static String availableIpAndPort = null;

    /**
     * 主IP最近检测时间 1970年距今的毫秒数
     */
    public static long LAST_CHECK_TIME = 0L;

    /**
     * 主IP异常检测时间间隔 5分钟
     */
    public static long CHECK_TIME_INTERVAL = 5 * 60 * 1000L;

    /**
     * IP是否设置
     */
    public static boolean ipIsSet = false;

    /**
     * 密码是否加密 默认加密
     */
    public static boolean IS_ENCRYPT_PWD = true;

    /**
     * 设置IP的方法
     *
     * @param masterIpAddress 主IP
     * @param ipAddress1      备用1
     * @param ipAddress2      备用2
     *                        * @param ipAddress3 备用3
     * @return 0:成功 ;-1:失败
     */
    public static int setIpInfo(String masterIpAddress, String ipAddress1, String ipAddress2, String ipAddress3) {
        try {
            if (masterIpAddress == null || "".equals(masterIpAddress.trim())) {
                return -1;
            }
            //判断ipAddress是否是域名
            boolean ipAddressIsDomain = isDomain(masterIpAddress.split(":")[0]);
            //如果是域名，则通过域名获取IP
            if (ipAddressIsDomain) {
                //通过域名获取IP
                String ip = getIpByDomain(masterIpAddress.split(":")[0]);
                //通过域名获取IP成功
                if (ip != null && !"".equals(ip.trim())) {
                    //设置主IP信息
                    masterIpAndPort = ip + ":" + masterIpAddress.split(":")[1];
                    //设置主域名信息
                    masterDomainAndPort = masterIpAddress;
                } else {
                    // 通过域名获取IP失败
                    masterIpAndPort = masterIpAddress;
                    //设置主域名信息
                    masterDomainAndPort = masterIpAddress;
                }
            } else {
                // 设置主IP和端口
                masterIpAndPort = masterIpAddress;
            }

            //设置备用域名1
            if (ipAddress1 != null && !"".equals(ipAddress1.trim())) {
                setBakAddress(ipAddress1);
            }

            //设置备用域名2
            if (ipAddress2 != null && !"".equals(ipAddress2.trim())) {
                setBakAddress(ipAddress2);
            }

            //设置备用域名3
            if (ipAddress3 != null && !"".equals(ipAddress3.trim())) {
                setBakAddress(ipAddress3);
            }
            //IP设置
            ipIsSet = true;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 清除所有设置的IP
     *
     * @return
     * @description
     */
    public static int removeAllIpInfo() {
        try {
            masterIpAndPort = null;
            masterDomainAndPort = null;
            ipAndPortBak = new ArrayList<String>();
            ipAndDomainBakMap = new HashMap<String, String>();
            masterIPState = 0;
            availableIpAndPort = null;
            //IP未设置
            ipIsSet = false;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * 判断是否是域名
     *
     * @param address IP信息
     * @return
     */
    private static boolean isDomain(String address) {
        boolean isDomain = false;
        try {
            //正则表达式判断是否包含字母，如果包含字母，则是域名
            isDomain = Pattern.compile("[a-zA-Z]").matcher(address).find();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDomain;
    }

    /**
     * 通过域名获取IP
     *
     * @param domain 域名
     * @return
     */
    private static String getIpByDomain(String domain) {
        String ip = null;
        try {
            //通过域名获取IP
            ip = InetAddress.getByName(domain).getHostAddress();
        } catch (Exception e) {
            ip = null;
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 设置备用IP
     */
    private static void setBakAddress(String ipAddress) throws Exception {
        try {
            //判断ipAddress是否是域名
            boolean ipAddressIsDomain = isDomain(ipAddress.split(":")[0]);
            //如果是域名，则通过域名获取IP
            if (ipAddressIsDomain) {
                //通过域名获取IP
                String ip = getIpByDomain(ipAddress.split(":")[0]);
                //通过域名获取IP成功
                if (ip != null && !"".equals(ip.trim())) {
                    //设置备IP信息
                    ipAndPortBak.add(ip + ":" + ipAddress.split(":")[1]);
                    //设置备IP信息和域名的对应关系
                    ipAndDomainBakMap.put(ip + ":" + ipAddress.split(":")[1], ipAddress);
                } else {
                    //通过域名获取IP失败
                    ipAndPortBak.add(ipAddress);
                    //设置对应关系
                    ipAndDomainBakMap.put(ipAddress, ipAddress);
                }
            } else {
                //如果不是域名
                ipAndPortBak.add(ipAddress);
            }
        } catch (Exception e) {
            throw e;
        }
    }

}
