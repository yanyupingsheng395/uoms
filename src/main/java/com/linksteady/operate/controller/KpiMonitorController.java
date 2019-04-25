package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DateUtil;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.domain.WeekInfo;
import com.linksteady.operate.service.KpiMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 运营指标监控相关的controller
 * @author  linkSteady
 */
@RestController
@RequestMapping("/kpiMonitor")
public class KpiMonitorController extends BaseController {

    @Autowired
    KpiMonitorService kpiMonitorService;

    /**
     * 自然月
     */
    public static final String PERIOD_TYPE_MONTH="month";
    /**
     * 间隔月
     */
    public static final String PERIOD_TYPE_INTERVAL_MONTH="dmonth";
    /**
     * 自然周
     */
    public static final String PERIOD_TYPE_WEEK="week";
    /**
     * 间隔周
     */
    public static final String PERIOD_TYPE_INTERVAL_WEEK="dweek";

    /**
     * 获取留存率的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getRetainData")
    public ResponseBo getRetainData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> retainData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存率的临时变量
        Map<String,String> ret=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
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
                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

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
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

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
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                ret=Maps.newHashMap();

                ret.put("week",weeks.get(i).getWeekOfYareName());
                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
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
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                ret=Maps.newHashMap();
                //月份
                ret.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存率
                        ret.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("retain")));
                    }else
                    {
                        ret.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                retainData.add(ret);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",retainData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }


    /**
     * 获取留存用户数的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getRetainCntData")
    public ResponseBo getRetainCntData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> retainCntData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String> retCnt=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                retCnt=Maps.newHashMap();
                //月份
                retCnt.put("month",months.get(i));
                //当月新增用户数
                retCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        retCnt.put(months.get(j),String.valueOf(getRandomData("retainCnt").intValue()));
                    }else
                    {
                        retCnt.put(months.get(j),"-1");
                    }
                }
                retainCntData.add(retCnt);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("month".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按间隔月
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                retCnt=Maps.newHashMap();

                retCnt.put("month",months.get(i));
                retCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        retCnt.put(subCols.get(j),"-1");
                    }else
                    {
                        retCnt.put(subCols.get(j),String.valueOf(getRandomData("retainCnt").intValue()));
                    }
                }
                retainCntData.add(retCnt);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("month".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }

                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按间隔周
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                retCnt=Maps.newHashMap();

                retCnt.put("week",weeks.get(i).getWeekOfYareName());
                retCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        retCnt.put(subCols.get(j),"-1");
                    }else
                    {
                        retCnt.put(subCols.get(j),String.valueOf(getRandomData("retainCnt").intValue()));
                    }
                }
                retainCntData.add(retCnt);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                retCnt=Maps.newHashMap();
                //月份
                retCnt.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                retCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        retCnt.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("retainCnt").intValue()));
                    }else
                    {
                        retCnt.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                retainCntData.add(retCnt);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }

        result.put("columns",columns);
        result.put("data",retainCntData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }

    /**
     * 获取流失率的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getLossData")
    public ResponseBo getLossData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //流失率数据(每行一条数据)
        List<Map<String,String>> lossData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放流失率的临时变量
        Map<String,String> loss=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取其流失率
            for(int i=0;i<months.size();i++)
            {
                loss=Maps.newHashMap();
                //月份
                loss.put("month",months.get(i));
                //当月新增用户数
                loss.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取流失率
                        loss.put(months.get(j),String.valueOf(getRandomData("loss")));
                    }else
                    {
                        loss.put(months.get(j),"-1");
                    }
                }
                lossData.add(loss);
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

                for(Map<String,String> mp:lossData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                loss=Maps.newHashMap();

                loss.put("month",months.get(i));
               loss.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取流失用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        loss.put(subCols.get(j),"-1");
                    }else
                    {
                        loss.put(subCols.get(j),String.valueOf(getRandomData("loss")));
                    }
                }
                lossData.add(loss);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("month".equals(col)||"loss".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:lossData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                loss=Maps.newHashMap();

                loss.put("week",weeks.get(i).getWeekOfYareName());
                loss.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        loss.put(subCols.get(j),"-1");
                    }else
                    {
                        loss.put(subCols.get(j),String.valueOf(getRandomData("loss")));
                    }
                }
                lossData.add(loss);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:lossData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                loss=Maps.newHashMap();
                //月份
                loss.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                loss.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存率
                        loss.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("retain")));
                    }else
                    {
                        loss.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                lossData.add(loss);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:lossData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",lossData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }


    /**
     * 获取流失用户数的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getLossCntData")
    public ResponseBo getLossCntData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> lossCntData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String> lossCnt=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                lossCnt=Maps.newHashMap();
                //月份
                lossCnt.put("month",months.get(i));
                //当月新增用户数
                lossCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        lossCnt.put(months.get(j),String.valueOf(getRandomData("lossCnt").intValue()));
                    }else
                    {
                        lossCnt.put(months.get(j),"-1");
                    }
                }
                lossCntData.add(lossCnt);
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

                for(Map<String,String> mp:lossCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按间隔月
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                lossCnt=Maps.newHashMap();

                lossCnt.put("month",months.get(i));
                lossCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        lossCnt.put(subCols.get(j),"-1");
                    }else
                    {
                        lossCnt.put(subCols.get(j),String.valueOf(getRandomData("lossCnt").intValue()));
                    }
                }
                lossCntData.add(lossCnt);
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

                for(Map<String,String> mp:lossCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }

                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按间隔周
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                lossCnt=Maps.newHashMap();

                lossCnt.put("week",weeks.get(i).getWeekOfYareName());
                lossCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        lossCnt.put(subCols.get(j),"-1");
                    }else
                    {
                        lossCnt.put(subCols.get(j),String.valueOf(getRandomData("lossCnt").intValue()));
                    }
                }
                lossCntData.add(lossCnt);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:lossCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                lossCnt=Maps.newHashMap();
                //月份
                lossCnt.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                lossCnt.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        lossCnt.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("lossCnt").intValue()));
                    }else
                    {
                        lossCnt.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                lossCntData.add(lossCnt);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:lossCntData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
                    }
                }

                if(count>0)
                {
                    totalData.put(col, String.valueOf(total.intValue()));
                }else
                {
                    totalData.put(col,"");
                }
            }
        }

        result.put("columns",columns);
        result.put("data",lossCntData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }


    /**
     * 获取客单价的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getUpriceData")
    public ResponseBo getUpriceData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> upriceData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String> uprice=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                uprice=Maps.newHashMap();
                //月份
                uprice.put("month",months.get(i));
                //当月新增用户数
                uprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取客单价
                        uprice.put(months.get(j),String.valueOf(getRandomData("uprice").intValue()));
                    }else
                    {
                        uprice.put(months.get(j),"-1");
                    }
                }
                upriceData.add(uprice);
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

                for(Map<String,String> mp:upriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                uprice=Maps.newHashMap();

                uprice.put("month",months.get(i));
                uprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        uprice.put(subCols.get(j),"-1");
                    }else
                    {
                        uprice.put(subCols.get(j),String.valueOf(getRandomData("uprice").intValue()));
                    }
                }
                upriceData.add(uprice);
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

                for(Map<String,String> mp:upriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                uprice=Maps.newHashMap();

                uprice.put("week",weeks.get(i).getWeekOfYareName());
                uprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        uprice.put(subCols.get(j),"-1");
                    }else
                    {
                        uprice.put(subCols.get(j),String.valueOf(getRandomData("uprice").intValue()));
                    }
                }
                upriceData.add(uprice);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:upriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                uprice=Maps.newHashMap();
                //月份
                uprice.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                uprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取客单价
                        uprice.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("uprice").intValue()));
                    }else
                    {
                        uprice.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                upriceData.add(uprice);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:upriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",upriceData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }

    /**
     * 获取件单价的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getSpriceData")
    public ResponseBo getSpriceData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> spriceData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String> sprice=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                sprice=Maps.newHashMap();
                //月份
                sprice.put("month",months.get(i));
                //当月新增用户数
                sprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        sprice.put(months.get(j),String.valueOf(getRandomData("sprice").intValue()));
                    }else
                    {
                        sprice.put(months.get(j),"-1");
                    }
                }
                spriceData.add(sprice);
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

                for(Map<String,String> mp:spriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                sprice=Maps.newHashMap();

                sprice.put("month",months.get(i));
                sprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        sprice.put(subCols.get(j),"-1");
                    }else
                    {
                        sprice.put(subCols.get(j),String.valueOf(getRandomData("sprice").intValue()));
                    }
                }
                spriceData.add(sprice);
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

                for(Map<String,String> mp:spriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                sprice=Maps.newHashMap();

                sprice.put("week",weeks.get(i).getWeekOfYareName());
                sprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        sprice.put(subCols.get(j),"-1");
                    }else
                    {
                        sprice.put(subCols.get(j),String.valueOf(getRandomData("sprice").intValue()));
                    }
                }
                spriceData.add(sprice);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:spriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                sprice=Maps.newHashMap();
                //月份
                sprice.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                sprice.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        sprice.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("sprice").intValue()));
                    }else
                    {
                        sprice.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                spriceData.add(sprice);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:spriceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",spriceData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }

    /**
     * 获取连带率的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getJoinrateData")
    public ResponseBo getJoinrateData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>>  joinrateData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String>  joinrate=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                joinrate=Maps.newHashMap();
                //月份
                joinrate.put("month",months.get(i));
                //当月新增用户数
                joinrate.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        joinrate.put(months.get(j),String.valueOf(getRandomData("joinrate").intValue()));
                    }else
                    {
                        joinrate.put(months.get(j),"-1");
                    }
                }
                joinrateData.add(joinrate);
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

                for(Map<String,String> mp: joinrateData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                joinrate=Maps.newHashMap();

                joinrate.put("month",months.get(i));
                joinrate.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        joinrate.put(subCols.get(j),"-1");
                    }else
                    {
                        joinrate.put(subCols.get(j),String.valueOf(getRandomData("joinrate").intValue()));
                    }
                }
                joinrateData.add( joinrate);
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

                for(Map<String,String> mp: joinrateData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                joinrate=Maps.newHashMap();

                joinrate.put("week",weeks.get(i).getWeekOfYareName());
                joinrate.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        joinrate.put(subCols.get(j),"-1");
                    }else
                    {
                        joinrate.put(subCols.get(j),String.valueOf(getRandomData("joinrate").intValue()));
                    }
                }
                joinrateData.add(joinrate);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp: joinrateData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                joinrate=Maps.newHashMap();
                //月份
                joinrate.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                joinrate.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        joinrate.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("joinrate").intValue()));
                    }else
                    {
                        joinrate.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                joinrateData.add(joinrate);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:joinrateData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",joinrateData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }

    /**
     * 获取购买频率的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getFreqData")
    public ResponseBo getFreqData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> freqData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String> freq=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                freq=Maps.newHashMap();
                //月份
                freq.put("month",months.get(i));
                //当月新增用户数
                freq.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        freq.put(months.get(j),String.valueOf(getRandomData("freq").intValue()));
                    }else
                    {
                        freq.put(months.get(j),"-1");
                    }
                }
                freqData.add(freq);
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

                for(Map<String,String> mp:freqData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                freq=Maps.newHashMap();

                freq.put("month",months.get(i));
                freq.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        freq.put(subCols.get(j),"-1");
                    }else
                    {
                        freq.put(subCols.get(j),String.valueOf(getRandomData("freq").intValue()));
                    }
                }
                freqData.add(freq);
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

                for(Map<String,String> mp:freqData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                freq=Maps.newHashMap();

                freq.put("week",weeks.get(i).getWeekOfYareName());
                freq.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        freq.put(subCols.get(j),"-1");
                    }else
                    {
                        freq.put(subCols.get(j),String.valueOf(getRandomData("freq").intValue()));
                    }
                }
                freqData.add(freq);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:freqData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                freq=Maps.newHashMap();
                //月份
                freq.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                freq.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        freq.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("freq").intValue()));
                    }else
                    {
                        freq.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                freqData.add(freq);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:freqData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",freqData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }


    /**
     * 获取件单价的同期群数据
     * @param periodType  周期类型
     * @return
     */
    @RequestMapping("/getSpriceData")
    public ResponseBo getPriceData(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {

        JSONObject result=new JSONObject();

        //留存数据(每行一条数据)
        List<Map<String,String>> priceData=Lists.newArrayList();
        //表头
        List<String> columns=Lists.newArrayList();
        //合计数据
        Map<String,String> totalData=Maps.newLinkedHashMap();

        //存放留存用户数的临时变量
        Map<String,String> price=null;

        //按自然月
        if(PERIOD_TYPE_MONTH.equals(periodType))
        {
            //获取两个日期之间间隔的月份
            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));

            columns.add("month");
            columns.add("newuser");
            columns.addAll(months);

            //循环月 获取留存用户数
            for(int i=0;i<months.size();i++)
            {
                price=Maps.newHashMap();
                //月份
                price.put("month",months.get(i));
                //当月新增用户数
                price.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<months.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        price.put(months.get(j),String.valueOf(getRandomData("price").intValue()));
                    }else
                    {
                        price.put(months.get(j),"-1");
                    }
                }
                priceData.add(price);
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

