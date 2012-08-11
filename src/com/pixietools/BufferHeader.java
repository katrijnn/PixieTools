package com.pixietools;

public class BufferHeader 
{
	public int wordsInBuffer = 0;
	public int moduleNumber = 0;
	public int bufferFormat = 0;
	public int bufferTimeHi = 0;
	public int bufferTimeMid = 0;
	public int bufferTimeLo = 0;
	
	public long binaryStartPosition = 0;
	
	public void copy(BufferHeader bh)
	{
		this.wordsInBuffer = bh.wordsInBuffer;
		this.moduleNumber = bh.moduleNumber;
		this.bufferFormat = bh.bufferFormat;
		this.bufferTimeHi = bh.bufferTimeHi;
		this.bufferTimeMid = bh.bufferTimeMid;
		this.bufferTimeLo = bh.bufferTimeLo;
		this.binaryStartPosition = bh.binaryStartPosition;
	}
}
