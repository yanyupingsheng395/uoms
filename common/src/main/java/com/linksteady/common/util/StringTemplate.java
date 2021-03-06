package com.linksteady.common.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

public class StringTemplate {

    private String template;

    private  Map<String,String> param= Maps.newHashMap();

    public StringTemplate(String template)
    {
         this.template=template;
    }

    public synchronized StringTemplate add(String name, String value) {
        if ( name==null ) {
            throw new NullPointerException("属性名称不能为空");
        }
        if ( name.indexOf('.')>=0 ) {
            throw new IllegalArgumentException("属性名称中不允许含有特殊符号");
        }
        param.put(name,value);
        return this;
    }

    public String render() {
        String renderTemplate=this.template;
        for (String key : param.keySet()) {
            renderTemplate=StringUtils.replace(renderTemplate,key,param.get(key));
        }
        return renderTemplate;
    }

}


