package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.domain.LcSpuInfo;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.OrderingConstants;
import com.linksteady.operate.vo.LcSpuVO;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 */
@RestController
@RequestMapping("/lifecycle")
public class LifeCycleController extends BaseController {

    @Autowired
    LifeCycleService lifeCycleService;

    @Autowired
    DozerBeanMapper dozerBeanMapper;


    /**
     * 获取spu列表
     * @param
     * @return
     */
    @RequestMapping("/getSpuList")
    public ResponseBo lifecycleCatList(@RequestParam   String startDt,@RequestParam  String endDt,String filterType,String source) {

        //最终构造的返回值
        Map<String,Object> resultMap=Maps.newHashMap();

        Map<String,Double> stats=Maps.newHashMap();
        DoubleSummaryStatistics dst=null;
        double[] quartiles=null;

        //根据startDT,endDT,source获取到符合条件的SPU的列表
        List<LcSpuInfo> list=lifeCycleService.getSpuList();

        LcSpuVO  lsVO=null;
        List<LcSpuVO> resultList=Lists.newArrayList();

        //表头
        List<Map<String,String>> columns=Lists.newArrayList();

        Map<String,String> temp=Maps.newHashMap();
        temp.put("spuName","SPU名称");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("spuWid","spuWid");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("stockNum","库存数量");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("stockbysales","库销比");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("timeToMarket","上市日期");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("onsaleDuration","在售时长(天）");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("salesDt","适销时间");
        columns.add(temp);

        temp=Maps.newHashMap();
        temp.put("orderNo","名次");
        columns.add(temp);

        if("gmv".equals(filterType))
        {
             //返回的列信息
            temp=Maps.newHashMap();
            temp.put("gmvCont","GMV贡献率");
            columns.add(temp);

            temp=Maps.newHashMap();
            temp.put("gmvRelate","GMV相关性");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);

                lsVO.setGmvCont(getRandomValue("gmvCont"));
                lsVO.setGmvRelate(getRandomValue("gmvRelate"));

                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.GMV_CONT_ORDERING);

            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getGmvCont).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getGmvCont).toArray());
        }
        else if("user".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("userCont","用户数贡献率");
            columns.add(temp);

            temp=Maps.newHashMap();
            temp.put("newUserCont","新用户数贡献率");
            columns.add(temp);

            temp=Maps.newHashMap();
            temp.put("oldUserCont","复购用户数贡献率");
            columns.add(temp);

            temp=Maps.newHashMap();
            temp.put("loyaltyCont","忠诚用户数贡献率");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);

                lsVO.setUserCont(getRandomValue("userCont"));
                lsVO.setNewUserCont(getRandomValue("userCont"));
                lsVO.setOldUserCont(getRandomValue("userCont"));
                lsVO.setLoyaltyCont(getRandomValue("userCont"));

                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.USER_CONT_ORDERING);
            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getUserCont).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getUserCont).toArray());
        }
        else if("pocount".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("poCount","订单数贡献率");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setPoCount(getRandomValue("pocount"));

                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.POCOUNT_CONT_ORDERING);
            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getPoCount).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getPoCount).toArray());
        }
        else if("joinrate".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("joinrate","连带率");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setJoinrate(getRandomValue("joinrate"));

                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.JOINRATE_ORDERING);
            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getJoinrate).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getJoinrate).toArray());
        }
        else if("sprice".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("sprice","件单价");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setSprice(getRandomValue("sprice"));

                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.SPRICE_ORDERING);

            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getSprice).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getSprice).toArray());
        }
        else if("profit".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("profit","毛利率");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);

                lsVO.setProfit(getRandomValue("profit"));
                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.PROFIT_ORDERING);
            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getProfit).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getProfit).toArray());
        }

        int orderNo=1;
        for(LcSpuVO v:resultList)
        {
             v.setOrderNo(orderNo);
             orderNo++;
        }

        stats.put("max",dst.getMax());
        stats.put("min",dst.getMin());
        stats.put("q1",quartiles[0]);
        stats.put("q2",quartiles[1]);
        stats.put("q3",quartiles[2]);

        resultMap.put("columns",columns);
        resultMap.put("data",resultList);
        resultMap.put("stats",stats);

        return  ResponseBo.okWithData("",resultMap);
    }


    private Double getRandomValue(String type)
    {
        double  result =0d;

        if("gmvCont".equals(type))
        {
            result= RandomUtil.getIntRandom(1,200)/10.00;
        }else if("gmvRelate".equals(type))
        {
            result= RandomUtil.getIntRandom(10,100)/100.00;
        }else if("userCont".equals(type))
        {
            result= RandomUtil.getIntRandom(20,30)/100.00;
        }else if("pocount".equals(type))
        {
            result= RandomUtil.getIntRandom(20,30)/100.00;
        }else if("joinrate".equals(type))
        {
            result=RandomUtil.getIntRandom(15,25)/10.00;
        }else if("sprice".equals(type))
        {
            result= RandomUtil.getIntRandom(200,300);
        }else if("profit".equals(type))
        {
            result= RandomUtil.getIntRandom(100,1000)/100.00;
        }

        return result;
    }

    /**
     * 获取SPU的筛选条件列表
     */
    @RequestMapping("/getSpuFilterList")
    public ResponseBo getSpuFilterList() {

        List<Map<String,String>> result= Lists.newArrayList();

        Map<String,String> temp= Maps.newHashMap();
        temp.put("gmv","GMV贡献率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("user","用户数贡献率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("pocount","订单数贡献率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("joinrate","连带率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("sprice","件单价");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("profit","毛利率");
        result.add(temp);

        return  ResponseBo.okWithData("",result);
    }


}

