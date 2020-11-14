package com.linksteady.qywx.service.impl;

import com.linksteady.common.domain.QywxMessage;
import com.linksteady.qywx.dao.WelcomeMessageMapper;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.service.ParamService;
import com.linksteady.qywx.service.MediaService;
import com.linksteady.qywx.service.WelcomeMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WelcomeMessageServiceImpl implements WelcomeMessageService {

    @Autowired
    WelcomeMessageMapper welcomeMessageMapper;

    @Autowired
    ParamService paramService;

    @Autowired
    MediaService mediaService;

    /**
     * 获取有效的企业微信欢迎语消息
     * @return
     */
    @Override
    public QywxMessage getValidWelcomeMessage() {
        //获取一个有效的欢迎语方案
        QywxWelcome qywxWelcome=welcomeMessageMapper.getValidWelcome();
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
                qywxMessage.setMpAppid(paramService.getMpAppId());
                qywxMessage.setMpPicMediaId(getWelcomeMpMediaId(qywxWelcome));
                qywxMessage.setMpTitle(qywxWelcome.getMiniprogramTitle());
                qywxMessage.setMpPage(qywxWelcome.getMiniprogramPage());
            }
            return qywxMessage;
        }
    }

    private String getWelcomeMpMediaId( QywxWelcome qywxWelcome)
    {
        String policyType=qywxWelcome.getPolicyType();
        if("PRODUCT".equals(policyType))
        {
           return mediaService.getMpMediaId("PRODUCT",Long.parseLong(qywxWelcome.getQywxProductId()));
        }else if("COUPON".equals(policyType))
        {
            return mediaService.getMpMediaId("COUPON",Long.parseLong(qywxWelcome.getQywxCouponId()));
        }else
        {
            return mediaService.getMpMediaId("PRODUCT",-1L);
        }
    }

}
