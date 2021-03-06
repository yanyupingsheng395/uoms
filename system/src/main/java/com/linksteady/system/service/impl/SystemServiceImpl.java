package com.linksteady.system.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.system.dao.SystemMapper;
import com.linksteady.system.domain.SysInfo;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.system.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-05-06
 */
@Service
@Slf4j
public class SystemServiceImpl extends BaseService<SysInfo> implements SystemService {

    @Autowired
    private SystemMapper systemMapper;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSystem(SysInfo system) {
        system.setCreateDt(new Date());
        system.setCreateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
        this.save(system);
    }

    @Override
    public SysInfo findSystem(Long id) {
        return systemMapper.findSystem(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystem(SysInfo system) {
        try{
            system.setUpdateDt(new Date());
            system.setUpdateBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
            this.updateNotNull(system);
        }catch (Exception e) {
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSystem(String ids) {
        List<Long> list = Arrays.asList(ids.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        this.batchDelete(list, "id", SysInfo.class);
    }

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
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SysInfo> findAllSystem() {
        return systemMapper.findAll();
    }

    @Override
    public SysInfo findByName(String name,Long id) {
        Example example = new Example(SysInfo.class);
        Example.Criteria criteria=example.createCriteria();

        criteria.andCondition("name=", name);

        if(null!=id)
        {
            criteria.andEqualTo("id",id);
        }
        List<SysInfo> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }
}
