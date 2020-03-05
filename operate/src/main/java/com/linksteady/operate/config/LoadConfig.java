package com.linksteady.operate.config;

import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.domain.enums.PushPropertiesEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 判断t_config配置数据是否能从redis中加载到，如果加载不到，则报错
 */
@Component
@Slf4j
public class LoadConfig implements CommandLineRunner {

    @Autowired
    ConfigService configService;

    @Override
    public void run(String... args) throws Exception {
        //判断redis中TCONFIG这个hashkey是否存在
        if(!configService.configExists())
        {
            throw new LinkSteadyException("无法正确加载到配置，请检查");
        }
        //开启监控线程
        startThread();
    }

    /**
     * 读取推送的配置信息
     *
     */
    @Bean(name="pushProperties")
    public PushProperties pushProperties() throws Exception
    {
        //初始化配置对象
        PushProperties pushProperties=new PushProperties();
        try {
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.repeatPushDays);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.pushFlag);
            //推送方式
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.pushType);
            //默认推送方法
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.pushMethod);

            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.smsLengthLimit);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.productUrl);

            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.isTestEnv);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.demoShortUrl);

            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.shortUrlLen);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.prodNameLen);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.couponSendType);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.couponNameLen);
            setPropertiesByKeyCode(pushProperties,PushPropertiesEnum.priceLen);
        } catch (Exception e) {
           log.error("初始化pushProperties对象错误,错误原因为{}",e);
            throw e;
        }

        return pushProperties;
    }

    private void  setPropertiesByKeyCode(PushProperties pushProperties, PushPropertiesEnum pushPropertiesEnum) throws Exception
    {
        Object targetValue=null;
        //获取keycode获取对应的值
       String value=configService.getValueByName(pushPropertiesEnum.getKeyCode());

       if(StringUtils.isEmpty(value))
       {
           throw new LinkSteadyException("初始化PushProperties,"+pushPropertiesEnum.getFieldName()+"对应的code+:"+pushPropertiesEnum.getKeyCode()+"未配置值！");
       }

        Field field = null;

        //根据属性名调用对象
        PropertyDescriptor descriptor = null;
        try {
            field=pushProperties.getClass().getDeclaredField(pushPropertiesEnum.getFieldName());

            if(null==field)
            {
                throw new LinkSteadyException("初始化PushProperties,列不存在");
            }

            //整形
            if (field.getGenericType().toString().equals("int")||field.getGenericType().toString().equals("class java.lang.Integer")){
                  targetValue=Integer.parseInt(value);
            }else if(field.getGenericType().toString().equals("class java.lang.String"))
            {
                //什么也不做
                targetValue=value;
            }else
            {
                throw new LinkSteadyException("初始化PushProperties,暂不支持的数据类型");
            }

            descriptor = new PropertyDescriptor(pushPropertiesEnum.getFieldName(), pushProperties.getClass());
            Method writeMethod = descriptor.getWriteMethod();

            //调用set方法
            writeMethod.invoke(pushProperties,targetValue);
        } catch (Exception e) {
               throw e;
        }
    }

    /**
     * 开启监控线程
     */
    private void startThread()
    {
        //TODO 待monitor完成后在此处开启线程
    }


}
