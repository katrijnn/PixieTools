package com.pixietools;

public class ChannelHeader 
{
	// This channel header class is assuming "Compression 3 Format"  
	// If need to use other modes, this will need to be extended.
	public int channelNumber = -1;
	public int channelTrigTime = 0;
	public int channelEnergy = 0;
	
	public long binaryStartPosition = 0;
	
	public void copy(ChannelHeader ch)
	{
		this.channelNumber = ch.channelNumber;
		this.channelTrigTime = ch.channelTrigTime;
		this.channelEnergy = ch.channelEnergy;
		this.binaryStartPosition = ch.binaryStartPosition;
	}
}
