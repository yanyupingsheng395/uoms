package com.linksteady.common.domain;

import java.util.HashMap;

public class ResponseBo extends HashMap<String, Object> {

	private static final long serialVersionUID = -8713837118340960775L;

	// 成功
	private static final Integer SUCCESS = 200;
	// 异常 失败
	private static final Integer FAIL = 500;

	private static final Integer WARN = 400;


	public ResponseBo() {
		put("code", SUCCESS);
		put("msg", "操作成功");
		put("data", null);
	}

	public static ResponseBo ok() {
		return new ResponseBo();
	}

	public static ResponseBo error() {
		return ResponseBo.error("");
	}

	public static ResponseBo error(Object msg) {
		ResponseBo responseBo = new ResponseBo();
		responseBo.put("code", FAIL);
		responseBo.put("msg", msg);
		return responseBo;
	}

	public static ResponseBo warn(Object msg) {
		ResponseBo responseBo = new ResponseBo();
		responseBo.put("code", WARN);
		responseBo.put("msg", msg);
		return responseBo;
	}

	public static ResponseBo ok(Object msg) {
		ResponseBo responseBo = new ResponseBo();
		responseBo.put("code", SUCCESS);
		responseBo.put("msg", msg);
		return responseBo;
	}

	public static ResponseBo okWithData(Object msg, Object data) {
		ResponseBo responseBo = new ResponseBo();
		responseBo.put("code", SUCCESS);
		responseBo.put("msg", msg);
		responseBo.put("data", data);
		return responseBo;
	}

	public static ResponseBo okOverPaging(Object msg,int totalCount,Object rows) {
		ResponseBo responseBo = new ResponseBo();
		responseBo.put("code", SUCCESS);
		responseBo.put("msg", msg);
		responseBo.put("total",totalCount);
		responseBo.put("rows",rows);
		return responseBo;
	}

	@Override
	public ResponseBo put(String key, Object value) {
		super.put(key, value);
		return this;
	}

}
