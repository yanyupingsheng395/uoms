package com.linksteady.operate.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.StringTemplate;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.TargetDimensionMapper;
import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.domain.TargetDimension;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TargetList;
import com.linksteady.operate.domain.TgtReference;
import com.linksteady.operate.service.TargetListService;
import com.linksteady.operate.service.TargetSplitAsyncService;
import com.linksteady.operate.vo.DimJoinVO;
import com.linksteady.operate.vo.TemplateResult;
import com.linksteady.operate.vo.TgtReferenceVO;
import com.linksteady.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-05-22
 */
@Service
public class TargetListServiceImpl implements TargetListService {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    private TargetDimensionMapper targetDimensionMapper;

    private static final String TARGET_PERIOD_YEAR="year";
    private static final String TARGET_PERIOD_MONTH="month";
    private static final String TARGET_PERIOD_DAY="day";

    @Override
    public List<TargetList> getTargetList() {
        return targetListMapper.getTargetList();
    }

    @Override
    public Map<String, Object> getMonitorVal(String targetId) {
        return targetListMapper.getMonitorVal(targetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(TargetInfo target) {
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        target.setCreateBy(user.getUsername());
        target.setCreateDt(new Date());
        target.setStatus("1");
        targetListMapper.save(target);
        Long tgtId = target.getId();
        List<TargetDimension> dimensionList = target.getDimensionList();
        dimensionList.stream().forEach(x->{
            x.setId(targetDimensionMapper.getIdFromDual());
            x.setTgtId(tgtId);
        });
        targetDimensionMapper.save(dimensionList);

        return tgtId.intValue();
    }

    @Override
    public List<Map<String, Object>> getPageList(int startRow, int endRow) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return targetListMapper.getPageList(startRow, endRow, username);
    }

    @Override
    public int getTotalCount() {
        return targetListMapper.getTotalCount();
    }

    @Override
    public Map<String, Object> getDataById(Long id) {
        Map<String, Object> map = targetListMapper.getDataById(id);
        List<Map<String, Object>> list = targetDimensionMapper.getListByTgtId(id);
        map.put("DIMENSIONS", list);
        return map;
    }

    /**
     * 获取GMV的历史参考值
     * @param period
     * @param startDt
     * @param endDt
     * @param dimInfo
     * @return
     */
    @Override
    public List<TgtReferenceVO> getGmvReferenceData(String period, String startDt, String endDt, Map<String, String> dimInfo) {

        List<TgtReferenceVO> result= Lists.newLinkedList();
        //年
        if(TARGET_PERIOD_YEAR.equals(period))
        {
           //获取过去4年的周期列表
            List<String> periodList= Arrays.asList(String.valueOf(Long.valueOf(startDt)-1),String.valueOf(Long.valueOf(startDt)-2),String.valueOf(Long.valueOf(startDt)-3),String.valueOf(Long.valueOf(startDt)-4));
            List<TgtReference> data=getGmvHistoryByYear(periodList,dimInfo);

            Map<String,Double> dataMap=data.stream().collect(Collectors.toMap(TgtReference::getPeriodName,TgtReference::getValue));

            //计算同比信息
            for(int i=0;i<=periodList.size()-1;i++)
            {
                //获取当年的值
                double current=dataMap.get(periodList.get(i));
                //获取上一年的值
                double pre=dataMap.get(periodList.get(i+1));
                //计算同比增长率
                double tb=pre==0?0: ArithUtil.formatDoubleByMode((current-pre)/pre*100,2, RoundingMode.DOWN);

                TgtReferenceVO vo=new TgtReferenceVO();
                vo.setPeriod(periodList.get(i));
                vo.setKpi(String.valueOf(current));
                vo.setYearOnYear(String.valueOf(tb));

                result.add(vo);
            }

        }else if(TARGET_PERIOD_MONTH.equals(period))
        {
            //获取当前所有的月
            YearMonth yearMonth = YearMonth.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth preYearMonth=yearMonth.minus(Period.ofYears(1));

            //当前月份 过去的4个月的列表
            List<YearMonth> periodList=Arrays.asList(yearMonth.minus(Period.ofMonths(1)),yearMonth.minus(Period.ofMonths(2)),yearMonth.minus(Period.ofMonths(3)),yearMonth.minus(Period.ofMonths(4)));

            //过去三个月对应的去年同期的月份列表
            List<YearMonth> tbPeroidList=Arrays.asList(preYearMonth.minus(Period.ofMonths(1)),preYearMonth.minus(Period.ofMonths(2)),preYearMonth.minus(Period.ofMonths(3)));

            List<YearMonth> allPeriodList=Lists.newArrayList();
            allPeriodList.addAll(periodList);
            allPeriodList.addAll(tbPeroidList);

            List<String> allPeriodList2=allPeriodList.stream().map(s->s.toString().replace("-","")).collect(Collectors.toList());
           List<TgtReference> data=getGmvHistoryByMonth(allPeriodList2,dimInfo);

            Map<String,Double> dataMap=data.stream().collect(Collectors.toMap(TgtReference::getPeriodName,TgtReference::getValue));

            //计算同比和环比值
            for(int j=0;j<periodList.size()-1;j++)
            {
                //当月名称
                String currentMonthName=periodList.get(j).format(DateTimeFormatter.ofPattern("yyyy年MM月"));
                //上月名称
                String lastMonthName=periodList.get(j).minus(Period.ofMonths(1)).format(DateTimeFormatter.ofPattern("yyyy年MM月"));
                //去年同期月份名称
                String preMonthName=periodList.get(j).minus(Period.ofMonths(1)).format(DateTimeFormatter.ofPattern("yyyy年MM月"));

                double currentValue=dataMap.get(currentMonthName);
                double lastMonthValue=dataMap.get(lastMonthName);
                //去年同期值
                double preMonthValue=dataMap.get(preMonthName);

                //计算同比增长率
                double tb=preMonthValue==0?0: ArithUtil.formatDoubleByMode((currentValue-preMonthValue)/preMonthValue*100,2, RoundingMode.DOWN);
                //环比
                double hb=lastMonthValue==0?0: ArithUtil.formatDoubleByMode((currentValue-lastMonthValue)/lastMonthValue*100,2, RoundingMode.DOWN);

                TgtReferenceVO vo=new TgtReferenceVO();
                vo.setPeriod(periodList.get(j).format(DateTimeFormatter.ofPattern("yyyy年MM月")));
                vo.setKpi(String.valueOf(currentValue));
                vo.setYearOnYear(String.valueOf(tb));
                vo.setYearOverYear(String.valueOf(hb));

                result.add(vo);
            }
        }else if(TARGET_PERIOD_DAY.equals(period))
        {
            //获取开始时间 和 结束时间
            LocalDate startLocalDate = LocalDate.parse(startDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endLocalDate = LocalDate.parse(endDt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            //上一周期的gmv值
            TgtReference prePeriodValue=null;
            TgtReference currPeriodValue=null;
            //获取上一年的开始时间和结束时间
            for(int k=4;k>=0;k--)
            {
                LocalDate tempStartDate=startLocalDate.minusYears(k);
                LocalDate tempEndDate=endLocalDate.minusYears(k);
                //最早的那次取值 仅仅是为了计算第三次的同比而取的
                if(k==4)
                {
                    prePeriodValue=getGmvHistoryByPeriodDay(tempStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),tempEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),dimInfo);
                }else
                {
                    //当前周期的值
                    currPeriodValue=getGmvHistoryByPeriodDay(tempStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),tempEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),dimInfo);

                    TgtReferenceVO vo=new TgtReferenceVO();
                    vo.setPeriod(tempStartDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))+"-"+tempEndDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));


