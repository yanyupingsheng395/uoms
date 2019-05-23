package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.TargetDimensionMapper;
import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.domain.TargetDimension;
import com.linksteady.operate.domain.TargetList;
import com.linksteady.operate.service.TargetListService;
import com.linksteady.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hxcao on 2019-05-22
 */
@Service
public class TargetListServiceImpl implements TargetListService {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    private TargetDimensionMapper targetDimensionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(TargetList target) {
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        target.setCreateBy(user.getUsername());
        target.setCreateDt(new Date());
        targetListMapper.save(target);
        Long tgtId = target.getId();
        List<TargetDimension> dimensionList = target.getDimensionList();
        dimensionList.stream().forEach(x->{
            x.setId(targetDimensionMapper.getIdFromDual());
            x.setTgtId(tgtId);
        });
        targetDimensionMapper.save(dimensionList);
    }

    @Override
    public List<Map<String, Object>> getPageList(int startRow, int endRow) {
        String userId = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        return targetListMapper.getPageList(startRow, endRow, userId);
    }

    @Override
    public int getTotalCount() {
        return targetListMapper.getTotalCount();
    }

    @Override
    public Map<String, Object> getDataById(Long id) {
        Map<String, Object> map = targetListMapper.getDataById(id);
        List<Map<String, Object>> list = targetDimensionMapper.getListByTgtId(id);
        map.put("DIMENSIONS", list);
        return map;
    }
}
