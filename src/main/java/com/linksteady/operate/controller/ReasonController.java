package com.linksteady.operate.controller;

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
     * 获取原因探究列表
     * @param reasonVO 原因探究主表
     * @return
     */
    @RequestMapping("/submitAnalysis")
    public ResponseBo submitAnalysis(@RequestBody ReasonVO reasonVO) {

        String curuser=String.valueOf(((User)SecurityUtils.getSubject().getPrincipal()).getUserId());

        int primaryKey=reasonService.getReasonPrimaryKey();

        //写入主记录 返回名称
        String reasonName=reasonService.saveReasonData(reasonVO,curuser,primaryKey);

        // 写入选择的维度信息 UO_REASON_DETAIL
       reasonService.saveReasonDetail(primaryKey,reasonVO.getDims());

        //UO_REASON_TEMPLATE
        reasonService.saveReasonTemplate(primaryKey,reasonVO.getTemplates());

        //原因KPI列表

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
        reasonService.findReasonKpis(reasonId);

        //更新进度
        reasonService.updateProgressById(reasonId,100);
        return ResponseBo.ok("");
    }


    /**
     * 获取原因探究的详细信息
     * @param reasonId 原因ID
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonDetailById")
    public ResponseBo getReasonDetailById(@RequestParam String reasonId) {
       Map<String,Object> result= reasonService.getReasonInfoById(reasonId);

        return ResponseBo.ok(result);
    }

    /**
     * 根据模板CODE和reasonId获取到原因KPI的列表
     * @param reasonId 原因ID
     * @param   templateCode 模板CODE
     * @return ResponseBo对象
     */
    @RequestMapping("/getRelatedKpiList")
    public ResponseBo getRelatedKpiList(@RequestParam String reasonId,@RequestParam String templateCode) {
        List<Map<String,String>> result= reasonService.getRelatedKpiList(reasonId,templateCode);
        return ResponseBo.ok(result);
    }

    /**
     * 根据KPI CODE和模板CODE获取到此模板下指标的历史信息
     * @param kpiCode KPI编码
     * @param templateCode 模板编码
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonKpiHistroy")
    public ResponseBo getReasonKpiHistroy(@RequestParam String reasonId,@RequestParam String kpiCode,@RequestParam String templateCode) {
        List<Map<String,Object>> result=reasonService.getReasonKpiHistroy(reasonId,kpiCode,templateCode);
        return ResponseBo.ok(result);
    }

    /**
     * 根据模板CODE、KPI CODE和reasonID 获取到原因KPI的详细数据
     * @param reasonId 原因ID
     * @param templateCode 指标编码
     * @param reasonKpiCode 指标编码
     * @return ResponseBo对象
     */
    @RequestMapping("/getReasonRelatedKpi")
    public ResponseBo getReasonRelatedKpi(@RequestParam String reasonId,@RequestParam String templateCode,@RequestParam String reasonKpiCode) {
        Map<String,Object> result=reasonService.getReasonRelatedKpi(reasonId,templateCode,reasonKpiCode);

        Map rt=reasonService.getReasonRelateKpiDataFromRedis(reasonId,templateCode,reasonKpiCode);
        return ResponseBo.okWithData(result,rt);
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
    public ResponseBo addConcernKpi(@RequestParam String reasonId,@RequestParam String kpiCode,@RequestParam String templateCode,@RequestParam String reasonKpiCode) {
        //判断是否存在与表中 如果存在，则删除，否则增加
        int count=reasonService.getConcernKpiCount(reasonId,kpiCode,templateCode,reasonKpiCode);

        //不在列表中 进行增加操作
        if(count==0)
        {
            reasonService.addConcernKpi(reasonId,kpiCode,templateCode,reasonKpiCode);
        }else  //进行删除操作
        {
            reasonService.deleteConcernKpi(reasonId,kpiCode,templateCode,reasonKpiCode);
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


}
