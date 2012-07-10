package com.pixietools;

//import pixietools.*;

import java.text.DecimalFormat;

import java.io.*;


public class BinToStack 
{

	public static void main(String[] args) 
	{		
		// insert bin file name here
		PixieBinFile myFile = new PixieBinFile("C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\test_file_0004.bin");
		// for testing use test_file_0010.bin 
		// this file is a ramp-up of voltages, there is not error within it (some test files have errors)
		myFile.open();
		
		int[] stackArray = new int[32768];

		
		try 
		{
			System.out.println("Starting read...");
			
			// insert file output name here
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\stackOut.txt");
			OutputStreamWriter stackOut = new OutputStreamWriter(fos, "UTF-8");
					
			
			// insert file output here
			FileOutputStream fos1 = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\stackHist_out.txt");
			OutputStreamWriter stackHistOut = new OutputStreamWriter(fos1, "UTF-8");
			
			// Create header
			stackHistOut.write("Energy Histogram Data" + "\n");
			stackHistOut.write("----------------------");
			stackHistOut.write("\n\n");
			
			// Create header
			stackOut.write("Event Time Difference (Ns)\tEnergy (bin)" + "\n");
			stackOut.write("----------------------------------------------------------------------");
			stackOut.write("\n\n");

			double linacHitTime = 0;
			
			// Uncomment line below when limiting number of buffers to output
			// long bufferCount = 0;
			
			for (boolean bBuffer = myFile.moveFirstBuffer(); bBuffer; bBuffer = myFile.moveNextBuffer())
			{
				// Uncomment line below when limiting number of buffers to output
				// bufferCount++;
				
				//Uncomment line below to limit number of buffers output
				 //if (bufferCount >= 1) break;
				
				
				// Loop over all events and print them to text file
				for (boolean bEvent = myFile.moveFirstEvent(); bEvent; bEvent = myFile.moveNextEvent())
				{
					// loop over each channel hit in this event
					for (boolean bChannel = myFile.moveFirstChannel(); bChannel; bChannel = myFile.moveNextChannel())
					{
						if (myFile.getEventChannel() == 0)
						{
							linacHitTime = Double.valueOf(myFile.getEventTimeNs());
						}
						DecimalFormat dFormat = new DecimalFormat("#.########");
						
						double timeDifference = (linacHitTime - (Double.valueOf(dFormat.format(myFile.getEventTimeNs()))));
						
						stackOut.write(String.valueOf(myFile.getEventChannel()));
						stackOut.write("\t\t");
						stackOut.write(String.valueOf(timeDifference));
						stackOut.write("\t\t\t");
						stackOut.write(String.valueOf(myFile.getEventEnergy()));
						stackOut.write("\n");
						
						int Energy = myFile.getEventEnergy();
						int chanNum = myFile.getEventChannel();
						
						if (Energy < 32768)
						{
							if (chanNum == 1)
							{
								stackArray[Energy]= stackArray[Energy] + 1;
							}
								
						}
						
					}
				}
			}
			stackOut.close();
					
			System.out.println("Finished...");
		}
		catch (Exception e) 
		{
			System.out.println("An error has occured!");
		}
		
		myFile.close();
	}
}
