package com.linksteady.system.service;

import java.util.List;

import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.IService;
import com.linksteady.system.domain.Dept;

public interface DeptService extends IService<Dept> {

	Tree<Dept> getDeptTree();

	List<Dept> findAllDepts(Dept dept);

	Dept findByName(String deptName);

	Dept findById(Long deptId);
	
	void addDept(Dept dept);
	
	void updateDept(Dept dept);

	void deleteDepts(String deptIds);
}
