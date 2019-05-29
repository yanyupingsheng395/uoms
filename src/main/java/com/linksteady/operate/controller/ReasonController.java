package com.linksteady.operate.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.Reason;
import com.linksteady.operate.domain.ReasonRelMatrix;
import com.linksteady.operate.domain.ReasonResult;
import com.linksteady.operate.service.CacheService;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.thrift.ThriftClient;
import com.linksteady.operate.vo.ReasonVO;
import com.linksteady.system.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 * @author huang
 */
@Slf4j
@RestController
@RequestMapping("/reason")
public class ReasonController  extends BaseController {

    @Autowired
    ReasonService reasonService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CacheService cacheService;

    @Autowired
    ThriftClient thriftClient;

    /**
     * 获取原因探究列表
     * @param request  包装分页参数的对象
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo list(@RequestBody  QueryRequest request) {
        List<Reason> result=reasonService.getReasonList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());
        int totalCount= reasonService.getReasonTotalCount();
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

    /**
     * 提交分析(手工提交)
     * @param reasonVO 原因探究主表
     * @return
     */
    @RequestMapping("/submitAnalysisManual")
    public ResponseBo submitAnalysisManual(@RequestBody ReasonVO reasonVO) {

        String username=getCurrentUser().getUsername();
        int primaryKey=reasonService.getReasonPrimaryKey();

        SimpleDateFormat sf2=new SimpleDateFormat("yyyyMMdd");
        Date now=new Date();
        String reasonName=sf2.format(now)+"-"+primaryKey;
        reasonVO.setReasonName(reasonName);

        //写入主记录
        reasonService.saveReasonData(reasonVO,username,primaryKey);
        //写入选择的维度信息 UO_REASON_DETAIL
        reasonService.saveReasonDetail(primaryKey,reasonVO.getDims());

        return  ResponseBo.ok(reasonName);
    }

    /**
     * 删除原因探究
     * @param reasonId 原因ID
     * @return 返回包装类
     */
    @RequestMapping("/deleteReasonById")
    public ResponseBo deleteReasonById(@RequestParam String reasonId) {
        reasonService.deleteReasonById(reasonId);
        return ResponseBo.ok("");
    }

    /**
     * 更新原因探究的进度
     * @param reasonId 原因ID
     * @return ResponseBo对象
     */
    @RequestMapping("/updateProgressById")
    public ResponseBo ppdateProgressById(@RequestParam String reasonId) {

        reasonService.findReasonKpisSnp(reasonId);
        return ResponseBo.ok();

    }


    /**
     * 获取原因探究的详细信息
     * @param reasonId 原因ID
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonInfoById")
    public ResponseBo getReasonInfoById(@RequestParam String reasonId) {
       Map<String,Object> result= reasonService.getReasonAllInfoById(reasonId);
        return ResponseBo.ok(result);
    }

    /**
     * 根据reasonId, 模板CODE获取到此模板下原因指标的快照信息
     * @param templateCode 模板编码
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonKpisSnp")
    public ResponseBo getReasonKpisSnp(@RequestParam String reasonId,@RequestParam String templateCode) {
        List<Map<String,Object>> result=reasonService.getReasonKpisSnp(reasonId,templateCode);
        return ResponseBo.ok(result);
    }

    /**
     * 根据reasonID 获取到关注的KPI列表
     * @param reasonId 原因ID
     * @return ResponseBo对象
     */
    @RequestMapping("/getConcernReasonKpis")
    public ResponseBo getConcernReasonKpis(@RequestParam String reasonId) {
        List<Map<String,Object>> result=reasonService.getConcernKpiList(reasonId);
        return ResponseBo.ok(result);
    }

    /**
     * 将原因KPI加入到当前的关注列表中 或 取消关注
     * @param reasonId 原因ID
     * @return ResponseBo对象
     */
    @RequestMapping("/addConcernKpi")
    public ResponseBo addConcernKpi(@RequestParam String reasonId,@RequestParam String templateCode,@RequestParam String reasonKpiCode) {
        //判断是否存在与表中 如果存在，则删除，否则增加
        int count=reasonService.getConcernKpiCount(reasonId,templateCode,reasonKpiCode);

        //不在列表中 进行增加操作
        if(count==0)
        {
            reasonService.addConcernKpi(reasonId,templateCode,reasonKpiCode);
        }else  //进行删除操作
        {
            reasonService.deleteConcernKpi(reasonId,templateCode,reasonKpiCode);
        }
        return ResponseBo.ok("success");
    }


/**
 * 获取原因的指标列表
 * @return ResponseBo对象
 */
    @RequestMapping("/getReasonKpiList")
    public ResponseBo getReasonKpiList() {
        Map<String,String> reasonKpiList= Maps.newHashMap();
       KpiCacheManager.getInstance().getReasonKpiList().forEach((k,v)->{
           reasonKpiList.put(k,v.getKpiName());
        });
        return ResponseBo.okWithData("",reasonKpiList);
    }


