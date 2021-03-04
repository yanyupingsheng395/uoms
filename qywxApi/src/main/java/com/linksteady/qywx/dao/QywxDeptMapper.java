package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxDeptUser;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/27
 */
public interface QywxDeptMapper {

    List<Map<String, Object>> getDeptList(Integer limit, Integer offset);

    int getDeptCount();

    void saveDept(Long id,String deptName,Long parentId,int orderNo);

    void deleteDept();


}
