package com.linksteady.mdss.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.mdss.domain.Retention;
import com.linksteady.mdss.service.RetentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/retention")
public class RetentionController extends BaseController {

    @Autowired
    private RetentionService retentionService;

    /**
     * 根据起止时间查询数据
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/list")
    public List<Retention> list(String startDate, String endDate) {
        List<Retention> retentionList = retentionService.findMonthDataByDate(startDate, endDate);
        return retentionList;
    }
}