                for(Map<String,String> mp:priceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_MONTH.equals(periodType))
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
                price=Maps.newHashMap();

                price.put("month",months.get(i));
                price.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
                    {
                        price.put(subCols.get(j),"-1");
                    }else
                    {
                        price.put(subCols.get(j),String.valueOf(getRandomData("price").intValue()));
                    }
                }
                priceData.add(price);
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

                for(Map<String,String> mp:priceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        else if(PERIOD_TYPE_INTERVAL_WEEK.equals(periodType))
        {
            columns.add("week");
            columns.add("newuser");

            //获取两个日期之间间隔的所有周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
            List<String> subCols=Lists.newArrayList();

            for(int i=1;i<=12;i++)
            {
                subCols.add("+"+i+"周");
            }

            columns.addAll(subCols);

            for(int i=0;i<weeks.size();i++)
            {
                price=Maps.newHashMap();

                price.put("week",weeks.get(i).getWeekOfYareName());
                price.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<subCols.size();j++)
                {
                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
                    {
                        price.put(subCols.get(j),"-1");
                    }else
                    {
                        price.put(subCols.get(j),String.valueOf(getRandomData("price").intValue()));
                    }
                }
                priceData.add(price);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:priceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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
        //按自然周
        else if(PERIOD_TYPE_WEEK.equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("week");
            columns.add("newuser");
            for(WeekInfo w:weeks)
            {
                columns.add(w.getWeekOfYareName());
            }

            //循环周 获取其留存率
            for(int i=0;i<weeks.size();i++)
            {
                price=Maps.newHashMap();
                //月份
                price.put("week",weeks.get(i).getWeekOfYareName());
                //当月新增用户数
                price.put("newuser",String.valueOf(getRandomData("newuser").intValue()));

                for(int j=0;j<weeks.size();j++)
                {
                    if(j>=i)
                    {
                        //获取留存用户数
                        price.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("price").intValue()));
                    }else
                    {
                        price.put(weeks.get(j).getWeekOfYareName(),"-1");
                    }
                }
                priceData.add(price);
            }

            //获取汇总数据
            for(String col:columns)
            {
                Double total=0D;
                int count=0;
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:priceData)
                {
                    if(!"-1".equals(mp.get(col)))
                    {
                        count+=1;
                        total+=Double.parseDouble(mp.get(col));
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

        result.put("columns",columns);
        result.put("data",priceData);
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
        }else if("retainCnt".equals(type))
        {
            result= RandomUtil.getIntRandom(80,100);
        }else if("loss".equals(type))
        {
            result= RandomUtil.getIntRandom(199,999)/100.00;
        }else if("lossCnt".equals(type))
        {
            result= RandomUtil.getIntRandom(80,100);
        }else if("uprice".equals(type))
        {
            result= RandomUtil.getIntRandom(500,600);
        }else if("sprice".equals(type))
        {
            result= RandomUtil.getIntRandom(200,300);
        }else if("joinrate".equals(type))
        {
            result=RandomUtil.getIntRandom(15,25)/10.00;
        }else if("freq".equals(type))
        {
            result=RandomUtil.getIntRandom(11,15)/10.00;
        }else if("price".equals(type))
        {
            result=RandomUtil.getIntRandom(400,600);
        }

        return result;
    }

    /**
     * 判断一个月份ID是否大于当前月，如果是 返回true 否则返回false
     * @param month YYYY-MM格式
     * @return
     */
    private boolean greaterThanNow(String month)
    {
        String nowMonth= LocalDate.now().getYear()+String.format("%02d",LocalDate.now().getMonthValue());
        return Integer.parseInt(month.substring(0,4)+month.substring(5,7))>Integer.parseInt(nowMonth);
    }

    private boolean greaterThanNow(BigDecimal daywid)
    {
        String now=LocalDate.now().toString().replace("-","");
        return daywid.intValue()>Integer.parseInt(now);
    }

}

