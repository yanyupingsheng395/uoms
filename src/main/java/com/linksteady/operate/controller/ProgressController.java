package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 流程图拆解过程
 */
@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private RedisTemplate redisTemplate;

    private final String ROOT_KPI_CODE = "gmv";

    /**
     * 获取乘法公式
     * @param code
     * @return
     */
    @GetMapping("/getFormula")
    public ResponseBo getFormula(@RequestParam("code") String code) {
        Map<String, String> data = KpiCacheManager.getInstance().getCodeFomularPair();
        String formula = data.get(code);
        return ResponseBo.okWithData(null, formula);
    }

    /**
     * 获取可拆解的指标
     * @param code
     * @return
     */
    @GetMapping("/getKpi")
    public ResponseBo getKpi(@RequestParam("code") String code) {
        Map<String, Object> data = KpiCacheManager.getInstance().getKpiDismant();
        return ResponseBo.okWithData(null, data.get(code));
    }

    /**
     * 获取指标组合
     * @param code
     * @return
     */
    @GetMapping("/getKpiComb")
    public ResponseBo getKpiComb(@RequestParam("code") String code) {
        Map<String, String> data = KpiCacheManager.getInstance().getCodeNamePair();
        Map<String, Object> dismant = KpiCacheManager.getInstance().getKpiDismant();
        Map<String, Object> map = (Map)dismant.get(code);
        String tmp1 = (String)map.get("DISMANT_PART1_CODE");
        String tmp2 = (String)map.get("DISMANT_PART2_CODE");
        Map<String, Object> result = new HashMap<>();
        if(data.get(tmp1) != null) {
            result.put("k", tmp1);
            result.put("v", data.get(tmp1));
        }
        if(data.get(tmp2) != null) {
            result.put("k", tmp2);
            result.put("v", data.get(tmp2));
        }
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/getDiagDimList")
    public ResponseBo getDiagDimList() {
        Map<String,String> result = KpiCacheManager.getInstance().getDiagDimList();
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/getDiagDimValueList")
    public ResponseBo getDiagDimValueList(@RequestParam("code") String code) {
        Map<String,Object> result = KpiCacheManager.getInstance().getDiagDimValueList();
        Map<String, Object> data = (Map)result.get(code);
        return ResponseBo.okWithData(null, data);
    }

    /**
     * 通过Diag_ID 设置为levelID的key
     * @return
     */
    @GetMapping("/getKpiLevelId")
    public ResponseBo getKpiLevelId(@RequestParam("id") Long id) {
        long kpiLevelId = seqGeneratorByRedisAtomicLong("uoms_seq_node_id_" + id);
        return ResponseBo.okWithData(null, kpiLevelId);
    }

    // 获取KPI_LEVEL_ID
    public synchronized long seqGeneratorByRedisAtomicLong(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        long result = counter.incrementAndGet();
        return result;
    }

    @GetMapping("/getNodeId")
    public ResponseBo getNodeIdFromSequence() {
        long id = progressService.getNodeIdFromSequence();
        return ResponseBo.okWithData(null, id);
    }

    @GetMapping("/getRootNode")
    public ResponseBo getRootNode() {
        String rootKpiCode = ROOT_KPI_CODE;
        Map<String, String> data = KpiCacheManager.getInstance().getCodeNamePair();
        Map<String, Object> result = new HashMap<>();
        result.put(rootKpiCode, data.get(rootKpiCode));
        return ResponseBo.okWithData(null, result);
    }
}
