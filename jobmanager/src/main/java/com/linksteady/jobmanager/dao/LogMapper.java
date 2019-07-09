package com.linksteady.jobmanager.dao;

import com.linksteady.common.domain.SysLog;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface LogMapper extends Mapper<SysLog>, MySqlMapper<SysLog> { }