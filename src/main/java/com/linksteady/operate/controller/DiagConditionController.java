package com.linksteady.operate.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.DiagConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diagcondition")
public class DiagConditionController {

    @Autowired
    private DiagConditionService service;

    @GetMapping("/redisCreate")
    public ResponseBo redisCreate(@RequestParam("data") String data, @RequestParam("diagId") String diagId, @RequestParam("nodeId") String nodeId) throws Exception{
        service.redisCreate(data, diagId, nodeId);
        return null;
    }

    @GetMapping("/redisRead")
    public String redisRead(@RequestParam("diagId") String diagId, @RequestParam("nodeId") String nodeId) throws Exception{
        String result = service.redisRead(diagId, nodeId);
        return result;
    }
}
