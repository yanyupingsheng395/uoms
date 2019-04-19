package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import com.linksteady.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 */
@RestController
@RequestMapping("/reason")
public class ReasonController  extends BaseController {

    @Autowired
    ReasonService reasonService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取原因探究列表
     * @param request  包装分页参数的对象
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo list(@RequestBody  QueryRequest request) {
        List<Map<String,Object>> result=reasonService.getReasonList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());

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

        String curuser=String.valueOf(((User)SecurityUtils.getSubject().getPrincipal()).getUserId());
        int primaryKey=reasonService.getReasonPrimaryKey();

        SimpleDateFormat sf2=new SimpleDateFormat("yyyyMMdd");
        Date now=new Date();
        String reasonName=sf2.format(now)+"-"+primaryKey;
        reasonVO.setReasonName(reasonName);

        //写入主记录
        reasonService.saveReasonData(reasonVO,curuser,primaryKey);
        //写入选择的维度信息 UO_REASON_DETAIL
        reasonService.saveReasonDetail(primaryKey,reasonVO.getDims());

        return  ResponseBo.ok(reasonName);
    }

    /**
     * 删除原因探究
     * @param reasonId 原因ID
     * @return
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
    @RequestMapping("/UpdateProgressById")
    public ResponseBo UpdateProgressById(@RequestParam String reasonId) {
        //将相关的指标写进目标表 todo 后续交由算法完成
        reasonService.findReasonKpisSnp(reasonId);

        //更新进度
        reasonService.updateProgressById(reasonId,100);
        return ResponseBo.ok("");
    }


    /**
     * 获取原因探究的详细信息
     * @param reasonId 原因ID
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonInfoById")
    public ResponseBo getReasonInfoById(@RequestParam String reasonId) {
       Map<String,Object> result= reasonService.getReasonInfoById(reasonId);
        return ResponseBo.ok(result);
    }

//    /**
//     * 根据模板CODE和reasonId获取到原因KPI的列表
//     * @param reasonId 原因ID
//     * @param   templateCode 模板CODE
//     * @return ResponseBo对象
//     */
//    @RequestMapping("/getRelatedKpiList")
//    public ResponseBo getRelatedKpiList(@RequestParam String reasonId,@RequestParam String templateCode) {
//        List<Map<String,String>> result= reasonService.getRelatedKpiList(reasonId,templateCode);
//        return ResponseBo.ok(result);
//    }

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
        Map<String,String> result=(Map)KpiCacheManager.getInstance().getReasonDimValueList().get(dimCode);
        return ResponseBo.okWithData("",result);
    }

    /**
     * 效果评估
     * @param
     * @return ResponseBo对象
     */
    @RequestMapping("/getEffectForecast")
    public ResponseBo getEffectForecast(@RequestParam String reasonId, @RequestParam("code") String code) {
        //todo 后端进行数据校验

        List<String> data= Lists.newArrayList();
        String fcode="";
        for(String s:data)
        {
            //如果存在，先删除
            if(reasonService.getReasonResultCount(reasonId,fcode)>0)
            {
                reasonService.deleteReasonResult(reasonId,fcode);
            }

            //todo 进行回归分析
            reasonService.saveReasonResult(reasonId,fcode,"产品","y=a*x+b","x每变动一份，则带动GMV提升b");
        }

        List<Map<String,Object>> result=reasonService.getReasonResultList(reasonId);
        return ResponseBo.okWithData("",result);
    }
}
