package com.linksteady.common.domain;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Map;

public class QueryRequest implements Serializable {

	private static final long serialVersionUID = -4869594085374385813L;

	private int pageSize;
	private int pageNum;

	private Map<String,String> param;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

//	@Override
//	public String toString() {
//		return MoreObjects.toStringHelper(this)
//				.add("pageSize", pageSize)
//				.add("pageNum", pageNum)
//				.toString();
//	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public Map<String, String> getParam() {
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}
}
