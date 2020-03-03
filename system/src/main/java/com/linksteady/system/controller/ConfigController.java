package com.linksteady.system.controller;

import com.linksteady.common.annotation.Log;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.Tconfig;
import com.linksteady.system.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hxcao
 * @date 2020/3/3
 */
@Controller
@Slf4j
public class ConfigController extends BaseController{

    @Autowired
    private ConfigService configService;


    @Log("获取系统数据")
    @RequestMapping("config")
    public String list() {
        return "system/config/config";
    }

    /**
     * 获取列表数据
     * @return
     */
    @RequestMapping("config/getList")
    @ResponseBody
    public ResponseBo getList(String sort, String order, String typeCode1, String name) {
        String asc = "asc";
        String sortCol1 = "typeCode2";
        String sortCol2 = "orderNum";
        List<Tconfig> tconfigs = configService.selectConfigList();
        if(StringUtils.isNotEmpty(typeCode1)) {
            tconfigs = tconfigs.stream().filter(x->typeCode1.equalsIgnoreCase(x.getTypeCode1())).collect(Collectors.toList());
        }
        if(StringUtils.isNotEmpty(name)) {
            tconfigs = tconfigs.stream().filter(x->name.equalsIgnoreCase(x.getName())).collect(Collectors.toList());
        }
        if(StringUtils.isNotEmpty(sort) && StringUtils.isNotEmpty(order)) {
            if(asc.equalsIgnoreCase(order)) {
                if(sortCol1.equalsIgnoreCase(sort)) {
                    tconfigs = tconfigs.stream().sorted(Comparator.comparing(Tconfig::getTypeCode2, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
                }
                if(sortCol2.equalsIgnoreCase(sort)) {
                    tconfigs = tconfigs.stream().sorted(Comparator.comparing(Tconfig::getOrderNum, Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());
                }
            }else {
                if(sortCol1.equalsIgnoreCase(sort)) {
                    tconfigs = tconfigs.stream().sorted(Comparator.comparing(Tconfig::getTypeCode2, Comparator.nullsLast(String::compareTo)).reversed()).collect(Collectors.toList());
                }
                if(sortCol2.equalsIgnoreCase(sort)) {
                    tconfigs = tconfigs.stream().sorted(Comparator.comparing(Tconfig::getOrderNum, Comparator.nullsLast(Integer::compareTo)).reversed()).collect(Collectors.toList());
                }
            }
        }else {
            tconfigs = tconfigs.stream().sorted(Comparator.comparing(Tconfig::getTypeCode2, Comparator.nullsLast(String::compareTo))
                    .thenComparing(Tconfig::getOrderNum, Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
        }
        return ResponseBo.okOverPaging(null, 0, tconfigs);
    }

    /**
     * 获取列表数据
     * @return
     */
    @RequestMapping("config/reloadData")
    @ResponseBody
    public ResponseBo reloadData() {
        configService.loadConfigToRedis();
        return ResponseBo.ok();
    }
}
