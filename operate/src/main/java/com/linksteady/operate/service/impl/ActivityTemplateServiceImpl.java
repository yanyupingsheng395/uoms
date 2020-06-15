package com.linksteady.operate.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.dao.ActivityTemplateMapper;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-11-13
 */
@Service
public class ActivityTemplateServiceImpl implements ActivityTemplateService {

    @Autowired
    private ActivityTemplateMapper activityTemplateMapper;

    @Autowired
    private PushConfig pushConfig;

    @Autowired
    private ConfigService configService;

    /**
     * 获取给定短信的预览文案
     * @param code
     * @return
     */
    @Override
    public String getActivityTemplateContent(String code,String scene) {
        ActivityTemplate activityTemplate = activityTemplateMapper.getTemplate(code);
        String isProdUrl = activityTemplate.getIsProdUrl();
        String isProdName = activityTemplate.getIsProdName();
        String isPrice = activityTemplate.getIsPrice();
        String isProfit = activityTemplate.getIsProfit();
        String prodUrl = "${商品详情页短链}";
        String prodName = "${商品名称}";
        String price = "${商品最低单价}";
        String profit = "${商品利益点}";
        String content = activityTemplate.getContent();
        if (StringUtils.isNotEmpty(isProdUrl) && isProdUrl.equalsIgnoreCase("1")) {
            content = content.replace(prodUrl, " "+configService.getValueByName("op.activity.sms.prodUrl")+" ");
        }
        if (StringUtils.isNotEmpty(isProdName) && isProdName.equalsIgnoreCase("1")) {
            content = content.replace(prodName, configService.getValueByName("op.activity.sms.prodName"));
        }
        if (StringUtils.isNotEmpty(isPrice) && isPrice.equalsIgnoreCase("1")) {
            content = content.replace(price, configService.getValueByName("op.activity.sms.price"));
        }
        if (StringUtils.isNotEmpty(isProfit) && isProfit.equalsIgnoreCase("1")) {
            content = content.replace(profit, configService.getValueByName("op.activity.sms.profit"));
        }

        //获取签名
        String signature=pushConfig.getSignature();
        String signatureFlag=pushConfig.getSignatureFlag();

        String unsubscribe=pushConfig.getUnsubscribe();
        String unsubscribeFlag=pushConfig.getUnsubscribeFlag();

        if("DISPLAY".equals(scene))
        {
            content =signature+content;
            content=content+unsubscribe;
        }else if("SEND".equals(scene))
        {
            //需要系统自动加上签名
            if(null!=signatureFlag&&"Y".equals(signatureFlag))
            {
                content =signature+content;
            }

            //需要加上退订方式
            if(null!=unsubscribeFlag&&"Y".equals(unsubscribeFlag))
            {
                content=content+unsubscribe;
            }
        }

        return content;
    }

    @Override
    public List<ActivityTemplate> getSmsTemplateList(ActivityTemplate activityTemplate) {
        List<ActivityTemplate> templateTableData = activityTemplateMapper.getTemplateTableData(activityTemplate);
        templateTableData.stream().map(x->{
            String relation = x.getRelation();
            if(StringUtils.isNotEmpty(relation)) {
                relation = relation.replace("GROWTH", "成长").replace("LATENT", "潜在");
            }
            String scene = x.getScene();
            if(StringUtils.isNotEmpty(scene)) {
                scene = scene.replace("DURING", "活动期间").replace("NOTIFY", "活动通知");
            }
            x.setRelation(relation);
            x.setScene(scene);
            return x;
        }).collect(Collectors.toList());
        return templateTableData;
    }

    @Override
    public void saveTemplate(ActivityTemplate activityTemplate) {
        activityTemplate.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));
        activityTemplate.setInsertDt(new Date());
        if("0".equals(activityTemplate.getIsProdUrl()) && "0".equals(activityTemplate.getIsProdName()) && "0".equals(activityTemplate.getIsPrice()) && "0".equalsIgnoreCase(activityTemplate.getIsProfit())) {
            activityTemplate.setIsPersonal("0");
        }else {
            activityTemplate.setIsPersonal("1");
        }
        activityTemplateMapper.saveTemplate(activityTemplate);
    }

    @Override
    public int refrenceCount(String code) {
        return 0;
    }

    @Override
    public void deleteTemplate(String code) {
        activityTemplateMapper.deleteTemplate(code);
    }

    @Override
    public ActivityTemplate getTemplate(String code) {
        return activityTemplateMapper.getTemplate(code);
    }

    @Override
    public void update(ActivityTemplate activityTemplate) {
        if("0".equals(activityTemplate.getIsProdUrl()) && "0".equals(activityTemplate.getIsProdName()) && "0".equals(activityTemplate.getIsPrice()) && "0".equalsIgnoreCase(activityTemplate.getIsProfit())) {
            activityTemplate.setIsPersonal("0");
        }else {
            activityTemplate.setIsPersonal("1");
        }
        activityTemplateMapper.update(activityTemplate);
    }
}
