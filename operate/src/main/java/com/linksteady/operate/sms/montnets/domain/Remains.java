package com.linksteady.operate.sms.montnets.domain;

/**
 * 
 * @功能概要：余额对象
 * @公司名称： ShenZhen Montnets Technology CO.,LTD.
 */
public class Remains
{
	//是否成功的标识   0:成功;其他则为错误码
	private int result=-310099;
	
	//计费类型  0:按条计费;1:按金额计费
	private int chargetype=0;
	
    //剩余条数  chargetype为1时,balance为0.
	private int balance=0;
	
	//剩余金额   chargetype为0时,money为0.
	private  String money="0";

	public Remains()
	{
		
	}
	
	public Remains(int result)
	{
		this.result=result;
	}
	
	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}

	public int getChargetype()
	{
		return chargetype;
	}

	public void setChargetype(int chargetype)
	{
		this.chargetype = chargetype;
	}

	public int getBalance()
	{
		return balance;
	}

	public void setBalance(int balance)
	{
		this.balance = balance;
	}

	public String getMoney()
	{
		return money;
	}

	public void setMoney(String money)
	{
		this.money = money;
	}

}
