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
        else if("dweek".equals(periodType))
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
        //按自然周
        else if("week".equals(periodType))
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
        if("month".equals(periodType))
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
                if("month".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
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
                if("month".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
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
        else if("dweek".equals(periodType))
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
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
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
        //按自然周
        else if("week".equals(periodType))
        {
            //获取两个日期之间间隔的周
            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);

            columns.add("month");
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
                if("week".equals(col)||"newuser".equals(col))
                {
                    totalData.put(col,"");
                    continue;
                }

                for(Map<String,String> mp:retainCntData)
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

        result.put("columns",columns);
        result.put("data",retainCntData);
        result.put("total",totalData);
        return  ResponseBo.okWithData("",result);

    }

//    /**
//     * 获取留存率的同期群数据
//     * @param periodType  周期类型
//     * @return
//     */
//    @RequestMapping("/getRetainData")
//    public ResponseBo list(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {
//
//        JSONObject result=new JSONObject();
//
//        //留存数据(每行一条数据)
//        List<Map<String,String>> retainData=Lists.newArrayList();
//        //表头
//        List<String> columns=Lists.newArrayList();
//        //合计数据
//        Map<String,String> totalData=Maps.newLinkedHashMap();
//
//        //存放留存率的临时变量
//        Map<String,String> ret=null;
//
//        //按自然月
//        if("month".equals(periodType))
//        {
//            //获取两个日期之间间隔的月份
//            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));
//
//            columns.add("month");
//            columns.add("newuser");
//            columns.addAll(months);
//
//            //循环月 获取其留存率
//            for(int i=0;i<months.size();i++)
//            {
//                ret=Maps.newHashMap();
//                //月份
//                ret.put("month",months.get(i));
//                //当月新增用户数
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<months.size();j++)
//                {
//                    if(j>=i)
//                    {
//                        //获取留存率
//                        ret.put(months.get(j),String.valueOf(getRandomData("retain")));
//                    }else
//                    {
//                        ret.put(months.get(j),"-1");
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("month".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//        //按间隔月
//        else if("dmonth".equals(periodType))
//        {
//            columns.add("month");
//            columns.add("newuser");
//
//            //获取两个日期之间间隔的月份
//            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));
//            List<String> subCols=Lists.newArrayList();
//
//            for(int i=1;i<=12;i++)
//            {
//                subCols.add("+"+i+"月");
//            }
//
//            columns.addAll(subCols);
//
//            for(int i=0;i<months.size();i++)
//            {
//                ret=Maps.newHashMap();
//
//                ret.put("month",months.get(i));
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<subCols.size();j++)
//                {
//                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
//                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
//                    {
//                        ret.put(subCols.get(j),"-1");
//                    }else
//                    {
//                        ret.put(subCols.get(j),String.valueOf(getRandomData("retain")));
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("month".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//        //按间隔周
//        else if("dweek".equals(periodType))
//        {
//            columns.add("week");
//            columns.add("newuser");
//
//            //获取两个日期之间间隔的所有周
//            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
//            List<String> subCols=Lists.newArrayList();
//
//            for(int i=1;i<=12;i++)
//            {
//                subCols.add("+"+i+"周");
//            }
//
//            columns.addAll(subCols);
//
//            for(int i=0;i<weeks.size();i++)
//            {
//                ret=Maps.newHashMap();
//
//                ret.put("week",weeks.get(i).getWeekOfYareName());
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<subCols.size();j++)
//                {
//                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
//                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
//                    {
//                        ret.put(subCols.get(j),"-1");
//                    }else
//                    {
//                        ret.put(subCols.get(j),String.valueOf(getRandomData("retain")));
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("week".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//        //按自然周
//        else if("week".equals(periodType))
//        {
//            //获取两个日期之间间隔的周
//            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
//
//            columns.add("month");
//            columns.add("newuser");
//            for(WeekInfo w:weeks)
//            {
//                columns.add(w.getWeekOfYareName());
//            }
//
//            //循环周 获取其留存率
//            for(int i=0;i<weeks.size();i++)
//            {
//                ret=Maps.newHashMap();
//                //月份
//                ret.put("week",weeks.get(i).getWeekOfYareName());
//                //当月新增用户数
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<weeks.size();j++)
//                {
//                    if(j>=i)
//                    {
//                        //获取留存率
//                        ret.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("retain")));
//                    }else
//                    {
//                        ret.put(weeks.get(j).getWeekOfYareName(),"-1");
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("week".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//
//        result.put("columns",columns);
//        result.put("data",retainData);
//        result.put("total",totalData);
//        return  ResponseBo.okWithData("",result);
//
//    }
//
//    /**
//     * 获取留存率的同期群数据
//     * @param periodType  周期类型
//     * @return
//     */
//    @RequestMapping("/getRetainData")
//    public ResponseBo list(@RequestParam String periodType,@RequestParam String start,@RequestParam String end) {
//
//        JSONObject result=new JSONObject();
//
//        //留存数据(每行一条数据)
//        List<Map<String,String>> retainData=Lists.newArrayList();
//        //表头
//        List<String> columns=Lists.newArrayList();
//        //合计数据
//        Map<String,String> totalData=Maps.newLinkedHashMap();
//
//        //存放留存率的临时变量
//        Map<String,String> ret=null;
//
//        //按自然月
//        if("month".equals(periodType))
//        {
//            //获取两个日期之间间隔的月份
//            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));
//
//            columns.add("month");
//            columns.add("newuser");
//            columns.addAll(months);
//
//            //循环月 获取其留存率
//            for(int i=0;i<months.size();i++)
//            {
//                ret=Maps.newHashMap();
//                //月份
//                ret.put("month",months.get(i));
//                //当月新增用户数
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<months.size();j++)
//                {
//                    if(j>=i)
//                    {
//                        //获取留存率
//                        ret.put(months.get(j),String.valueOf(getRandomData("retain")));
//                    }else
//                    {
//                        ret.put(months.get(j),"-1");
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("month".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//        //按间隔月
//        else if("dmonth".equals(periodType))
//        {
//            columns.add("month");
//            columns.add("newuser");
//
//            //获取两个日期之间间隔的月份
//            List<String> months=DateUtil.getMonthBetween(start.substring(0,7),end.substring(0,7));
//            List<String> subCols=Lists.newArrayList();
//
//            for(int i=1;i<=12;i++)
//            {
//                subCols.add("+"+i+"月");
//            }
//
//            columns.addAll(subCols);
//
//            for(int i=0;i<months.size();i++)
//            {
//                ret=Maps.newHashMap();
//
//                ret.put("month",months.get(i));
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<subCols.size();j++)
//                {
//                    //j为1时，实际表示的+2月  判断 month + (j+1)个月之后，是否超过了当前月，如果不是，则去取留存用户
//                    if(greaterThanNow(DateUtil.getOffsetMonthDate(months.get(i),j+1)))
//                    {
//                        ret.put(subCols.get(j),"-1");
//                    }else
//                    {
//                        ret.put(subCols.get(j),String.valueOf(getRandomData("retain")));
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("month".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//        //按间隔周
//        else if("dweek".equals(periodType))
//        {
//            columns.add("week");
//            columns.add("newuser");
//
//            //获取两个日期之间间隔的所有周
//            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
//            List<String> subCols=Lists.newArrayList();
//
//            for(int i=1;i<=12;i++)
//            {
//                subCols.add("+"+i+"周");
//            }
//
//            columns.addAll(subCols);
//
//            for(int i=0;i<weeks.size();i++)
//            {
//                ret=Maps.newHashMap();
//
//                ret.put("week",weeks.get(i).getWeekOfYareName());
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<subCols.size();j++)
//                {
//                    //j为0时，实际表示的+1周  判断 +1周所在的begin_dt 是否已经超过了当日,如已超过，则不计算
//                    if(greaterThanNow(weeks.get(i).getWeekBeginWid()))
//                    {
//                        ret.put(subCols.get(j),"-1");
//                    }else
//                    {
//                        ret.put(subCols.get(j),String.valueOf(getRandomData("retain")));
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("week".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//        //按自然周
//        else if("week".equals(periodType))
//        {
//            //获取两个日期之间间隔的周
//            List<WeekInfo> weeks=kpiMonitorService.getWeekList(start,end);
//
//            columns.add("month");
//            columns.add("newuser");
//            for(WeekInfo w:weeks)
//            {
//                columns.add(w.getWeekOfYareName());
//            }
//
//            //循环周 获取其留存率
//            for(int i=0;i<weeks.size();i++)
//            {
//                ret=Maps.newHashMap();
//                //月份
//                ret.put("week",weeks.get(i).getWeekOfYareName());
//                //当月新增用户数
//                ret.put("newuser",String.valueOf(getRandomData("newuser").intValue()));
//
//                for(int j=0;j<weeks.size();j++)
//                {
//                    if(j>=i)
//                    {
//                        //获取留存率
//                        ret.put(weeks.get(j).getWeekOfYareName(),String.valueOf(getRandomData("retain")));
//                    }else
//                    {
//                        ret.put(weeks.get(j).getWeekOfYareName(),"-1");
//                    }
//                }
//                retainData.add(ret);
//            }
//
//            //获取汇总数据
//            for(String col:columns)
//            {
//                Double total=0D;
//                int count=0;
//                if("week".equals(col)||"newuser".equals(col))
//                {
//                    totalData.put(col,"");
//                    continue;
//                }
//
//                for(Map<String,String> mp:retainData)
//                {
//                    if(!"-1".equals(mp.get(col)))
//                    {
//                        count+=1;
//                        total=Double.parseDouble(mp.get(col));
//                    }
//                }
//
//                if(count>0)
//                {
//                    totalData.put(col, String.valueOf(ArithUtil.formatDoubleByMode(total/count,2, RoundingMode.DOWN)));
//                }else
//                {
//                    totalData.put(col,"");
//                }
//            }
//        }
//
//        result.put("columns",columns);
//        result.put("data",retainData);
//        result.put("total",totalData);
//        return  ResponseBo.okWithData("",result);
//
//    }

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

