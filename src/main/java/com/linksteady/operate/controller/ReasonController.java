package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
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
     * @return
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
     * @return
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
     * @return
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
     * @return
     */
    @RequestMapping("/getReasonKpiHistroy")
    public ResponseBo getReasonKpiHistroy(@RequestParam String kpiCode,@RequestParam String templateCode) {
        List<Map<String,Object>> result=null;
        if(null!=templateCode&&"concern".equals(templateCode))
        {
            result= reasonService.getReasonKpiHistroy(kpiCode,"B");
        }else
        {
            result= reasonService.getReasonKpiHistroy(kpiCode,templateCode);
        }

        return ResponseBo.ok(result);
    }

    /**
     * 根据模板CODE、KPI CODE和reasonID 获取到原因KPI的详细数据
     * @param reasonId 原因ID
     * @param templateCode 指标编码
     * @param reasonKpiCode 指标编码
     * @return
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
     * @return
     */
    @RequestMapping("/getConcernReasonKpis")
    public ResponseBo getConcernReasonKpis(@RequestParam String reasonId) {
        return ResponseBo.ok(reasonId);
    }

    /**
     * 将原因KPI加入到当前的关注列表中
     * @param reasonId 原因ID
     * @return
     */
    @RequestMapping("/addConcernKpi")
    public ResponseBo addConcernKpi(@RequestParam String reasonId) {

        return ResponseBo.ok(111);
    }

}
