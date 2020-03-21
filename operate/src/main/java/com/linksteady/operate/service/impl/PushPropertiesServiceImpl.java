package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.domain.Tconfig;
import com.linksteady.common.dao.ConfigMapper;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.domain.enums.PushPropertiesEnum;
import com.linksteady.operate.domain.enums.PushSignalEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.PushPropertiesService;
import com.linksteady.operate.service.RedisMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PushPropertiesServiceImpl implements PushPropertiesService {

    @Autowired
    private RedisMessageService redisMessageService;

    @Autowired
    private ConfigMapper configMapper;


    private static final String CONFIG_KEY_NAME="TCONFIG";

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void sendPushSignal(PushProperties prop,PushSignalEnum signal,String currentUser) throws Exception{
        HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
        //启动
        if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_START.getSignalCode()))
        {
            //更新数据库
            configMapper.updateConfig(PushPropertiesEnum.pushFlag.getKeyCode(),"Y");
            //重新加载到redis并刷新pushProperties
            initProperties(prop,currentUser);
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);

        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_STOP.getSignalCode()))
        {
            //更新数据库
            configMapper.updateConfig(PushPropertiesEnum.pushFlag.getKeyCode(),"N");
            //重新加载到redis并刷新pushProperties
            initProperties(prop,currentUser);
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);
        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_PRINT.getSignalCode()))
        {
            //打印
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);
        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_REFRESH.getSignalCode()))
        {
            //重新加载到redis
            initProperties(prop,currentUser);
            //通知推送端也重新加载配置
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);
        }else
        {
            //什么也不做
        }
    }

    @Override
    public  void initProperties(PushProperties prop,String currentUser) throws Exception
    {
        log.info("{} 加载了推送配置",currentUser);

        //数据库加载到redis
        List<Tconfig> tconfigList=configMapper.selectConfigList();

        Map<String,String> configMap=tconfigList.stream().collect(Collectors.toMap(Tconfig::getName,Tconfig::getValue));
        HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();

        //删除
        redisTemplate.delete(CONFIG_KEY_NAME);
        //存入redis
        for(Tconfig tconfig:tconfigList)
        {
            hashOperations.put(CONFIG_KEY_NAME,tconfig.getName(),tconfig);
        }

        for(PushPropertiesEnum pushPropertiesEnum:PushPropertiesEnum.values())
        {
            setPropertiesByKeyCode(prop,pushPropertiesEnum,configMap);
        }
    }

    @Override
    public List<Tconfig> selectPushConfigList() {
        HashOperations<String, String, Tconfig> hashOperations= redisTemplate.opsForHash();
        List<Tconfig> tconfigList=hashOperations.values(CONFIG_KEY_NAME);

        return tconfigList.stream().filter(p->"push".equals(p.getTypeCode2())).collect(Collectors.toList());
    }

    private void  setPropertiesByKeyCode(PushProperties pushProperties, PushPropertiesEnum pushPropertiesEnum, Map<String,String> configMap) throws Exception
    {
        Object targetValue=null;
        //获取keycode获取对应的值
        String value=configMap.get(pushPropertiesEnum.getKeyCode());

        if(StringUtils.isEmpty(value))
        {
            throw new LinkSteadyException("初始化PushProperties,"+pushPropertiesEnum.getFieldName()+"对应的code+:"+pushPropertiesEnum.getKeyCode()+"未配置值！");
        }

        Field field = null;

        //根据属性名调用对象
        PropertyDescriptor descriptor = null;
        try {
            field=pushProperties.getClass().getDeclaredField(pushPropertiesEnum.getFieldName());

            //整形
            if (field.getGenericType().toString().equals("int")||field.getGenericType().toString().equals("class java.lang.Integer")){
                targetValue=Integer.parseInt(value);
            }else if(field.getGenericType().toString().equals("class java.lang.String"))
            {
                //什么也不做
                targetValue=value;
            }
            else if (field.getGenericType().toString().equals("long")||field.getGenericType().toString().equals("class java.lang.Long")){
                    targetValue=Long.parseLong(value);
            }
        else
            {
                throw new LinkSteadyException("初始化PushProperties,暂不支持的数据类型");
            }

            field.setAccessible(true);
            descriptor = new PropertyDescriptor(pushPropertiesEnum.getFieldName(), pushProperties.getClass());
            Method writeMethod = descriptor.getWriteMethod();

            //调用set方法
            writeMethod.invoke(pushProperties,targetValue);
        } catch (Exception e) {
            throw e;
        }
    }


}
