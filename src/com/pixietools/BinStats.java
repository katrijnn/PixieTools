package com.pixietools;

public class BinStats
{

	public static void main(String[] args) 
	{
		// insert bin file name here
		PixieBinFile myFile = new PixieBinFile("C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\test_file_0004.bin");
		myFile.open();
		
		try 
		{
			System.out.println("Starting...");
			System.out.println("");
			
			boolean firstBuffer = true;
			double startTime = 0.0;
			double endTime = 0.0;
			double totalTime = 0.0;
			long totalEvents = 0;
			long totalChannelHits = 0;
			long totalBuffers = 0;
			double eventsPerSecond = 0.0;
			double channelHitsPerSecond = 0.0;

			for (boolean bBuffer = myFile.moveFirstBuffer(); bBuffer; bBuffer = myFile.moveNextBuffer())
			{
				totalBuffers++;
				if (firstBuffer)
				{
					startTime = myFile.getBufferTime();
					firstBuffer = false;
				}
				endTime = myFile.getBufferTime();
				
				for (boolean bEvent = myFile.moveFirstEvent(); bEvent; bEvent = myFile.moveNextEvent())
				{				
					totalEvents++;
					for (boolean bChannel = myFile.moveFirstChannel(); bChannel; bChannel = myFile.moveNextChannel())
					{			
						totalChannelHits++;
					}
				}
			}
			
			totalTime = endTime - startTime;
			eventsPerSecond = Math.abs((double)totalEvents / totalTime);
			channelHitsPerSecond = Math.abs((double)totalChannelHits / totalTime);
			
			System.out.println("Total Buffers: " + String.valueOf(totalBuffers));
			System.out.println("Total Events: " + String.valueOf(totalEvents));
			System.out.println("Total Channel Hits: " + String.valueOf(totalChannelHits));
			System.out.println("Start Time: " + String.valueOf(startTime) + " (ns)");
			System.out.println("End Time: " + String.valueOf(endTime) + " (ns)");
			System.out.println("Total Time: " + String.valueOf(totalTime) + " (ns)");
			System.out.println("Events per Second: " + String.valueOf(Math.round(eventsPerSecond)));
			System.out.println("Channel Hits per Second: " + String.valueOf(Math.round(channelHitsPerSecond)));
			
			System.out.println("");
			System.out.println("Finished...");
		}
		catch (Exception e) 
		{
			System.out.println("An error has occured!");
		}
		
		myFile.close();
	}
}
