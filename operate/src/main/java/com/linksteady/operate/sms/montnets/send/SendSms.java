package com.linksteady.operate.sms.montnets.send;

import com.linksteady.operate.sms.montnets.domain.Message;
import com.linksteady.operate.sms.montnets.domain.MultiMt;
import com.linksteady.operate.sms.montnets.domain.Remains;
import com.linksteady.operate.sms.montnets.util.CHttpPost;
import lombok.extern.slf4j.Slf4j;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-11
 */
@Slf4j
public class SendSms {

    /**
     * 用户名
     */
    private final String userid;
    /**
     * 密码
     */
    private final String pwd;
    /**
     * 密码是否加密
     */
    private final boolean isEncryptPwd;

    private SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");

    public SendSms(String userid, String pwd, boolean isEncryptPwd) {
        this.userid = userid;
        this.pwd = pwd;
        this.isEncryptPwd = isEncryptPwd;
    }

    /**
     * @description 单条发送
     *
     * 发送短信内容至单个手机号。每次只能将短信内容发送至一个手机号码，否则接口返回失败。
     */
    public int singleSend(Message message) {
        int result = -1;
        try {
            CHttpPost cHttpPost = new CHttpPost();
            message = getMessage(message);
            // 返回的平台流水编号等信息
            StringBuffer msgId = new StringBuffer();
            result = cHttpPost.singleSend(message, msgId);
            // result为0:成功;非0:失败
            if (result == 0) {
                log.info("单条发送提交成功！");
                log.info(msgId.toString());
            } else {
                log.error("单条发送提交失败,错误码：" + result);
            }
        } catch (Exception e) {
            log.error("单条短信发送失败，发生异常：", e);
        }
        return result;
    }

    /**
     * 封装message信息
     * @return
     */
    private Message getMessage(Message message) {
        CHttpPost cHttpPost = new CHttpPost();
        message.setUserid(userid.toUpperCase());
        if (isEncryptPwd) {
            String timestamp = sdf.format(Calendar.getInstance().getTime());
            message.setTimestamp(timestamp);
            String encryptPwd = cHttpPost.encryptPwd(message.getUserid(), pwd, message.getTimestamp());
            message.setPwd(encryptPwd);
        } else {
            message.setPwd(pwd);
        }
        // 设置扩展号
        message.setExno("");
        // 用户自定义流水编号
        message.setCustid("");
        // 自定义扩展数据
        message.setExdata("");
        //业务类型
        message.setSvrtype("");
        return message;
    }

    /**
     * @description 相同内容群发
     *
     * 发送相同的短信内容至多个不同的手机号。每次最多可将相同的短信内容发送至1000个手机号码，否则接口返回失败。
     */
    public int batchSend(Message message) {
        int result = -1;
        try {
            CHttpPost cHttpPost = new CHttpPost();
            // 参数类
            message = getMessage(message);
            // 返回的平台流水编号等信息
            StringBuffer msgId = new StringBuffer();
            result = cHttpPost.batchSend(message, msgId);
            // result为0:成功;非0:失败
            if (result == 0) {
                log.info("相同内容发送提交成功！");
                log.info(msgId.toString());
            } else {
                log.info("相同内容发送提交失败,错误码：" + result);
            }
        } catch (Exception e) {
            //异常处理
            log.error("相同内容群发发生异常：", e);
        }
        return result;
    }

    /**
     * @description 个性化群发
     *
     * 发送不同的短信内容至多个不同手机号。每次最多可发送100个手机号码的个性化短信内容至对应的手机号码，否则接口返回失败。
     */
    public int multiSend(List<MultiMt> multiMts, Message message) {
        int result = -1;
        try {
            CHttpPost cHttpPost = new CHttpPost();
            message = getMessage(message);
            // 返回的流水号
            StringBuffer msgId = new StringBuffer();
            // 返回值
            result = cHttpPost.multiSend(message.getUserid(), message.getPwd(), message.getTimestamp(), multiMts, msgId);
            // result为0:成功;非0:失败
            if (result == 0) {
                log.info("个性化群发提交成功！");
                log.info(msgId.toString());
            } else {
                log.error("个性化群发提交失败,错误码：" + result);
            }
        } catch (Exception e) {
            log.error("个性化群发发发生异常：", e);
        }
        return result;
    }

    /**
     * @description 查询余额
     */
    public int getBalance(Message message) {
        int result = -1;
        try {
            message = getMessage(message);
            // 实例化短信处理对象
            CHttpPost cHttpPost = new CHttpPost();
            result = cHttpPost.getBalance(message.getUserid(), message.getPwd(), message.getTimestamp());
            // 返回值大于等于0:查询成功;小于0:查询失败
            if (result >= 0) {
                log.info("查询余额成功，余额为：" + result + "条");
            } else {
                log.info("查询余额失败，错误码为：" + result);
            }
        } catch (Exception e) {
            log.error("查询余额发生异常：", e);
        }
        return result;
    }

    /**
     * @description 查询剩余金额或条数接口
     */
    public Remains getRemains(Message message) {
        Remains remains = new Remains();
        try {
            CHttpPost cHttpPost = new CHttpPost();
            message = getMessage(message);
            // 调用查询余额的方法查询余额
            remains = cHttpPost.getRemains(message.getUserid(), message.getPwd(), message.getTimestamp());
            if (remains == null) {
                log.info("查询失败。");
            }
            if (remains.getResult() != 0) {
                log.info("查询失败,错误码为：" + remains.getResult());
            }
            //计费类型为0，条数计费
            if (remains.getChargetype() == 0) {
                log.info("查询成功,剩余条数为：" + remains.getBalance() + "条");
            } else if (remains.getChargetype() == 1) {
                //计费类型为1，金额计费
                log.info("查询成功,剩余金额为：" + remains.getMoney() + "元");
            } else {
                log.info("未知的计费类型,计费类型:" + remains.getChargetype());
            }
        } catch (Exception e) {
            //异常处理
            log.error("查询剩余条数发生异常：", e);
        }
        return remains;
    }
}
