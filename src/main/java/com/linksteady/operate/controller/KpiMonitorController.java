package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.DateUtil;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.ReasonRelMatrix;
import com.linksteady.operate.domain.ReasonResult;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import com.linksteady.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营指标监控相关的controller
 */
@RestController
@RequestMapping("/kpiMonitor")
public class KpiMonitorController extends BaseController {


    /**
     * 获取留存率的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getRetainData")
    public ResponseBo list(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {
//        Map<String, Object> result = Maps.newHashMap();
        List<JSONArray> list = Lists.newArrayList();
        //按自然月
        if("month".equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            //留存率数据
            JSONArray retainData=new JSONArray();
            JSONArray ret=null;

            //循环月 获取其留存率
            for(int i=0;i<months.size();i++)
            {
                ret=new JSONArray();
                //月份
                ret.add(months.get(i));
                //当月新增用户数
                ret.add(getRandomData("newuser"));

                // 每个月的留存率
                JSONArray tmp = new JSONArray();
                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存率
                        tmp.add(getRandomData("retain"));
                    }else
                    {
                        tmp.add(-1);
                    }
                }
                ret.add(tmp);
                list.add(ret);
            }

           //获取汇总数据

        }
        //按间隔月
        else if("dmonth".equals(periodType))
        {



        }
        //按间隔周
        else if("week".equals(periodType))
        {

        }
        return  ResponseBo.okWithData("",list);
    }

    private Double getRandomData(String type)
    {
        double  result =0d;
        if("newuser".equals(type))
        {
            result= RandomUtil.getIntRandom(80,100);
        }else if("retain".equals(type))
        {
            result= RandomUtil.getIntRandom(0,25)/100.00;
        }

        return result;
    }

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> tmp = Maps.newHashMap();
        Map<String, Object> res = Maps.newHashMap();
        List<String> list = Lists.newArrayList();
        for(int i=0;i<10;i++) {
            list.add("a" + i);
            tmp.put("a" + i, i);
        }
        res.put("columns", list);
        res.put("data", tmp);
        return res;
    }
}

