package com.ad.IntroMi;

public class Item {

	private Long mTime;
	private int mRssi;	
	private String mMac;

	public void setTime(long time)  

	{

		this.mTime =  time;
	}

	public void setMac(String mac)
	{
		this.mMac = mac;
	}

	public String getMac()
	{
		return this.mMac;
	}


	public  long  getTime()  
	{

		return  this.mTime;
	}


	public void setRssi(int rssi)

	{
		this.mRssi = rssi;	

	}

	public int getRssi()

	{
		return this.mRssi;	

	}


}
