package pixietools;

//import pixietools.*;

import java.text.DecimalFormat;

//import java.util.*;
import java.io.*;


public class BinToAscii 
{

	public static void main(String[] args) 
	{		
		// insert bin file name here
		PixieBinFile myFile = new PixieBinFile("C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\test_file_0004.bin");
		// for testing use test_file_0010.bin 
		// this file is a ramp-up of voltages, there is not error within it (some test files have errors)
		myFile.open();
		
		try 
		{
			System.out.println("Starting read...");
			
			// insert file output name here
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\out.txt");
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
					
			// Create header
			out.write("Module\tChannel\tEvent Time (Ns)\tEnergy (bin)" + "\n");
			out.write("----------------------------------------------------------------------");
			out.write("\n\n");

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
						DecimalFormat dFormat = new DecimalFormat("#.########");
						
						out.write(String.valueOf(myFile.getBufferModuleNumber()));
						out.write("\t\t");
						out.write(String.valueOf(myFile.getEventChannel()));
						out.write("\t\t");
						out.write(String.valueOf(dFormat.format(myFile.getEventTimeNs())));
						out.write("\t\t\t");
						out.write(String.valueOf(myFile.getEventEnergy()));
						out.write("\n");
					}
				}
			}
			out.close();
					
			System.out.println("Finished...");
		}
		catch (Exception e) 
		{
			System.out.println("An error has occured!");
		}
		
		myFile.close();
	}
}
