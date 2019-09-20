package com.linksteady.operate.sms.montnets.domain;

/**
 * @功能概要：上行实体类
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class MO
{
	// 平台流水编号
	private Long	msgid;

	// 上行手机号
	private String	mobile;

	// 上行通道号
	private String	spno;

	// 上行扩展子号
	private String	exno;

	// 上行时间
	private String	rtime;

	// 上行内容
	private String	content;

	public Long getMsgid()
	{
		return msgid;
	}

	public void setMsgid(Long msgid)
	{
		this.msgid = msgid;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public String getSpno()
	{
		return spno;
	}

	public void setSpno(String spno)
	{
		this.spno = spno;
	}

	public String getExno()
	{
		return exno;
	}

	public void setExno(String exno)
	{
		this.exno = exno;
	}

	public String getRtime()
	{
		return rtime;
	}

	public void setRtime(String rtime)
	{
		this.rtime = rtime;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

}
