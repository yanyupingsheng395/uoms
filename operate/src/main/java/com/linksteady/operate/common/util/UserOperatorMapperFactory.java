package com.linksteady.operate.common.util;

import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author cao
 */
@Component
public class UserOperatorMapperFactory implements ApplicationContextAware {

    private static Map<String, UserOperaterMapper> mapperTemplateMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, UserOperaterMapper> map = applicationContext.getBeansOfType(UserOperaterMapper.class);
        mapperTemplateMap = Maps.newConcurrentMap();
        map.forEach((k, v) -> {
            String key = "";
            switch (k) {
                case "gmvMapper":
                    key = UomsConstants.OP_DATA_GMV;
                    break;
                case "userCntMapper":
                    key = UomsConstants.OP_DATA_USER_CNT;
                    break;
                case "userPriceMapper":
                    key = UomsConstants.OP_DATA_USER_PRICE;
                    break;
                case "orderCntMapper":
                    key = UomsConstants.OP_DATA_ORDER_CNT;
                    break;
                case "orderPriceMapper":
                    key = UomsConstants.OP_DATA_ORDER_PRICE;
                    break;
                case "userJoinRateMapper":
                    key = UomsConstants.OP_DATA_ORDER_JOINRATE;
                    break;
                case "spriceMapper":
                    key = UomsConstants.OP_DATA_SPRICE;
                    break;
                default:
                    break;
            }
            mapperTemplateMap.put(key, v);
        });
    }

    public UserOperaterMapper getMapperTemplate(String code) {
        return mapperTemplateMap.get(code);
    }
}
