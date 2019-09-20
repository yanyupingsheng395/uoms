package com.linksteady.operate.sms.montnets.domain;

import lombok.Data;

/**
 * @功能概要：个性化信息详情类
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
@Data
public class MultiMt
{
	/**
	 * 短信接收的手机号
	 */
	private String	mobile;

	/**
	 * 短信内容
	 */
	private String	content;

	/**
	 * 扩展号
	 */
	private String	exno;

	/**
	 * 用户自定义流水编号
	 */
	private String	custid;

	/**
	 * 自定义扩展数据
	 */
	private String	exdata;

	/**
	 * 业务类型
	 */
	private String svrtype;
}
