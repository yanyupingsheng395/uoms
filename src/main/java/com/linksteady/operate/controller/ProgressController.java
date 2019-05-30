package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagResultInfo;
import com.linksteady.operate.domain.KpiConfigInfo;
import com.linksteady.operate.domain.KpiDismantInfo;
import com.linksteady.operate.service.DiagHandleService;
import com.linksteady.operate.service.ProgressService;
import com.linksteady.operate.vo.DiagConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private DiagHandleService diagHandleService;

    private final String ROOT_KPI_CODE = "gmv";

    /**
     * 获取乘法公式
     * @param code
     * @return
     */
    @GetMapping("/getFormula")
    public ResponseBo getFormula(@RequestParam("code") String code) {
        Map<String, String> data = KpiCacheManager.getInstance().getDiagcodeFomularList();
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
        Map<String, KpiDismantInfo> data = KpiCacheManager.getInstance().getKpiDismant();
        return ResponseBo.okWithData(null, data.get(code));
    }


    @GetMapping("/getDiagDimList")
    public ResponseBo getDiagDimList() {
        Map<String,String> result = KpiCacheManager.getInstance().getDiagDimList();
        return ResponseBo.okWithData(null, result);
    }

    @GetMapping("/getDiagDimValueList")
    public ResponseBo getDiagDimValueList(@RequestParam("code") String code) {
        Map<String, String> data =  KpiCacheManager.getInstance().getDiagDimValueList().row(code);
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
        long res;
        String value = (String)redisTemplate.opsForValue().get(key);
        if(value == null) {
            redisTemplate.opsForValue().set(key, "1");
            res = 1L;
        }else {
            res = Long.valueOf(value) + 1;
            redisTemplate.opsForValue().set(key, String.valueOf(res));
        }
        return res;
    }

    @GetMapping("/getNodeId")
    public ResponseBo getNodeIdFromSequence() {
        long id = progressService.getNodeIdFromSequence();
        return ResponseBo.okWithData(null, id);
    }

    @GetMapping("/getRootNode")
    public ResponseBo getRootNode() {
        String rootKpiCode = ROOT_KPI_CODE;
        Map<String, KpiConfigInfo> diagKpiList = KpiCacheManager.getInstance().getDiagKpiList();
        Map<String, String> data = Maps.newHashMap();
        diagKpiList.forEach((k,v)->{
           data.put(k,v.getKpiName());
        });

        KpiCacheManager.getInstance().getDiagKpiList();
        Map<String, Object> result = new HashMap<>(16);
        result.put(rootKpiCode, data.get(rootKpiCode));
        return ResponseBo.okWithData(null, result);
    }

    // redis
    @PostMapping("/saveDiagHandleInfo")
    public ResponseBo saveDiagHandleInfo(@RequestParam("diagHandleInfo") String diagHandleInfo) {
        try{
            JSONObject jsonObject = JSONObject.parseObject(diagHandleInfo);
            DiagHandleInfo obj = jsonObject.toJavaObject(DiagHandleInfo.class);
            if(null == obj.getWhereinfo()) {
                List<DiagConditionVO> tmp = new ArrayList<>();
                obj.setWhereinfo(tmp);
            }
            diagHandleService.setDiagHandleInfoToRedis(obj);
            diagHandleService.generateDiagData(obj);
            return ResponseBo.ok();
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBo.error();
        }
    }

    // 点击节点从redis获取数据
    @GetMapping("/generateDiagData")
    public ResponseBo generateDiagData(@RequestParam("diagId") int diagId, @RequestParam("kpiLevelId") int kpiLevelId) {
        try{
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            DiagResultInfo diagResultInfo = diagHandleService.getResultFromRedis(diagId, kpiLevelId);
            return ResponseBo.okWithData(null, diagResultInfo);
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBo.error();
        }
    }

    /**
     *
     * @param diagId
     * @param kpiLevelId
     * @return
     */
    @GetMapping("/generateDiagDataOfEdit")
    public ResponseBo generateDiagDataOfEdit(@RequestParam("diagId") int diagId, @RequestParam("kpiLevelId") int kpiLevelId) {
        try{
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
            DiagHandleInfo diagHandleInfo = diagHandleService.getDiagHandleInfoFromRedis(diagId, kpiLevelId);
            DiagResultInfo diagResultInfo = diagHandleService.generateDiagData(diagHandleInfo);
            return ResponseBo.okWithData(null, diagResultInfo);
        }catch (Exception ex) {
            ex.printStackTrace();
            return ResponseBo.error();
        }
    }


    /**
     * 原因探究之前判断该KPI是否在list中
     * @param kpiCode
     * @param kpiName
     * @return
     */
    @GetMapping("/checkReasonList")
    public ResponseBo checkReasonList(@RequestParam("kpiCode") String kpiCode, @RequestParam("kpiName") String kpiName) {
        Map<String, KpiConfigInfo> result = KpiCacheManager.getInstance().getReasonKpiList();
        if(null != result.get(kpiCode) && kpiName.equals(result.get(kpiCode).getKpiName())) {
            return ResponseBo.okWithData(null, true);
        }else {
            return ResponseBo.okWithData(null, false);
        }
    }
}
