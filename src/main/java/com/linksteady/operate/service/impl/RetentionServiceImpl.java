package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.RetentionMapper;
import com.linksteady.operate.domain.Retention;
import com.linksteady.operate.service.RetentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RetentionServiceImpl implements RetentionService {


    @Autowired
    private RetentionMapper retentionMapper;

    @Override
    public List<Retention> findMonthDataByDate(String startDate, String endDate) {
        startDate = startDate.replaceAll("-", "");
        endDate = endDate.replaceAll("-", "");
        List<Retention> retentionList = retentionMapper.findMonthDataByDate(startDate, endDate);
        retentionList.stream().forEach(retention -> {
            String month0 = retention.getMonth0().equals("0") ? "" : retention.getMonth0();
            String month1 = retention.getMonth1().equals("0") ? "" : retention.getMonth1() + "%";
            String month2 = retention.getMonth2().equals("0") ? "" : retention.getMonth2() + "%";
            String month3 = retention.getMonth3().equals("0") ? "" : retention.getMonth3() + "%";
            String month4 = retention.getMonth4().equals("0") ? "" : retention.getMonth4() + "%";
            String month5 = retention.getMonth5().equals("0") ? "" : retention.getMonth5() + "%";
            String month6 = retention.getMonth6().equals("0") ? "" : retention.getMonth6() + "%";
            retention.setMonth0(month0);
            retention.setMonth1(month1);
            retention.setMonth2(month2);
            retention.setMonth3(month3);
            retention.setMonth4(month4);
            retention.setMonth5(month5);
            retention.setMonth6(month6);
        });
        return retentionList;
    }
}
