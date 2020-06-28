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
import org.springframework.transaction.annotation.Transactional;

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
    public String getActivityTemplateContent(Long code,String scene) {
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
            content = content.replace(prodUrl, " "+configService.getValueByName("op.sms.url")+" ");
        }
        if (StringUtils.isNotEmpty(isProdName) && isProdName.equalsIgnoreCase("1")) {
            content = content.replace(prodName, configService.getValueByName("op.sms.prodname"));
        }
        if (StringUtils.isNotEmpty(isPrice) && isPrice.equalsIgnoreCase("1")) {
            content = content.replace(price, configService.getValueByName("op.sms.price"));
        }
        if (StringUtils.isNotEmpty(isProfit) && isProfit.equalsIgnoreCase("1")) {
            content = content.replace(profit, configService.getValueByName("op.sms.profit"));
        }

        //获取签名
        String signature=pushConfig.getSignature();
        String unsubscribe=pushConfig.getUnsubscribe();


        if("DISPLAY".equals(scene))
        {
            //(编辑短信时候不需要加上签名，则此处预览的时候补上)
            if(null!=pushConfig.getSignatureFlag()&&"N".equals(pushConfig.getSignatureFlag()))
            {
                content =signature+content;
            }

            //(编辑短信时候不需要加上签名，则此处预览的时候补上)
            if(null!=pushConfig.getUnsubscribeFlag()&&"Y".equals(pushConfig.getUnsubscribeFlag()))
            {
                content=content+unsubscribe;
            }
        }else if("SEND".equals(scene))
        {
            //需要系统自动加上签名
            if(null!=pushConfig.getSendSignatureFlag()&&"Y".equals(pushConfig.getSendSignatureFlag()))
            {
                content =signature+content;
            }

            //需要加上退订方式
            if(null!=pushConfig.getSendUnsubscribeFlag()&&"Y".equals(pushConfig.getSendUnsubscribeFlag()))
            {
                content=content+unsubscribe;
            }
        }

        return content;
    }

    @Override
    public List<ActivityTemplate> getSmsTemplateList(Long headId,String isPersonal,String scene,String stage,String type) {
        List<ActivityTemplate> templateTableData = activityTemplateMapper.getTemplateTableData(headId,isPersonal,scene,stage,type);
        templateTableData.stream().map(x->{
            String scene1 = x.getScene();
            if(StringUtils.isNotEmpty(scene1)) {
                scene1 = scene1.replace("DURING", "<span class=\"label label-primary\">通知</span>").replace("NOTIFY", "<span class=\"label label-success\">期间</span>");
            }
            x.setScene(scene1);
            return x;
        }).collect(Collectors.toList());
        return templateTableData;
    }

    @Override
    public void saveTemplate(ActivityTemplate activityTemplate,String currentUser) {
        activityTemplate.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));
        activityTemplate.setInsertDt(new Date());
        if("0".equals(activityTemplate.getIsProdUrl()) && "0".equals(activityTemplate.getIsProdName()) && "0".equals(activityTemplate.getIsPrice()) && "0".equalsIgnoreCase(activityTemplate.getIsProfit())) {
            activityTemplate.setIsPersonal("0");
        }else {
            activityTemplate.setIsPersonal("1");
        }
        activityTemplate.setInsertBy(currentUser);
        activityTemplate.setUpdateBy(currentUser);
        activityTemplate.setInsertDt(new Date());
        activityTemplate.setUpdateDt(new Date());
        activityTemplateMapper.saveTemplate(activityTemplate);
    }


    @Override
    public void deleteActivityTemplate(Long code, Long headId, String type, String stage) {
        activityTemplateMapper.deleteActivityTemplate(code);
        //对当前活动阶段的文案进行一次校验
        activityTemplateMapper.validUserGroup(headId,stage,type);
    }

    @Override
    public ActivityTemplate getTemplate(Long code) {
        return activityTemplateMapper.getTemplate(code);
    }

    @Override
    public String  updateSmsTemplate(ActivityTemplate activityTemplate,String flag,String currentUser) {
        //判断当前文案是否已经被引用 如果是，则进行新增操作 否则直接更新
        if(activityTemplateMapper.templateContentChanged(activityTemplate.getCode().longValue(),activityTemplate.getContent())>1)
        {
            return "文案内容未发生改变,无需保存！";
        }else
        {
            if("0".equals(activityTemplate.getIsProdUrl()) && "0".equals(activityTemplate.getIsProdName()) && "0".equals(activityTemplate.getIsPrice()) && "0".equalsIgnoreCase(activityTemplate.getIsProfit())) {
                activityTemplate.setIsPersonal("0");
            }else {
                activityTemplate.setIsPersonal("1");
            }

            if("Y".equals(flag))
            {
                //新写入一条记录 并返回主键
                activityTemplate.setCode(null);
                activityTemplate.setInsertBy(currentUser);
                activityTemplate.setUpdateBy(currentUser);
                activityTemplate.setInsertDt(new Date());
                activityTemplate.setUpdateDt(new Date());
                activityTemplateMapper.saveTemplate(activityTemplate);

                return "系统已为您生成文案编号为"+activityTemplate.getCode()+"的新文案，请在列表页查看！";
            }else
            {
                //更新操作
                activityTemplate.setUpdateBy(currentUser);
                activityTemplate.setUpdateDt(new Date());
                activityTemplateMapper.update(activityTemplate);
                return "保存文案信息成功！";
            }
        }
    }

    @Override
    public void setSmsCode(Long groupId, Long tmpCode, Long headId, String stage,String type) {
        activityTemplateMapper.setSmsCode(groupId, tmpCode, headId,stage, type);
        //设置完成后对当前活动stage、type上设置的文案情况进行一次校验
        activityTemplateMapper.validUserGroup(headId,stage,type);
    }

    @Override
    public boolean checkTemplateUsed(Long templateCode,Long headId,String stage,String type) {
        return activityTemplateMapper.checkTemplateUsed(templateCode,headId,stage,type) > 0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSmsSelected(String type, Long headId, String stage, Long groupId) {
        activityTemplateMapper.removeSmsSelected(type, headId, stage, groupId);
    }
}
