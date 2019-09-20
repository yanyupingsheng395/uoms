package com.linksteady.operate.sms.montnets.util;

import com.google.gson.Gson;
import com.linksteady.operate.sms.montnets.config.ConfigManager;
import com.linksteady.operate.sms.montnets.domain.Message;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;

/**
 * @功能概要：检测IP信息类
 */
public class CheckAddress {
    // http请求失败
    public static int ERROR_310099 = -310099;


    // 检测IP 请求超时时间(毫秒) 3秒
    public static int CHECK_HTTP_REQUEST_TIMEOUT = 3 * 1000;

    // 检测IP 响应超时时间(毫秒) 30秒
    public static int CHECK_HTTP_RESPONSE_TIMEOUT = 30 * 1000;

    // json解析器
    private Gson gson = new Gson();

    /**
     * 获取可用的地址信息
     *
     * @param userid    账号
     * @param password  密码
     * @param timestamp 时间戳
     * @return null：未获取到可用的地址信息;不为空:获取到可用的地址信息
     * @description
     */
    public String getAddressByUserID(String userid, String password, String timestamp) {
        try {
            // 判断主IP是否正常
            if (ConfigManager.masterIPState == 0) {
                // 主IP与可用IP不相等，则将可用IP设置为主IP
                if (!ConfigManager.masterIpAndPort.equals(ConfigManager.availableIpAndPort)) {
                    // 将可用IP设置为主IP
                    ConfigManager.availableIpAndPort = ConfigManager.masterIpAndPort;
                }
                // 正常
                return ConfigManager.availableIpAndPort;
            }
            // 主IP异常
            else {
                //如果没有设置备用IP信息,则每次直接检测主IP
                if (ConfigManager.ipAndPortBak == null || ConfigManager.ipAndPortBak.size() < 1) {
                    //检测主IP是否正常
                    String address = checkMasterAddress(userid, password, timestamp);
                    if (address != null && !"".equals(address)) {
                        return address;
                    }
                }

                //主IP异常，如果主IP异常时间超过5分钟，则检测异常主IP
                if ((Calendar.getInstance().getTimeInMillis() - ConfigManager.LAST_CHECK_TIME) > ConfigManager.CHECK_TIME_INTERVAL) {
                    //检测主IP是否正常
                    String address = checkMasterAddress(userid, password, timestamp);
                    if (address != null && !"".equals(address)) {
                        return address;
                    }
                }


                // 循环备用IP和可用IP比较，如果相等，则说明该备用IP可用
                if (ConfigManager.ipAndPortBak != null && ConfigManager.ipAndPortBak.size() > 0) {
                    for (int i = 0; i < ConfigManager.ipAndPortBak.size(); i++) {
                        // 如果备IP与可用IP相等，则该备用IP可用
                        if (ConfigManager.ipAndPortBak.get(i).equals(ConfigManager.availableIpAndPort)) {
                            // 返回备用IP
                            return ConfigManager.ipAndPortBak.get(i);
                        }

                    }
                }

                // 可用的IP地址不存在，则循环检测备用IP是否可用
                String availableAddress = checkAddress(userid, password, timestamp);
                if (availableAddress != null) {
                    // 设置账号当前可用IP地址
                    // 将检测出来的可用IP设置为可用IP
                    ConfigManager.availableIpAndPort = availableAddress;
                    return availableAddress;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 检测出可用的IP地址
     *
     * @param userid    账号
     * @param password  密码
     * @param timestamp 时间戳
     * @return null：无可用的IP地址;否则有可用的IP地址
     * @description
     */
    private String checkAddress(String userid, String password, String timestamp) {
        String address = null;
        try {
            // 调用查询余额的方法检测连接是否可用
            int result = ERROR_310099;

            //通过域名获取成功
            boolean isDomainGet = false;
            List<String> ipAndPortBakList = ConfigManager.ipAndPortBak;
            String ipAddressBak = null;
            //未设置备用IP
            if (ipAndPortBakList == null || ipAndPortBakList.size() < 1) {
                return address;
            }
            // 循环检测备用IP是否可用
            for (int i = 0; i < ipAndPortBakList.size(); i++) {
                ipAddressBak = ipAndPortBakList.get(i);

                // 调用查询余额的方法检测连接是否可用
                result = checkAddressAvailable(userid, password, timestamp, ipAddressBak);
                // 查询余额成功
                if (result == 0) {
                    address = ipAddressBak;
                    break;
                } else {
                    //如果用IP和端口查询失败，则检查IP和端口是否有对应的域名
                    String domainBak = ConfigManager.ipAndDomainBakMap.get(ipAddressBak);
                    //该备用IP存在域名，则检测该域名是否可以使用
                    if (domainBak == null || "".equals(domainBak)) {
                        //获取不到域名，则进入下一次循环
                        continue;
                    }
                    //通过域名获取IP
                    String ip = getIpByDomain(domainBak.split(":")[0]);

                    if (ip == null || "".equals(ip.trim())) {
                        //通过域名获取不到IP，则进入下一次循环
                        continue;
                    }
                    //通过域名获取IP成功
                    //新获取的IP和端口
                    String newIpAndPort = ip + ":" + ipAddressBak.split(":")[1];
                    // 调用查询余额的方法检测该备用IP对应的域名是否正常
                    result = checkAddressAvailable(userid, password, timestamp, newIpAndPort);
                    //如果正常，则将该备用IP修改
                    if (result == 0) {
                        isDomainGet = true;
                        //移除旧的备用IP和域名的对应关系
                        ConfigManager.ipAndDomainBakMap.remove(ipAddressBak);
                        //添加新的IP和域名的对应关系
                        ConfigManager.ipAndDomainBakMap.put(newIpAndPort, domainBak);
                        //将新的IP和端口赋值给address
                        address = newIpAndPort;
                        break;
                    }
                }
            }
            //IP地址不为空并且通过域名获取
            if (address != null && !"".equals(address) && isDomainGet) {
                //移除备用IP
                ConfigManager.ipAndPortBak.remove(ipAddressBak);
                //新增备用IP
                ConfigManager.ipAndPortBak.add(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
            address = null;
        }
        return address;
    }

    /**
     * @param userId    账号
     * @param password  密码
     * @param timestamp 时间戳
     * @param ipAddress 地址信息
     * @return 0:代表可用; 非0:代表不可用
     * @description 检查地址信息是否可用
     */
    private int checkAddressAvailable(String userId, String password, String timestamp, String ipAddress) {
        int result = ERROR_310099;
        try {
            Message message = new Message();

            // 设置账号 将账号转换成大写
            message.setUserid(userId.toUpperCase());

            // 设置密码
            message.setPwd(password);

            //设置时间戳
            message.setTimestamp(timestamp);


            String Message = null;
            String balanceHost = "http://" + ipAddress;
            // 通过HTTP POST调用，查询余额
            Message = executeNotKeepAlivePost(message, balanceHost + ConfigManager.REQUEST_PATH + "get_balance");

            // Message为-310099,则请求查询余额接口失败，否则请求查询余额接口成功
            if (String.valueOf(ERROR_310099).equals(Message)) {
                // 返回错误码 查询余额失败
                return ERROR_310099;
            }

            if (Message != null && !"".equals(Message.trim())) {
                // 解析返回值
                JSONObject parseObject = (JSONObject) JSONValue.parse(Message);
                // 获取是否成功标识
                result = Integer.parseInt(String.valueOf(parseObject.get("result")));
            } else {
                // 返回错误码 查询余额失败
                return ERROR_310099;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ERROR_310099;
        }
        return result;
    }

    /**
     * 短连接发送
     *
     * @param obj     请求对象
     * @param httpUrl http请求地址
     * @return
     * @throws Exception
     * @description
     * @author tanglili <jack860127@126.com>
     * @datetime 2016-7-6 上午08:41:40
     */
    private String executeNotKeepAlivePost(Object obj, String httpUrl) throws Exception {
        String result = String.valueOf(ERROR_310099);
        HttpClient httpclient = null;
        try {
            //将实体对象，生成JSON字符串
            String entityValue = gson.toJson(obj);
            // 定义请求头
            HttpPost httppost = new HttpPost(httpUrl);
            httppost.setHeader("Content-Type", "text/json");


            StringEntity stringEntity = new StringEntity(entityValue, HTTP.UTF_8);

            // 设置参数的编码UTF-8
            httppost.setEntity(stringEntity);

            // 创建连接
            httpclient = new DefaultHttpClient();

            // 检测IP 设置请求超时时间 设置为3秒
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CHECK_HTTP_REQUEST_TIMEOUT);
            // 检测IP 设置响应超时时间 设置为30秒
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, CHECK_HTTP_RESPONSE_TIMEOUT);

            HttpEntity entity = null;
            HttpResponse httpResponse = null;

            try {
                // 向网关请求
                httpResponse = httpclient.execute(httppost);
                // 若状态码为200，则代表请求成功
                if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                    //获取响应的实体
                    entity = httpResponse.getEntity();
                    //响应的内容不为空，并且响应的内容长度大于0,则获取响应的内容
                    if (entity != null && entity.getContentLength() > 0) {
                        try {
                            //请求成功，能获取到响应内容
                            result = EntityUtils.toString(entity);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            //获取内容失败，返回空字符串
                            result = "";
                        }
                    } else {
                        //请求成功，但是获取不到响应内容
                        result = "";
                    }
                } else {
                    // 设置错误码
                    result = String.valueOf(ERROR_310099);
                    //System.out.println("请求失败："+httpResponse.getStatusLine().toString());
                }

            } catch (Exception e) {
                result = String.valueOf(ERROR_310099);
                //这里将错误信息打印出来会过于频繁，从而不打印。
                //e.printStackTrace();
            }

        } catch (Exception e) {
            result = String.valueOf(ERROR_310099);
            //这里将错误信息打印出来会过于频繁，从而不打印。
            //e.printStackTrace();
        } finally {
            // 关闭连接
            if (httpclient != null) {
                try {
                    httpclient.getConnectionManager().shutdown();
                } catch (Exception e2) {
                    // 关闭连接失败
                    e2.printStackTrace();
                }

            }
        }
        return result;

    }

    /**
     * 检查主IP
     *
     * @return
     * @description
     */
    private String checkMasterAddress(String userid, String password, String timestamp) {
        try {
            //设置最近检查时间
            ConfigManager.LAST_CHECK_TIME = Calendar.getInstance().getTimeInMillis();

            // 调用查询余额的方法检测连接是否可用
            int result = checkAddressAvailable(userid, password, timestamp, ConfigManager.masterIpAndPort);

            // 查询余额成功
            if (result == 0) {
                //可用IP设置为主IP
                ConfigManager.availableIpAndPort = ConfigManager.masterIpAndPort;
                //将主IP设置为可用
                ConfigManager.masterIPState = 0;
                System.out.println("主ipAddress[" + ConfigManager.masterIpAndPort + "]恢复正常。");
                return ConfigManager.availableIpAndPort;
            }

            //如果主域名不存在，则返回
            if (ConfigManager.masterDomainAndPort == null || "".equals(ConfigManager.masterDomainAndPort.trim())) {
                return null;
            }
            //如果主域名存在，则通过域名去获取IP。
            //通过域名获取IP
            String ip = getIpByDomain(ConfigManager.masterDomainAndPort.split(":")[0]);

            //通过域名获取IP失败
            if (ip == null || "".equals(ip.trim())) {
                return null;
            }
            //通过域名获取IP成功
            //新获取的IP和端口
            String newIpAndPort = ip + ":" + ConfigManager.masterDomainAndPort.split(":")[1];
            //如果失败，通过新获取的IP检查余额
            result = checkAddressAvailable(userid, password, timestamp, newIpAndPort);
            // result为0，代表成功 result为非0，则代表失败
            if (result == 0) {
                if (ConfigManager.masterIpAndPort.equals(newIpAndPort)) {
                    System.out.println("主ipAddress[" + ConfigManager.masterIpAndPort + "]恢复正常。");
                } else {
                    System.out.println("主ipAddress由[" + ConfigManager.masterIpAndPort + "]切换为[" + newIpAndPort + "]。");
                }
                //可用IP设置为新的主IP
                ConfigManager.availableIpAndPort = newIpAndPort;
                //将主IP设置为可用
                ConfigManager.masterIPState = 0;
                // 将新的IP和端口设置好
                ConfigManager.masterIpAndPort = newIpAndPort;
                System.out.println("通过域名获取的主IP正常,主IP和端口：" + newIpAndPort);
                return ConfigManager.availableIpAndPort;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过域名获取IP
     *
     * @param domain 域名
     * @return
     */
    private String getIpByDomain(String domain) {
        String ip = null;
        try {
            //通过域名获取IP
            ip = InetAddress.getByName(domain).getHostAddress();
        } catch (Exception e) {
            ip = null;
            //打印会频繁，注释
            //e.printStackTrace();
        }
        return ip;
    }

}
