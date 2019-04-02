package com.linksteady.operate.controller;
import com.linksteady.operate.domain.DiagDetail;
import com.linksteady.operate.service.DiagDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diagdetail")
public class DiagDetailController {

    @Autowired
    private DiagDetailService service;

    @RequestMapping("/save")
    public void save(DiagDetail diagDetail) {
        service.save(diagDetail);
    }

}
