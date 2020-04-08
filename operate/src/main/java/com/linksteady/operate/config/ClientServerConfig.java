//package com.linksteady.operate.config;
//
//import com.linksteady.common.constant.CommonConstant;
//import com.linksteady.common.service.OpenApiService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
//
//import java.util.Map;
//
///**
// * @author hxcao
// * @date 2019-06-19
// */
//@Configuration
//@AutoConfigureAfter(RedisConfig.class)
//public class ClientServerConfig {
//
//    /**
//     * 服务端提供的服务URL
//     */
//    private String serviceDomain = CommonConstant.SYS_CODE;
//
//    private String serverServiceUrl = "/api/serverUrl";
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    /**
//     * 这里需要注意system 和 mdss 的redis BusinessDataBase配置信息一致，否则无法获取codeUrlMap
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public HttpInvokerProxyFactoryBean service() {
//        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
//        Map<String, Object> sysInfoMap = (Map<String, Object>)redisTemplate.opsForValue().get("sysInfoMap");
//        String url = ((SysInfo)sysInfoMap.get(serviceDomain)).getDomain() + "/" + serverServiceUrl;
//        httpInvokerProxyFactoryBean.setServiceUrl(url);
//        httpInvokerProxyFactoryBean.setServiceInterface(OpenApiService.class);
//        return httpInvokerProxyFactoryBean;
//    }
//}
