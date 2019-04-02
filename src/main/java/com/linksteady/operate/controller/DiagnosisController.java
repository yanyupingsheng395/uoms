package com.linksteady.operate.controller;

import com.github.pagehelper.PageHelper;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.Diag;
import com.linksteady.operate.service.DiagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/diag")
public class DiagnosisController {

    @Autowired
    private DiagService diagService;

    @RequestMapping("/list")
    public Map<String, Object> list(@RequestBody QueryRequest request) {
        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Diag> diagList = diagService.getRows((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize());
        Long total = diagService.getTotalCount();
        result.put("rows", diagList);
        result.put("total", total);
        return result;
    }

    @PostMapping("/add")
    public ResponseBo add(Diag diag) {
        try {
            diagService.save(diag);
            return ResponseBo.ok();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error();
        }
    }
}
