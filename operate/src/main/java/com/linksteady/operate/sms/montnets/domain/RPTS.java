package com.linksteady.operate.sms.montnets.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @功能概要：状态报告集合类
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class RPTS
{
	// 获取状态报告是否成功 0：成功 非0：失败
	private int			result;

	// 状态报告集合
	private List<RPT>	rpts	= new ArrayList<RPT>();

	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}

	public List<RPT> getRpts()
	{
		return rpts;
	}

	public void setRpts(List<RPT> rpts)
	{
		this.rpts = rpts;
	}

}
