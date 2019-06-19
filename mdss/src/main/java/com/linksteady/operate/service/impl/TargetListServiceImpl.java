package com.linksteady.operate.service.impl;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.TargetDimensionMapper;
import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.dao.TgtDismantMapper;
import com.linksteady.operate.domain.TargetDimension;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.domain.TgtDismant;
import com.linksteady.operate.service.TargetListService;
import com.linksteady.operate.vo.Echart;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-05-22
 */
@Service
public class TargetListServiceImpl implements TargetListService {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    private TargetDimensionMapper targetDimensionMapper;

    @Autowired
    private TgtDismantMapper tgtDismantMapper;


    @Override
    public List<TargetInfo> getTargetList(String username) {
        return targetListMapper.getTargetList(username);
    }

    @Override
    public Map<String, Object> getMonitorVal(String targetId) {
        return targetListMapper.getMonitorVal(targetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(TargetInfo target) {
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        target.setCreateBy(user.getUsername());
        target.setCreateDt(new Date());
        target.setStatus("0");
        targetListMapper.save(target);
        Long tgtId = target.getId();
        List<TargetDimension> dimensionList = target.getDimensionList();
        if(dimensionList.size() > 0) {
            dimensionList.stream().forEach(x->{
                x.setId(targetDimensionMapper.getIdFromDual());
                x.setTgtId(tgtId);
            });
            targetDimensionMapper.save(dimensionList);
        }
        return tgtId;
    }

    @Override
    public List<Map<String, Object>> getPageList(int startRow, int endRow) {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return targetListMapper.getPageList(startRow, endRow, username);
    }

    @Override
    public int getTotalCount() {
        String username = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return targetListMapper.getTotalCountByUsername(username);
    }

    @Override
    public Map<String, Object> getDataById(Long id) {
        Map<String, Object> map = targetListMapper.getDataById(id);
        List<Map<String, Object>> list = targetDimensionMapper.getListByTgtId(id);
        map.put("DIMENSIONS", list);
        return map;
    }

    @Override
    public void deleteDataById(String id) {
        targetListMapper.updateTargetStatus(Long.valueOf(id), "-2");
    }

    /**
     * 获取指标的参照信息
     * @param targetId
     * @return
     */
    @Override
    public Map<String, Object> getDismantData(Long targetId) {
        List<TgtDismant> dataList = tgtDismantMapper.findByTgtId(targetId);
        Echart echart = new Echart();
        echart.setxAxisName("日期");
        echart.setyAxisName("分解目标");
        List<String> xdata = dataList.stream().map(x->x.getPeriodDate()).collect(Collectors.toList());
        echart.setxAxisData(xdata);
        echart.setyAxisData(dataList.stream().map(x->String.valueOf(x.getTgtVal())).collect(Collectors.toList()));
        List<Double> row1 = dataList.stream().map(x->x.getTgtVal()).collect(Collectors.toList());
        List<Double> row2 = dataList.stream().map(x->x.getTgtWeightIdx()).collect(Collectors.toList());
        List<Double> row3 = dataList.stream().map(x->x.getTgtPercent()).collect(Collectors.toList());
        Map<String, Object> result = Maps.newHashMap();
        result.put("chart", echart);
        result.put("head", xdata);
        result.put("row1", row1);
        result.put("row2", row2);
        result.put("row3", row3);
        return result;
    }

    /**
     *  根据主键ID获取目标的详细信息
     * @param targetId
     * @return
     */
    @Override
    public TargetInfo selectByPrimaryKey(long targetId) {
        return targetListMapper.selectByPrimaryKey(targetId);
    }

    @Override
    public void updateTargetStatus(long targetId, String status) {
        targetListMapper.updateTargetStatus(targetId,status);
    }

}


