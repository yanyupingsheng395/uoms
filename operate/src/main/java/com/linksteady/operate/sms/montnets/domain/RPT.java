package com.linksteady.operate.sms.montnets.domain;

/**
 * @功能概要：状态报告类
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class RPT
{
	// 平台流水编号
	private Long	msgid;

	// 用户自定义流水编号
	private String	custid;

	// 当前条数
	private Integer	pknum;

	// 总条数
	private Integer	pktotal;

	// 短信接收的手机号
	private String	mobile;

	// 完整的通道号
	private String	spno;

	// 扩展号
	private String	exno;

	// 状态报告对应的下行发送时间
	private String	stime;

	// 状态报告返回时间
	private String	rtime;

	// 接收状态,0:成功 非0:失败
	private Integer	status;

	// 状态报告错误代码
	private String	errcode;

	// 下行时填写的exdata
	private String	exdata;
	
	//状态报告错误代码的描述
	private String errdesc;

	public String getErrdesc()
	{
		return errdesc;
	}

	public void setErrdesc(String errdesc)
	{
		this.errdesc = errdesc;
	}

	public Long getMsgid()
	{
		return msgid;
	}

	public void setMsgid(Long msgid)
	{
		this.msgid = msgid;
	}

	public String getCustid()
	{
		return custid;
	}

	public void setCustid(String custid)
	{
		this.custid = custid;
	}

	public Integer getPknum()
	{
		return pknum;
	}

	public void setPknum(Integer pknum)
	{
		this.pknum = pknum;
	}

	public Integer getPktotal()
	{
		return pktotal;
	}

	public void setPktotal(Integer pktotal)
	{
		this.pktotal = pktotal;
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

	public String getStime()
	{
		return stime;
	}

	public void setStime(String stime)
	{
		this.stime = stime;
	}

	public String getRtime()
	{
		return rtime;
	}

	public void setRtime(String rtime)
	{
		this.rtime = rtime;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public String getErrcode()
	{
		return errcode;
	}

	public void setErrcode(String errcode)
	{
		this.errcode = errcode;
	}

	public String getExdata()
	{
		return exdata;
	}

	public void setExdata(String exdata)
	{
		this.exdata = exdata;
	}

}
