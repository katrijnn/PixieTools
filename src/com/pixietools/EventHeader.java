package com.pixietools;

public class EventHeader 
{
	public int eventPattern = 0;
	public int eventTimeHi = 0;
	public int eventTimeLo = 0;
	
	public long binaryStartPosition = 0;
	
	public boolean channel0Hit = false;
	public boolean channel1Hit = false;
	public boolean channel2Hit = false;
	public boolean channel3Hit = false;
	
	
	// this function is for use with the "mark position"
	// and "rollback" functions in PixieBinFile
	public void copy(EventHeader eh)
	{
		this.eventPattern = eh.eventPattern;
		this.eventTimeHi = eh.eventTimeHi;
		this.eventTimeLo = eh.eventTimeLo;
		this.binaryStartPosition = eh.binaryStartPosition;
		this.channel0Hit = eh.channel0Hit;
		this.channel1Hit = eh.channel1Hit;
		this.channel2Hit = eh.channel2Hit;
		this.channel3Hit = eh.channel3Hit;
	}
	
	public int getChannelHitCount()
	{
		int channelHitCount = 0;
		
		if (channel0Hit) 
			channelHitCount += 1;
		if (channel1Hit) 
			channelHitCount += 1;
		if (channel2Hit) 
			channelHitCount += 1;
		if (channel3Hit) 
			channelHitCount += 1;
		
		return channelHitCount;
	}
	
	public boolean isChannelHit(int channelNumber)
	{
		if (channelNumber == 0 && channel0Hit)
			return true;
		else if (channelNumber == 1 && channel1Hit)
			return true;
		else if (channelNumber == 2 && channel2Hit)
			return true;
		else if (channelNumber == 3 && channel3Hit)
			return true;
		else
			return false;
	}
	
	public long getChannel0Position()
	{
		long pos = 0;
		if (channel0Hit)
		{
			pos += binaryStartPosition;
			pos += 6; // 3 words in header
		}
		return pos;
	}
	
	
	public long getChannel1Position()
	{
		long pos = 0;
		
		if (channel1Hit)
		{
			pos += binaryStartPosition;
			pos += 6; // 3 words in header
		}
		
		else if (channel1Hit && channel0Hit)
			pos += 4; // 2 words in channel
		
		return pos;
	}
	
	public long getChannel2Position()
	{
		long pos = 0;
		
		if (channel2Hit)
		{
			pos += binaryStartPosition;
			pos += 6; // 3 words in header
		}
		
		else if (channel2Hit && channel0Hit)
			pos += 4; // 2 words in channel
		
		else if (channel2Hit && channel1Hit)
			pos += 4; // 2 words in channel
		
		return pos;
	}
	
	public long getChannel3Position()
	{
		long pos = 0;
		
		if (channel3Hit)
		{
			pos += binaryStartPosition;
			pos += 6; // 3 words in header
		}
		
		else if (channel3Hit && channel0Hit)
			pos += 4; // 2 words in channel
		
		else if (channel3Hit && channel1Hit)
			pos += 4; // 2 words in channel
		
		else if (channel3Hit && channel2Hit)
			pos += 4; // 2 words in channel
		
		return pos;
	}
	
}
