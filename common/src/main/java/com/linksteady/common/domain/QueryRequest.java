package com.linksteady.common.domain;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

public class QueryRequest implements Serializable {

	private static final long serialVersionUID = -4869594085374385813L;

	private int pageSize;

	private int pageNum;

	private String sort;

	private String sortOrder;

	private String order;

	private Map<String,String> param;

	private int start;

	private int end;

	private int limit;

	private int offset;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public Map<String, String> getParam() {
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	public int getStart() {
		return (pageNum-1) * pageSize + 1;
	}

	public int getEnd() {
		return pageNum * pageSize;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getLimit() {
		return pageSize == 0 ? limit:pageSize;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return (pageNum-1) * pageSize;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
