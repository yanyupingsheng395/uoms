package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 流程图拆解过程
 */
@RestController
@RequestMapping("/progress")
public class ProgressController {

    /**
     * 获取乘法公式
     * @param code
     * @return
     */
    @GetMapping("/geFormula")
    public ResponseBo geFormula(@RequestParam("code") String code) {
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
}
