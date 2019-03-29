package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.KeyPointMapper;
import com.linksteady.operate.dao.ReasonMapper;
import com.linksteady.operate.domain.KeyPointMonth;
import com.linksteady.operate.domain.KeyPointYear;
import com.linksteady.operate.domain.Reason;
import com.linksteady.operate.service.KeyPointService;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ReasonServiceImpl implements ReasonService {

    @Autowired
    private ReasonMapper reasonMapper;

    @Autowired
    DozerBeanMapper dozerBeanMapper;


    public List<Map<String,Object>> getReasonList(int startRow,int endRow)
    {
       return  reasonMapper.getReasonList(startRow,endRow);
    }

    public int getReasonTotalCount()
    {
        return  reasonMapper.getReasonTotalCount();
    }

    @Override
    public int saveReasonData(ReasonVO reasonVO,String curuser,int primaryKey) {
        //将VO转化成DO
        Reason reasonDo=dozerBeanMapper.map(reasonVO, Reason.class);

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf2=new SimpleDateFormat("yyyyMMdd");
        Date now=new Date();

        reasonDo.setReasonId(primaryKey);
        reasonDo.setReasonName(sf2.format(now)+"-"+primaryKey);
        reasonDo.setStatus("A");
        reasonDo.setProgress(0);
        reasonDo.setCreateDt(sf.format(now));
        reasonDo.setUpdateDt(sf.format(now));
        reasonDo.setCreateBy(curuser);
        reasonDo.setUpdateBy(curuser);
        reasonMapper.saveReasonData(reasonDo);
        return primaryKey;
    }

    @Override
    public int getReasonPrimaryKey() {
        return reasonMapper.getReasonPrimaryKey();
    }


}
