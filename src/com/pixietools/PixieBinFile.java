package com.pixietools;

import java.io.*;
//import java.util.*;
//import pixietools.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/* This program reads the information about each buffer, event and channel hit
 * and stores them all in variables. These variables are used later in functions 
 * to determine the energy and time of a channel hit; also other information such
 * as module number, channel number, etc. 
 * Each function has a short descriptor of its purpose
 */


public class PixieBinFile 
{
	// set up to get file path, and use random access
	// so we can move around within the file
	private String _pixieFilePath = "";
	private RandomAccessFile _pixieFile = null;
	private boolean _fileLoaded = false;
	
	// initialize all headers
	private BufferHeader _currBufferHeader = null;
	private EventHeader _currEventHeader = null;
	private ChannelHeader _currChannelHeader = null;
	
	// Mark / Roll back headers
	private BufferHeader _markBufferHeader = null;
	private EventHeader _markEventHeader = null;
	private ChannelHeader _markChannelHeader = null;
	private long _markFilePosition = 0;
	
	
	
	PixieBinFile()
	{
		// Do nothing
	}
	
	PixieBinFile(String filePath)
	{
		// Overload constructor just in-case want to load this way
		_pixieFilePath = filePath;
	}
	
	private boolean loadFile()
	{
		// Check to see if file path has "something"
		if (_pixieFilePath == "")
			return false;
		
		// Create file object
		File f = new File(_pixieFilePath);
		
		// Check to see if file exists, if not return false
		if (!f.exists())
			return false;
		
		// Create a random access file object, allows us to start at any point in file
		try 
		{
			_pixieFile = new RandomAccessFile(f, "r");
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Error: FileNotFoundException in loadFile: " + e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	public boolean open()
	{
		// First check to see if the file is already open
		if (_fileLoaded)
			return true;
		
		// If not, load file and return true if succeeds 
		if (loadFile())
		{
			_fileLoaded = true;
			return true;
		}
		else
		{
			_fileLoaded = false;
			return false;
		}
	}
	
	
	public boolean close()
	{
		// Check to see if file is open.  
		// If so, close it gracefully and destroy the object
		if (_fileLoaded)
		{
			try 
			{
				// initialize variables on current readings of headers
				_pixieFile.close();
				_pixieFile = null;
				_fileLoaded = false;
				
				// set all headers to 0
				_currBufferHeader = null;
				_currEventHeader = null;
				_currChannelHeader = null;
			} 
			catch (IOException e)
			{
				System.out.println("Error: IOException in close: " + e.getMessage());
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isOpen()
	{
		// check if file is open and loaded
		return _fileLoaded;
	}
	
	public String getFilePath()
	{
		// gets file path from BinToAscii
		return _pixieFilePath;
	}
	
	// sets file path
	public void setFilePath(String filePath)
	{
		_pixieFilePath = filePath;
	}
	
	private boolean readBufferHeader(BufferHeader buffHead)
	{
		try
		{
			// read in all info in buffer header from start of binary position
			buffHead.binaryStartPosition = _pixieFile.getFilePointer();
			
			// since in binary, reading in unsigned shorts in little endian
			buffHead.wordsInBuffer = readUShortLE(_pixieFile);
			buffHead.moduleNumber = readUShortLE(_pixieFile);
			buffHead.bufferFormat = readUShortLE(_pixieFile);
			buffHead.bufferTimeHi = readUShortLE(_pixieFile);
			buffHead.bufferTimeMid = readUShortLE(_pixieFile);
			buffHead.bufferTimeLo = readUShortLE(_pixieFile);
			
			
		}
		catch (IOException e)
		{
			System.out.println("Error: IOException in readBufferHeader: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean readEventHeader(EventHeader eventHead)
	{
		try
		{
			// find where file pointer is at current event
			eventHead.binaryStartPosition = _pixieFile.getFilePointer();
			
			// read in three words in little endian (time, and hit pattern)
			// hit pattern gives what channel was hit, and possible coincidence
			// data if that is requested in Pixie
			eventHead.eventPattern = readUShortLE(_pixieFile);
			eventHead.eventTimeHi = readUShortLE(_pixieFile);
			eventHead.eventTimeLo = readUShortLE(_pixieFile);
			
			// Determine which channels were hit from event pattern
			if ((eventHead.eventPattern & 1) > 0)
				eventHead.channel0Hit = true;
			
			if ((eventHead.eventPattern & 2) > 0)
				eventHead.channel1Hit = true;
			
			if ((eventHead.eventPattern & 4) > 0)
				eventHead.channel2Hit = true;
			
			if ((eventHead.eventPattern & 8) > 0)
				eventHead.channel3Hit = true;
				
		}
		catch (IOException e)
		{
			System.out.println("Error: IOException in readEventHeader: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean readChannelHeader(ChannelHeader chanHead, int chanNumber)
	{
		// This function assumes we are reading a channel header for
		// compression mode 3.  This will have to be modified if 
		// a different mode is used.
		
		try
		{
			// read in all info in channel header
			chanHead.binaryStartPosition = _pixieFile.getFilePointer();
			
			// read in channel number, fast trigger time and pulse height in little endian
			chanHead.channelNumber = chanNumber;
			chanHead.channelTrigTime = readUShortLE(_pixieFile);
			chanHead.channelEnergy = readUShortLE(_pixieFile);
		}
		catch (IOException e)
		{
			System.out.println("Error: IOException in readChannelHeader: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean moveFirstBuffer()
	{
		if (!_fileLoaded)
			return false;
		
		try 
		{
			// Reset event and channel headers
			_currEventHeader = null;
			_currChannelHeader = null;
			
			// Move to beginning of stream
			_pixieFile.seek(0);
			
			// Read first buffer header
			_currBufferHeader = new BufferHeader();
			readBufferHeader(_currBufferHeader);

			// Check to see if buffer is past end of file
			if ((_currBufferHeader.wordsInBuffer * 2) > _pixieFile.length())
			{
				System.out.println("Error: words in first buffer exceed end of file!");
				return false;
			}
		}
		catch (IOException e)
		{
			System.out.println("Error: IOException in moveFirstBuffer: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean moveNextBuffer()
	{
		if (!_fileLoaded)
			return false;
		
		try 
		{
			// Reset event and channel headers
			_currEventHeader = null;
			_currChannelHeader = null;
			
			// calculate position of next buffer based on start position
			// and how where file started (*2 because each word is 2 bytes)
			long posNextBuffer = 0;
			posNextBuffer += _currBufferHeader.binaryStartPosition;
			posNextBuffer += _currBufferHeader.wordsInBuffer * 2;
			
			// Check to see if position is past end of file
			if ((posNextBuffer + 12) > _pixieFile.length())
			{
				_currBufferHeader = null;
				return false;
			}
			
			// get to next buffer
			_pixieFile.seek(posNextBuffer);
			
			// Read buffer header
			_currBufferHeader = new BufferHeader();
			readBufferHeader(_currBufferHeader);
		}
		catch (IOException e)
		{
			System.out.println("Error: IOException in moveNextBuffer: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean moveFirstEvent()
	{
		if (!_fileLoaded)
			return false;
		
		try 
		{
			// Reset channel header
			_currChannelHeader = null;
			
			// Get position for first event
			long posFirstEvent = 0;
			posFirstEvent += _currBufferHeader.binaryStartPosition;
			posFirstEvent += 12; // Ignore first 6 words (buffer header)
			
			// Move to first event in buffer
			_pixieFile.seek(posFirstEvent);
			
			// Read first event
			// **add code here to check within buffer limit**
			_currEventHeader = new EventHeader();
			readEventHeader(_currEventHeader);
		} 
		catch (IOException e) 
		{
			System.out.println("Error: IOException in moveFirstEvent: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean moveNextEvent()
	{
		if (!_fileLoaded)
			return false;
		
		try 
		{
			// Reset channel header
			_currChannelHeader = null;
			
			// Get position for next event
			long posNextEvent = 0;
			posNextEvent += _currEventHeader.binaryStartPosition;
			posNextEvent += 6; // 3 words in event header
			posNextEvent += _currEventHeader.getChannelHitCount() * 4; // 2 Words per channel (Compression 3 Mode)
			
			// Check to see next event is in the buffer
			long bufferMax = _currBufferHeader.binaryStartPosition + (_currBufferHeader.wordsInBuffer * 2);
			if ((posNextEvent + 6) > bufferMax)
			{
				_currEventHeader = null;
				return false;
			}
			
			// Move to next event in buffer
			_pixieFile.seek(posNextEvent);	
			
			// Read next event
			_currEventHeader = new EventHeader();
			readEventHeader(_currEventHeader);
		} 
		catch (IOException e) 
		{
			System.out.println("Error: IOException in moveNextEvent: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean moveFirstChannel()
	{
		if (!_fileLoaded)
			return false;
		
		try 
		{
			//System.out.println("got here");
			// Get position for first channel in event
			long posFirstChannel = 0;
			if (_currEventHeader.channel0Hit)		
				posFirstChannel = _currEventHeader.getChannel0Position();
			else if (_currEventHeader.channel1Hit)
				posFirstChannel = _currEventHeader.getChannel1Position();
			else if (_currEventHeader.channel2Hit)
				posFirstChannel = _currEventHeader.getChannel2Position();
			else if (_currEventHeader.channel3Hit)
				posFirstChannel = _currEventHeader.getChannel3Position();
			else
				return false; // should never get here
			// Move to first channel in event
			_pixieFile.seek(posFirstChannel);	
				

			// Read first channel in event
			_currChannelHeader = new ChannelHeader();
			if (_currEventHeader.channel0Hit)
				readChannelHeader(_currChannelHeader, 0);
			else if (_currEventHeader.channel1Hit)
				readChannelHeader(_currChannelHeader, 1);
			else if (_currEventHeader.channel2Hit)
				readChannelHeader(_currChannelHeader, 2);
			else if (_currEventHeader.channel3Hit)
				readChannelHeader(_currChannelHeader, 3);
			else
				return false; // should never get here

		} 
		catch (IOException e) 
		{
			System.out.println("Error: IOException in moveFirstChannel: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean moveNextChannel()
	{
		// move to next channel in event
		// checks to ensure file is loaded
		if (!_fileLoaded)
			return false;
		
		try 
		{
			// Get position for next channel in event
			long posNextChannel = 0;
			int channelNumber = -1;
			if (_currEventHeader.channel0Hit && (_currEventHeader.getChannel0Position() > _currChannelHeader.binaryStartPosition))
			{
				// should never hit channel 0 for moveNextChannel
				posNextChannel = _currEventHeader.getChannel0Position();
				channelNumber = 0;
			}
			else if (_currEventHeader.channel1Hit && (_currEventHeader.getChannel1Position() > _currChannelHeader.binaryStartPosition))
			{
				posNextChannel = _currEventHeader.getChannel1Position();
				channelNumber = 1;
			}
			else if (_currEventHeader.channel2Hit && (_currEventHeader.getChannel2Position() > _currChannelHeader.binaryStartPosition))
			{
				posNextChannel = _currEventHeader.getChannel2Position();
				channelNumber = 2;
			}
			else if (_currEventHeader.channel3Hit && (_currEventHeader.getChannel3Position() > _currChannelHeader.binaryStartPosition))
			{
				posNextChannel = _currEventHeader.getChannel3Position();
				channelNumber = 3;
			}
			else
				return false;
			
			// Move to first channel in event
			_pixieFile.seek(posNextChannel);	
					
			// Read next channel in event
			_currChannelHeader = new ChannelHeader();
			readChannelHeader(_currChannelHeader, channelNumber);
		} 
		catch (IOException e) 
		{
			System.out.println("Error: IOException in moveNextChannel: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public int getBufferModuleNumber()
	{
		// get module number (which pixie card being used)
		if (!_fileLoaded)
			return -1;
		// returns module number as integer
		return (int)_currBufferHeader.moduleNumber;
	}
	
	public double getBufferTime()
	{
		// calculating buffer time using words High, Mid, Low
		if (!_fileLoaded)
			return -1.0;
		
		// returns time at which buffer was created
		double highTime = _currBufferHeader.bufferTimeHi * (57.2662306133);
		double midTime = _currBufferHeader.bufferTimeMid * (0.0008738133);
		double lowTime = _currBufferHeader.bufferTimeLo * (0.0000000133);
		
		return highTime + midTime + lowTime;
	}
	
	public double getBufferTimeNs()
	{
		// This function converts the Seconds to Nanoseconds
		double bufferTimeS = getBufferTime();
		
		// will never get a negative buffertime, so this means there's an error
		if (bufferTimeS == -1.0)
			return -1.0;
		
		return bufferTimeS * (1000000000.0);
	}
	
	public double getEventTime()
	{
		// calculating event time using buffer High, event high, event low
		if (!_fileLoaded)
			return -1.0;
		
		// convert buffer words over to actual time format
		double highTime = _currBufferHeader.bufferTimeHi * (57.2662306133);
		double midTime = _currEventHeader.eventTimeHi * (0.0008738133);
		double lowTime = _currEventHeader.eventTimeLo * (0.0000000133);
		
		// adding three words together to get absolute time stamp
		return highTime + midTime + lowTime;
	}
	
	public double getEventTimeNs()
	{
		// This function converts the Seconds to Nanoseconds
		double eventTimeS = getEventTime();
		
		if (eventTimeS == -1.0)
			return -1.0;
		
		return eventTimeS * (1000000000.0);
	}
	
	public double getChannelFastTriggerTime()
	{
		// calculating event time using buffer High, event high, event low
		if (!_fileLoaded)
			return -1.0;
				
		double highTime = _currBufferHeader.bufferTimeHi * (57.2662306133);
		double midTime = _currEventHeader.eventTimeHi * (0.0008738133);
		double lowTime = _currChannelHeader.channelTrigTime * (0.0000000133);
		
		// see PixieDataCollection Guide for more info on difference
		// between FastTriggerTime and EventTime
		return highTime + midTime + lowTime;
	}
	
	public double getChannelFastTriggerTimeNs()
	{
		// This function converts the Seconds to Nanoseconds
		double channelTimeS = getChannelFastTriggerTime();
		
		if (channelTimeS == -1.0)
			return -1.0;
		
		return channelTimeS * (1000000000.0);
	}
	
	public int getEventChannel()
	{
		// this function returns the event channel number
		if (!_fileLoaded)
			return -1;
		
		return _currChannelHeader.channelNumber;
	}
	
	public int getEventEnergy()
	{
		// this function returns the event energy reading
		if (!_fileLoaded)
			return -1;
		
		return _currChannelHeader.channelEnergy;
	}
	
	private int readUShortLE(RandomAccessFile _ranAccessFile)
	{
		// this function allows PixieBinFile to read the data in little endian (normal for Java)
		int returnValue = -1;
		
		try
		{
			// Read two bytes into byte array
			// We do it this way so we don't slam the garbage collector
			byte[] buffer = new byte[2];
			int bytes = _ranAccessFile.read(buffer);
			
			// Check to see that it read all 2 bytes
			if (bytes != 2)
				throw new IOException("Unexpected End of Stream");
			
			// ANDs every bit giving you the absolute value in bits
			// turning every negative number into the proper unsigned value	
			returnValue = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
		}
		catch (IOException e) 
		{
			System.out.println("Error: IOException in readUShortLittleEndian: " + e.getMessage()); 
			return -1;
		}
		
		return returnValue;
	}
	
	// function to mark position of current event 
	// so it can be returned to later
	public void markPosition()
	{
		try
		{
			_markBufferHeader.copy(_currBufferHeader);
			_markEventHeader.copy(_currEventHeader);
			_markChannelHeader.copy(_currChannelHeader);
			_markFilePosition = _pixieFile.getFilePointer();
		}
		catch (IOException e)
		{
			System.out.println("Error in markPosition() " + e.getMessage());
		}
	}
	
	// function to roll back to whatever position/event
	// was marked by markPosition function
	public void rollbackPosition()
	{
		try
		{
			_currBufferHeader.copy(_markBufferHeader);
			_currEventHeader.copy(_markEventHeader);
			_currChannelHeader.copy(_markChannelHeader);
			_pixieFile.seek(_markFilePosition);
		}
		catch (IOException e)
		{
			System.out.println("Error in rollbackPosition() " + e.getMessage());
		}
	}
	
}