    /**
     * 获取维度列表
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonDimList")
    public ResponseBo getReasonDimList() {
        Map<String,String> result= KpiCacheManager.getInstance().getReasonDimList();
        return ResponseBo.okWithData("",result);
    }

    /**
     * 获取维度对应的值列表
     * @param dimCode 维度编码
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonDimValuesList")
    public ResponseBo getReasonDimValuesList(@RequestParam String dimCode) {
        Map<String,String> result=KpiCacheManager.getInstance().getReasonDimValueList().row(dimCode);
        return ResponseBo.okWithData("",result);
    }

    /**
     * 效果评估
     * @param
     * @return ResponseBo对象
     */
    @RequestMapping("/getEffectForecast")
    public ResponseBo getEffectForecast(@RequestParam String reasonId, @RequestParam("code") String code) {

        List<String> signal= Arrays.asList("a","b","d","d","e","f","g","h","i","j","k","l","m","n");

        if(reasonService.getReasonResultCount(reasonId,code)==0)
        {
            //根据reasonId获取kpiCode
            String kpiCode=reasonService.getReasonHeaderInfoById(reasonId).getKpiCode();

            Joiner joiner=Joiner.on("</br>").skipNulls();
            List<String> data= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(code);

            //变量图例
            List<String> reasonKpiNames=Lists.newArrayList();
            //说明
            List<String> busincess=Lists.newArrayList();
            //回归公式
            StringBuilder regression=new StringBuilder();

            String kpiName=KpiCacheManager.getInstance().getKpiCodeNamePair().get("gmv");
            regression.append("y=");
            reasonKpiNames.add("y : "+kpiName);

            //调用thrift的服务，获取回归公式 然后写结果表
            try {
                thriftClient.open();
                String callback=thriftClient.getThriftService().submitReasonForecast(Integer.parseInt(reasonId),kpiCode,code);

                //系数和截距通过 | 进行分割
                List<String> resultList= Splitter.on('|').trimResults().omitEmptyStrings().splitToList(callback);
                //各个系数之间通过 ，分割
                List<String> ceof= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(resultList.get(0));

                //如果程序正常运行 data的length和ceof的长度是一致的，且顺序对应
                for(int i=0;i<ceof.size();i++)
                {
                    String sign=signal.get(i);
                    //拼凑回归公式
                    regression.append(ceof.get(i)+"*"+sign);
                    String reasonName=KpiCacheManager.getInstance().getReasonRelateKpiList().get(data.get(i)).getReasonKpiName();
                    reasonKpiNames.add(sign+" : "+reasonName);
                    busincess.add(reasonName+"每变动一份，"+kpiName+"变动"+ceof.get(i)+"份; ");
                }

                //拼凑截距
                regression.append(resultList.get(1));

                //根据回归公式写结果表
                reasonService.saveReasonResult(reasonId,code,joiner.join(reasonKpiNames),regression.toString(),joiner.join(busincess));

                List<ReasonResult> reasonResults=reasonService.getReasonResultList(reasonId);
                return ResponseBo.okWithData("",reasonResults);
            } catch (TException e) {
                log.error("效果评估出错",e);
                return ResponseBo.error();
            } finally {
                thriftClient.close();
            }
        }else{
            List<ReasonResult> reasonResults=reasonService.getReasonResultList(reasonId);
            return ResponseBo.okWithData("",reasonResults);
        }
    }

    /**
     * 对指标进行校验
     * @param
     * @return ResponseBo对象
     */
    @RequestMapping("/validateRelateKpi")
    public ResponseBo validateRelateKpi(@RequestParam String reasonId, @RequestParam("code") String code) {

        List<String> codeList= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(code);
        List<String> validateResult=Lists.newArrayList();

        ReasonRelMatrix reasonRelMatrix=null;
        Double relateValue=0d;
        String info="";

        if(codeList.size()>1)
        {
            for(int i=0;i<codeList.size();i++)
            {
               for(int j=i+1;j<codeList.size();j++)
               {
                   //获取到交叉的相关系数
                   reasonRelMatrix=reasonService.getReasonResultByCode(reasonId,codeList.get(i),codeList.get(j)).get(0);


                   relateValue= reasonRelMatrix.getRelateValue();

                   if(relateValue>=0.3)
                   {
                       info="因子【"+reasonRelMatrix.getfName()+"】和因子【"+reasonRelMatrix.getRfName()+"】相关度较高，不能同时选择";
                       validateResult.add(info);
                   }
               }
            }
        }
        return ResponseBo.okWithData("",validateResult);
    }

}

