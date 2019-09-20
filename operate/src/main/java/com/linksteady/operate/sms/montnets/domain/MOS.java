package com.linksteady.operate.sms.montnets.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @功能概要：上行集合类
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class MOS
{
	// 获取上行是否成功 0：成功 非0：失败
	private int			result;

	// 上行集合
	private List<MO>	mos	= new ArrayList<MO>();

	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}

	public List<MO> getMos()
	{
		return mos;
	}

	public void setMos(List<MO> mos)
	{
		this.mos = mos;
	}
}
