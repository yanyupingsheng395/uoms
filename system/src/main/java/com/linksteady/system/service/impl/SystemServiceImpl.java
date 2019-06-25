package com.linksteady.system.service.impl;

import com.linksteady.common.domain.SysInfo;
import com.linksteady.system.dao.SystemMapper;
import com.linksteady.common.domain.Role;
import com.linksteady.common.domain.SysInfo;
import com.linksteady.system.service.SystemService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hxcao on 2019-05-06
 */
@Service
public class SystemServiceImpl extends BaseService<SysInfo> implements SystemService {

    @Autowired
    private SystemMapper systemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSystem(SysInfo system) {
        String id = systemMapper.getIdFromSeq();
        system.setId(id);
        system.setCreateDt(new Date());
        this.save(system);
    }

    @Override
    public SysInfo findSystem(String id) {
        return systemMapper.findSystem(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystem(SysInfo system) {
        try{
            this.updateNotNull(system);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSystem(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        this.batchDelete(list, "id", SysInfo.class);
    }

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<SysInfo> findAllSystem(SysInfo system) {
        try {
            Example example = new Example(SysInfo.class);
            if (StringUtils.isNotBlank(system.getName())) {
                example.createCriteria().andCondition("name=", system.getName());
            }
            example.setOrderByClause("create_dt");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SysInfo> findAllSystem() {
        return systemMapper.findAll();
    }

    @Override
    public SysInfo findByName(String name) {
        Example example = new Example(Role.class);
        example.createCriteria().andCondition("lower(name)=", name.toLowerCase());
        List<SysInfo> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<SysInfo> findUserSystem(String username) {
          return systemMapper.findUserSystem(username);
    }
}