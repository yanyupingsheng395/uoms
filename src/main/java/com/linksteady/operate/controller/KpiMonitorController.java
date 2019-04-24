package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.ArithUtil;
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

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营指标监控相关的controller
 * @author  linkSteady
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

        JSONObject result=new JSONObject();

        //留存用户数(每行一条数据)
        List<Map<String,String>> retainData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存率的临时变量
        Map<String,String> ret=null;

        //按自然月
        if("month".equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取其留存率
            for(int i=0;i<months.size();i++)
            {
                ret=Maps.newHashMap();
                //月份
                ret.put("month",months.get(i));
                //当月新增用户数
                ret.put("newuser",String.valueOf(getRandomData("newuser")));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存率
                        ret.put(months.get(j),String.valueOf(getRandomData("retain")));
                    }else
                    {
                        ret.put(months.get(j),"-1");
                    }
                }
                retainData.add(ret);
            }

           //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("month".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按间隔月
        else if("dmonth".equals(periodType))
        {
            columns.add("month");
            columns.add("newuser");

            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"月");
            }

            columns.addAll(subCols);

            for(int i=0;i<months.size();i++)
            {
                ret=Maps.newHashMap();

                ret.put("month",months.get(i));
                ret.put("newuser",String.valueOf(getRandomData("newuser")));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                           ret.put(subCols.get(j),"-1");
                    }else
                    {
                           ret.put(subCols.get(j),String.valueOf(getRandomData("retain")));
                    }
                }
                retainData.add(ret);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("month".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total=Double.parseDouble(mp.get(col));
                    }

                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按间隔周
        else if("week".equals(periodType))
        {

        }

        result.put("columns",columns);
        result.put("data",retainData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }

    private Double getRandomData(String type)
    {
        double  result =0d;
        if("newuser".equals(type))
        {
            result= RandomUtil.getIntRandom(80,100);
        }else if("retain".equals(type))
        {
            result= RandomUtil.getIntRandom(8000,9999)/100.00;
        }

        return result;
    }

    /**
     * 判断一个月份ID是否大于当前月，如果是 返回true 否则返回false
     * @param month
     * @return
     */
    private boolean greaterThanNow(String month)
    {
        String nowMonth= LocalDate.now().getYear()+String.format("%02d",LocalDate.now().getMonthValue());
        return Integer.parseInt(month)>Integer.parseInt(nowMonth);
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

