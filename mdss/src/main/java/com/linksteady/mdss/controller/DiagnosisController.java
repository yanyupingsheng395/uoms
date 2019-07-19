package com.linksteady.mdss.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.mdss.config.KpiCacheManager;
import com.linksteady.mdss.domain.Diag;
import com.linksteady.mdss.domain.DiagCondition;
import com.linksteady.mdss.service.DiagService;
import com.linksteady.mdss.util.UomsConstants;
import com.linksteady.mdss.vo.NodeDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 诊断
 * @author caohuixue
 */
@RestController
@RequestMapping("/diag")
@Slf4j
public class DiagnosisController {

    @Autowired
    private DiagService diagService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;



    @RequestMapping("/list")
    public ResponseBo list(@RequestBody QueryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Diag> diagList = diagService.getRows((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(), request.getParam().get("diagName"));
        Long total = diagService.getTotalCount(request.getParam().get("diagName"));
        return ResponseBo.okOverPaging(null, total.intValue(), diagList);
    }

    @PostMapping("/add")
    public ResponseBo add(Diag diag, String conditions) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(conditions);
            List<DiagCondition> conditionList = jsonArray.toJavaList(DiagCondition.class);
            Long result = diagService.save(diag, conditionList);
            return ResponseBo.okWithData(null, result);
        }catch (Exception e) {
            log.error("保存诊断信息错误，",e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return ResponseBo.error();
        }
    }

    @PostMapping("/getNodes")
    public ResponseBo getNodes(@RequestParam("diagId") String diagId) {
        List<NodeDataVO> res = diagService.getNodes(diagId);
        return ResponseBo.okWithData(null, JSON.toJSON(res));
    }

    @PostMapping("/deleteById")
    public ResponseBo deleteById(@RequestParam("id") String id) {
        try {
            diagService.deleteById(id);
            return ResponseBo.ok("删除成功！");
        }catch (Exception ex) {
            log.error("删除错误：", ex);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(ex),1,512));
            return ResponseBo.error("删除失败！");
        }
    }

    @GetMapping("/getDimByDiagId")
    public ResponseBo getDimByDiagId(@RequestParam("diagId") String diagId) {
        return ResponseBo.okWithData(null, diagService.geDiagInfoById(diagId));
    }

    /**
     * 获取指标列表
     * @return
     */
    @GetMapping("/getKpi")
    public ResponseBo getKpi() {
        Map<String, Object> map = Maps.newHashMap();
        KpiCacheManager.getInstance().getKpiCodeNamePair().forEach((k, v)->{
            if(UomsConstants.DIAG_KPI_LIST.contains(k))
            {
                map.put(k,v);
            }
        });

        return ResponseBo.okWithData(null, map);
    }

    /**
     * 获取维度列表
     * @return
     */
    @GetMapping("/getDimension")
    public ResponseBo getDimension() {
        Map<String, Object> map = Maps.newLinkedHashMap();

        KpiCacheManager.getInstance().getDiagDimList().forEach((k,v)->{
            if(UomsConstants.DIAG_DIM_LIST.contains(k))
            {
                map.put(k,v);
            }
        });
        return ResponseBo.okWithData(null, map);
    }

    /**
     * 根据维度code获取值
     * @return
     */
    @GetMapping("/getDimensionVal")
    public ResponseBo getDimensionVal(String key) {
        return ResponseBo.okWithData(null,KpiCacheManager.getInstance().getDiagDimValueList().row(key));
    }

}
