package com.linksteady.operate.sms.montnets.send;

import com.google.common.collect.Lists;
import com.linksteady.operate.sms.montnets.domain.Message;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author hxcao
 * @date 2019-10-11
 */
public class BatchSendMsgDemo {

    private static String userid = "JS4895";

    private static String pwd = "812316";

    private static String masterIpAddress = "61.145.229.28:7902";

    public static void main(String[] args) throws InterruptedException {
        //batchMsg();
    }

    /**
     * 相同内容批量发送
     */
    public static void batchMsg() {
        List<String> phones = findPhoneList();
        int pageSize = 998;
        int total = phones.size();
        int pageNum = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
        for (int i = 0; i < pageNum; i++) {
            int start = i * pageSize;
            int end = ((i + 1) * pageSize) > total ? total : (i + 1) * pageSize - 1;
            List<String> tmp = phones.subList(start, end);
            String mobiles = String.join(",", tmp);
            Message message = new Message();
            message.setMobile(mobiles);
            String content = "轻轻提醒你，卸妆水 https://dwz.cn/8uARXjgj 预售，付定金最低29.5元带走，记得双11付尾款立减还有拍立得抢！回T退";
            message.setContent(content);

            SendSms sendSms = new SendSms(userid, pwd, true, masterIpAddress);
            sendSms.batchSend(message);
            System.out.println("当前页数：" + i);
        }
    }

    public static List<String> findPhoneList() {
        LinkedList<String> mobiles = Lists.newLinkedList();
        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String dbURL = "jdbc:oracle:thin:@127.0.0.1:1522:orcl";
            conn = DriverManager.getConnection(dbURL, "hanhoogrowth_dev", "hanhoogrowthdev123");
            System.out.println("连接成功");
            String sql = "SELECT td.user_phone\n" +
                    "       FROM UO_OP_DAILY_DETAIL_11 TD\n" +
                    "       WHERE TD.push_status = 'P'\n" +
                    "         and td.touch_dt = '20191023' and td.order_period=12 \n" +
                    "         and td.sms_content in ('轻轻提醒你，卸妆水 https://dwz.cn/8uARXjgj 预售，付定金最低29.5元带走，记得双11付尾款立减还有拍立得抢！回T退')\n" +
                    "       group by user_phone";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                mobiles.addLast(resultSet.getString("user_phone"));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        mobiles.add("18911899103");
        return mobiles;
    }

    /**
     * 获取状态报告
     * @throws InterruptedException
     */
//    public static void getBatchRpt() throws InterruptedException {
//        int retsizeRpt = 100;
//        Thread.sleep(20_000);
//        RecvRptThread recvRptThread = new RecvRptThread(userid, pwd, true, retsizeRpt);
//        //线程获取 启动获取状态报告的线程
//        recvRptThread.start();
//    }


}