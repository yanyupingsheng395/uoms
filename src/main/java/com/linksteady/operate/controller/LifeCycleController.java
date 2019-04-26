package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.domain.LcSpuInfo;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.vo.LcSpuVO;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        Map<String,Object> resultMap=Maps.newHashMap();

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


        if("gmv".equals(filterType))
        {
             //返回的列信息
            temp=Maps.newHashMap();
            temp.put("gmvCont","GMV贡献率");
            columns.add(temp);

            temp=Maps.newHashMap();
            temp.put("gmvRelate","GMV相关性");
            columns.add(temp);

            temp=Maps.newHashMap();
            temp.put("orderNo","名次");
            columns.add(temp);


            for(LcSpuInfo lsi:list)
            {
                lsVO=dozerBeanMapper.map(lsi, LcSpuVO.class);

                lsVO.setGmvCont(getRandomValue("gmvCont"));
                lsVO.setGmvRelate(getRandomValue("gmvRelate"));
                lsVO.setOrderNo(1);

                resultList.add(lsVO);
            }


        }
        else if("user".equals(filterType))
        {

        }
        else if("pocount".equals(filterType))
        {

        }
        else if("joinrate".equals(filterType))
        {

        }
        else if("sprice".equals(filterType))
        {

        }
        else if("profit".equals(filterType))
        {

        }

        //对结果进行排序

        resultMap.put("columns",columns);
        resultMap.put("data",resultList);

        return  ResponseBo.okWithData("",resultMap);
    }


    private Double getRandomValue(String type)
    {
        double  result =0d;

        if("gmvCont".equals(type))
        {
            result= RandomUtil.getIntRandom(20,30)/100.00;
        }else if("retain".equals(type))
        {
            result= RandomUtil.getIntRandom(10,100)/100.00;
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
        temp.put("user","用户数");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("pocount","订单数");
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
