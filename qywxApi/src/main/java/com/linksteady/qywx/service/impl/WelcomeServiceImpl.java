package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.WelcomeMapper;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.MediaService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.WelcomeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class WelcomeServiceImpl implements WelcomeService {

    @Autowired
    WelcomeMapper welcomeMapper;

    @Autowired
    MediaService mediaService;

    @Autowired
    QywxService qywxService;

    /**
     * 获取有效的企业微信欢迎语消息
     * @return
     */
    @Override
    public QywxMessage getValidWelcomeMessage() throws Exception{
        //获取一个有效的欢迎语方案
        QywxWelcome qywxWelcome=welcomeMapper.getValidWelcome();
        if(qywxWelcome==null)
        {
            return null;
        }else
        {
            QywxMessage qywxMessage=new QywxMessage();
            //文本
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getContent()))
            {
                qywxMessage.setText(qywxWelcome.getContent());
            }
            //图片
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getPicUrl()))
            {
                qywxMessage.setImgPicUrl(qywxWelcome.getPicUrl());
            }
            //链接
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getLinkTitle()))
            {
                qywxMessage.setLinkTitle(qywxWelcome.getLinkTitle());
                qywxMessage.setLinkPicUrl(qywxWelcome.getLinkPicurl());
                qywxMessage.setLinkDesc(qywxWelcome.getLinkDesc());
                qywxMessage.setLinkUrl(qywxWelcome.getLinkUrl());
            }
            //小程序
            if(!org.springframework.util.StringUtils.isEmpty(qywxWelcome.getMiniprogramTitle()))
            {
                qywxMessage.setMpAppid(qywxService.getMpAppId());
                //获取小程序卡片的mediaId
                qywxMessage.setMpPicMediaId(getWelcomeMpMediaId(qywxWelcome));
                qywxMessage.setMpTitle(qywxWelcome.getMiniprogramTitle());
                qywxMessage.setMpPage(qywxWelcome.getMiniprogramPage());
            }
            return qywxMessage;
        }
    }

    @Override
    public void sendWelcomeMessage(String welcomeCode, String exernalUserId) throws Exception {
        QywxMessage qywxMessage= null;
        try {
            qywxMessage = getValidWelcomeMessage();
            //完成欢迎语的推送
            sendMessage(welcomeCode,qywxMessage);
        } catch (Exception e) {
            log.error("发送欢迎语失败，原因为{}",e);
            //todo 进行错误原因上报
        }
    }

    private String getWelcomeMpMediaId( QywxWelcome qywxWelcome) throws Exception
    {
        String policyType=qywxWelcome.getPolicyType();
        if("PRODUCT".equals(policyType))
        {
           return mediaService.getMpMediaId("PRODUCT",qywxWelcome.getQywxProductId());
        }else if("COUPON".equals(policyType))
        {
            return mediaService.getMpMediaId("COUPON",qywxWelcome.getQywxCouponId());
        }else
        {
            return mediaService.getMpMediaId("PRODUCT",-1L);
        }
    }

    private void sendMessage(String welcomeCode,QywxMessage message) throws Exception
    {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.SEND_WELCOME_MESSAGE));
        try {
            requestUrl.append(qywxService.getAccessToken());
        } catch (WxErrorException e) {
            throw new Exception("发送欢迎语失败，原因为获取accessToken失败");
        }
        log.info("发送欢迎语请求url:{}", requestUrl.toString());

        JSONObject param=new JSONObject();
        param.put("welcome_code",welcomeCode);

        JSONObject tempContent=null;
        //文本
        if(StringUtils.isNotEmpty(message.getText()))
        {
            tempContent=new JSONObject();
            tempContent.put("content",message.getText());
            param.put("text",tempContent);
        }

        //图片
        if(StringUtils.isNotEmpty(message.getImgMediaId())||StringUtils.isNotEmpty(message.getImgPicUrl()))
        {
            tempContent=new JSONObject();
            if(StringUtils.isNotEmpty(message.getImgMediaId()))
            {
                tempContent.put("media_id",message.getImgMediaId());
            }
            if(StringUtils.isNotEmpty(message.getImgPicUrl()))
            {
                tempContent.put("pic_url",message.getImgPicUrl());
            }
            param.put("image",tempContent);
        }
        //链接
        if(StringUtils.isNotEmpty(message.getLinkTitle()))
        {
            tempContent=new JSONObject();
            tempContent.put("title",message.getLinkTitle());

            if(StringUtils.isNotEmpty(message.getLinkPicUrl()))
            {
                tempContent.put("picurl",message.getLinkPicUrl());
            }

            if(StringUtils.isNotEmpty(message.getLinkDesc()))
            {
                tempContent.put("desc",message.getLinkDesc());
            }

            if(StringUtils.isNotEmpty(message.getLinkUrl()))
            {
                tempContent.put("url",message.getLinkUrl());
            }

            param.put("link",tempContent);
        }

        //小程序
        if(StringUtils.isNotEmpty(message.getMpTitle()))
        {
            tempContent=new JSONObject();
            tempContent.put("title",message.getMpTitle());
            tempContent.put("pic_media_id",message.getMpPicMediaId());
            tempContent.put("appid",message.getMpAppid());
            tempContent.put("page",message.getMpPage());

            param.put("miniprogram",tempContent);
        }
        String result = OkHttpUtil.postRequest(requestUrl.toString(),param.toJSONString());
        log.info("发送欢迎语接口返回的结果为{}", result);

        //4.获取结果
        JSONObject jsonObject = JSON.parseObject(result);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            if(error.getErrorCode()==41051)
            {
                throw new Exception("发送欢迎语接口失败,已有其他应用发送了带有welcome_code的回调事件");
            }else
            {
                throw new Exception("发送欢迎语接口失败,原因为:"+error.getErrorMsg());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveData(QywxWelcome qywxWelcome) {
        String policyType = qywxWelcome.getPolicyType();
        if (StringUtils.isNotEmpty(policyType) && policyType.equalsIgnoreCase("A")) {
            qywxWelcome.setPolicyType(qywxWelcome.getPolicyTypeTmp());
        }
        welcomeMapper.saveData(qywxWelcome);
        System.out.println(qywxWelcome);
        return qywxWelcome.getId();
    }

    @Override
    public int getDataCount() {
        return welcomeMapper.getDataCount();
    }

    @Override
    public List<QywxWelcome> getDataList(Integer limit, Integer offset) {
        return welcomeMapper.getDataList(limit, offset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        welcomeMapper.deleteById(id);
    }

    @Override
    public QywxWelcome getDataById(String id) {
        List<QywxWelcome> welcomeList = welcomeMapper.getDataById(id);
        return welcomeList.size() > 0 ? welcomeList.get(0) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateData(QywxWelcome qywxWelcome) {
        String policyType = qywxWelcome.getPolicyType();
        if (StringUtils.isNotEmpty(policyType) && policyType.equalsIgnoreCase("A")) {
            qywxWelcome.setPolicyType(qywxWelcome.getPolicyTypeTmp());
        }
        welcomeMapper.updateData(qywxWelcome);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String id, String status) {
        if(StringUtils.isNotEmpty(status)) {
            if(status.equalsIgnoreCase("start")) {
                welcomeMapper.updateStartStatus(id, status);
            } else if(status.equalsIgnoreCase("stop")) {
                welcomeMapper.updateStopStatus(id, status);
            }
        }
    }

}