                    double prevalue=null==prePeriodValue?0:prePeriodValue.getValue();
                    double curvalue=null==currPeriodValue?0:currPeriodValue.getValue();
                    vo.setKpi(String.valueOf(curvalue));
                    double tb=prevalue==0?0: ArithUtil.formatDoubleByMode((curvalue-prevalue)/prevalue*100,2, RoundingMode.DOWN);
                    vo.setYearOnYear(String.valueOf(tb));

                    result.add(vo);

                    //将当次的值设置为上一次的值
                    prePeriodValue=currPeriodValue;
                }
            }
        }
        return result;
    }


    /**
     * 按年获取gmv的历史数据
     * @return
     */
    private List<TgtReference> getGmvHistoryByYear(List<String> periodList,Map<String, String> dimInfo)
    {
           String sqlTemplate=" SELECT\n" +
               "            W_DATE.YEAR PERIOD_NAME\n" +
               "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
               "        FROM\n" +
               "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
               "            $JOIN_TABLES$\n" +
               "       WHERE\n" +
               "            W_ORDERS.VALID_STATUS = 1\n" +
               "            $PERIOD_INFO$\n" +
               "            $WHERE_INFO$\n" +
               "       GROUP BY W_DATE.YEAR";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();

        if(periodList.size()==1)
        {
            periodInfo.append(" AND W_DATE.YEAR="+periodList.get(0));
        }else
        {
            periodInfo.append(" AND W_DATE.YEAR IN "+Joiner.on(",").skipNulls().join(periodList)+")");
        }

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 按月获取gmv的历史数据
     * @return
     */
    private List<TgtReference> getGmvHistoryByMonth(List<String> periodList,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            W_DATE.MONTH_NAME PERIOD_NAME\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$\n" +
                "       GROUP BY W_DATE.MONTH_NAME";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();


        if(periodList.size()==1)
        {
            periodInfo.append(" AND W_DATE.MONTH="+periodList.get(0));
        }else
        {
            periodInfo.append(" AND W_DATE.MONTH IN "+Joiner.on(",").skipNulls().join(periodList)+")");
        }

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeroid(stringTemplate.render());

    }

    /**
     * 获取从YYYYMMDD到YYYYMMDD之间的gmv值
     * @return
     */
    private TgtReference getGmvHistoryByPeriodDay(String startDay,String endDay,Map<String, String> dimInfo)
    {
        String sqlTemplate=" SELECT\n" +
                "            TRUNC(SUM(W_ORDERS.REAL_FEE)) VALUE\n" +
                "        FROM\n" +
                "            W_ORDERS JOIN W_DATE ON W_ORDERS.ORDER_DT_WID=W_DATE.ROW_WID\n" +
                "            $JOIN_TABLES$\n" +
                "       WHERE\n" +
                "            W_ORDERS.VALID_STATUS = 1\n" +
                "            $PERIOD_INFO$\n" +
                "            $WHERE_INFO$";

        TemplateResult templateResult=buildWhereAndJoinInfo(dimInfo);
        StringBuffer periodInfo=new StringBuffer();
        periodInfo.append(" AND W_DATE.ROW_WID>="+startDay+" AND W_DATE.ROW_WID<="+endDay);

        StringTemplate stringTemplate=new StringTemplate(sqlTemplate);
        stringTemplate.add("$JOIN_TABLES$",templateResult.getJoinInfo()).add("$WHERE_INFO$",templateResult.getFilterInfo())
                .add("$PERIOD_INFO$",periodInfo.toString());

        return targetListMapper.getGmvHistoryByPeriodDay(stringTemplate.render());

    }


    private TemplateResult buildWhereAndJoinInfo(Map<String, String> dimInfo)
    {
        Joiner joiner = Joiner.on(",").skipNulls();
        StringBuilder joins=new StringBuilder();
        StringBuilder filters=new StringBuilder();

        dimInfo.forEach((k,v)->{
            //根据key获取关联信息
            DimJoinVO dimJoinVO=KpiCacheManager.getInstance().getDimJoinList().get("W_ORDERS",k);

            joins.append(" JOIN ").append(dimJoinVO.getDimTable()).append(" ").append(dimJoinVO.getDimTableAlias()).append(" ON ").append(dimJoinVO.getRelation());

            //where条件
            List<String> values= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(v);
            //字符串类型
            if("STRING".equals(dimJoinVO.getDimWhereType()))
            {
                if(values.size()==1)
                {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append("='").append(values.get(0)).append("'");
                }else
                {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values.stream().map(a->"'"+a+"'").toArray())).append(")");
                }
            }else if("NUMBER".equals(dimJoinVO.getDimWhereType())) {
                //数字类型
                if (values.size() == 1) {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append("=").append(values.get(0));
                } else {
                    filters.append(" AND ").append(dimJoinVO.getDimWhere()).append(" IN(").append(joiner.join(values)).append(")");
                }
            }

        });

        TemplateResult result=new TemplateResult();
        result.setFilterInfo(filters.toString());
        result.setJoinInfo(joins.toString());

        return result;
    }
}


