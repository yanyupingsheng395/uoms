package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.domain.DiagCondition;
import com.linksteady.operate.service.DiagService;
import com.linksteady.operate.util.UomsConstants;
import com.linksteady.operate.vo.NodeDataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 诊断
 * @author caohuixue
 */
@RestController
@RequestMapping("/diag")
public class DiagnosisController {

    @Autowired
    private DiagService diagService;

    private static Logger logger = LoggerFactory.getLogger(DiagnosisController.class);

    @RequestMapping("/list")
    public Map<String, Object> list(@RequestBody QueryRequest request) {
        Map<String, Object> result = new HashMap<>(16);
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Diag> diagList = diagService.getRows((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(), request.getParam().get("diagName"));
        Long total = diagService.getTotalCount(request.getParam().get("diagName"));
        result.put("rows", diagList);
        result.put("total", total);
        return result;
    }

    @PostMapping("/add")
    public ResponseBo add(Diag diag, String conditions) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(conditions);
            List<DiagCondition> conditionList = jsonArray.toJavaList(DiagCondition.class);
            Long result = diagService.save(diag, conditionList);
            return ResponseBo.okWithData(null, result);
        }catch (Exception e) {
            logger.error("保存诊断信息错误，",e);
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
            logger.error("删除错误：", ex);
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