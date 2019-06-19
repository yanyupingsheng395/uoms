package com.linksteady.system.service.impl;

import com.linksteady.common.domain.Application;
import com.linksteady.common.domain.Role;
import com.linksteady.common.domain.System;
import com.linksteady.system.dao.ApplicationMapper;
import com.linksteady.system.dao.SystemMapper;
import com.linksteady.system.service.ApplicationService;
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
public class ApplicationServiceImpl extends BaseService<Application> implements ApplicationService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addApplication(Application application) {
        String id = applicationMapper.getIdFromSeq();
        application.setApplicationId(id);
        this.save(application);
    }

    @Override
    public Application findApplication(String id) {
            return applicationMapper.findApplication(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplication(Application application) {
        try{
            this.updateNotNull(application);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        this.batchDelete(list, "applicationId", Application.class);
    }

    @Override
    public List<Application> findAllApplication(Application application) {
        try {
            Example example = new Example(Application.class);
            if (StringUtils.isNotBlank(application.getApplicationName())) {
                example.createCriteria().andLike("applicationName","%"+application.getApplicationName()+"%");
            }
            example.setOrderByClause("application_id");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Application> findAllApplication() {
        return applicationMapper.findAll();
    }


    @Override
    public Application findByName(String name) {
        Example example = new Example(Application.class);
        example.createCriteria().andCondition("applicationName=", name.toLowerCase());
        List<Application> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

}
