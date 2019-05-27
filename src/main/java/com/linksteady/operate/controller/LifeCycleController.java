package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.ArithUtil;
import com.linksteady.common.util.DataStatisticsUtils;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.domain.LcSpuInfo;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.OrderingConstants;
import com.linksteady.operate.util.PearsonCorrelationUtil;
import com.linksteady.operate.vo.LcSpuVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 * @author huang
 */
@RestController
@RequestMapping("/lifecycle")
@Slf4j
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
    public ResponseBo lifecycleCatList(@RequestParam  String startDt,@RequestParam  String endDt,String filterType,String source) {

        //最终构造的返回值
        Map<String,Object> resultMap=Maps.newHashMap();

        Map<String,Double> stats=Maps.newHashMap();
        DoubleSummaryStatistics dst=null;
        //存放四分位数的数组
        double[] quartiles=null;

        //数据库返回的spu列表
        List<LcSpuInfo> list=null;
        //返回的spu列表
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
            //根据startDT,endDT,source获取到符合条件的SPU的列表及其gmv
            list=lifeCycleService.getSpuList(StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));

            //计算汇总的gmv
            double gmvSum=list.stream().mapToDouble(LcSpuInfo::getGmvTotal).sum();

            //返回的列信息
            temp=Maps.newHashMap();
            temp.put("gmvCont","GMV贡献率");
            columns.add(temp);

            for(LcSpuInfo lsi:list)
            {
                LcSpuVO lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setGmvCont(gmvSum==0?0:ArithUtil.formatDoubleByMode(lsi.getGmvTotal()/gmvSum*100,2,RoundingMode.DOWN));
                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.GMV_CONT_ORDERING);

            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getGmvCont).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getGmvCont).toArray());
        }
        else if("newuser".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("newUserCont","首购用户数贡献率");
            columns.add(temp);

            //根据startDT,endDT,source获取到符合条件的SPU的列表及其新用户数
            list=lifeCycleService.getSpuListWithUserCount(StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));

            //计算所有的首购用户数
            double newuserSum=list.stream().mapToInt(LcSpuInfo::getNewuser).sum();

            for(LcSpuInfo lsi:list)
            {
                LcSpuVO lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setNewUserCont(newuserSum==0?0:ArithUtil.formatDoubleByMode(lsi.getNewuser()/newuserSum*100,2,RoundingMode.DOWN));
                resultList.add(lsVO);
            }

            //对结果进行排序 按用户数贡献率排序
            resultList.sort(OrderingConstants.NEW_USER_CONT_ORDERING);
            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getNewUserCont).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getNewUserCont).toArray());
        }
        else if("olduser".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("oldUserCont","复购用户数贡献率");
            columns.add(temp);

            //根据startDT,endDT,source获取到符合条件的SPU的列表及其老用户数
            list=lifeCycleService.getSpuListWithUserCount(StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));

            //计算所有的首购用户数
            double olduserSum=list.stream().mapToDouble(LcSpuInfo::getOlduser).sum();

            for(LcSpuInfo lsi:list)
            {
                LcSpuVO lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setOldUserCont(olduserSum==0?0:ArithUtil.formatDoubleByMode(lsi.getOlduser()/olduserSum*100,2,RoundingMode.DOWN));
                resultList.add(lsVO);
            }

            //对结果进行排序 按用户数贡献率排序
            resultList.sort(OrderingConstants.OLD_USER_CONT_ORDERING);
            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getOldUserCont).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getOldUserCont).toArray());
        }
        else if("pocount".equals(filterType))
        {
            temp=Maps.newHashMap();
            temp.put("poCount","订单数贡献率");
            columns.add(temp);

            //根据startDT,endDT,source获取到符合条件的SPU的列表及其订单数
            list=lifeCycleService.getSpuListWithPoCount(StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));

            //计算所有的订单数
            double poCountSum=list.stream().mapToDouble(LcSpuInfo::getPoCount).sum();

            for(LcSpuInfo lsi:list)
            {
                LcSpuVO lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                lsVO.setPoCount(poCountSum==0?0:ArithUtil.formatDoubleByMode(lsi.getPoCount()/poCountSum*100,2,RoundingMode.DOWN));

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

            //根据startDT,endDT,source获取到符合条件的SPU的列表及其连带率
            list=lifeCycleService.getSpuListWithJoinRate(StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));

            for(LcSpuInfo lsi:list)
            {
                LcSpuVO lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
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

            //根据startDT,endDT,source获取到符合条件的SPU的列表及其件单价
            list=lifeCycleService.getSpuListWithSprice(StringUtils.replace(startDt,"-",""),StringUtils.replace(endDt,"-",""));

            for(LcSpuInfo lsi:list)
            {
                LcSpuVO lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);
                resultList.add(lsVO);
            }

            //对结果进行排序
            resultList.sort(OrderingConstants.SPRICE_ORDERING);

            //求统计信息  最大值、最小值
            dst=resultList.stream().mapToDouble(LcSpuVO::getSprice).summaryStatistics();
            quartiles=DataStatisticsUtils.getQuartiles(resultList.stream().mapToDouble(LcSpuVO::getSprice).toArray());
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
        temp.put("newuser","首购用户数贡献率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("olduser","复购用户数贡献率");
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

        return  ResponseBo.okWithData("",result);
    }


}

