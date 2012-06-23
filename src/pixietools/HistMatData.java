package pixietools;

//import pixietools.*;

//import java.util.*;
import java.io.*;


public class HistMatData
{

	public static void main(String[] args) 
	{		
		// insert bin file name here
		PixieBinFile myFile = new PixieBinFile("C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\test_file_0015.bin");
		myFile.open();
		
		try 
		{
			System.out.println("Starting read...");
			
			// insert file output name here
			FileOutputStream fos1 = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\hist_mat_data.txt");
			OutputStreamWriter out1 = new OutputStreamWriter(fos1, "UTF-8");
					
			// Create header
			out1.write("Channel\tEnergy (bin)" + "\n");
			out1.write("-------------------------------");
			out1.write("\n\n");

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

						out1.write(String.valueOf(myFile.getEventChannel()));
						out1.write("\t\t");
						out1.write(String.valueOf(myFile.getEventEnergy()));
						out1.write("\n");
					}
				}
			}
			out1.close();
					
			System.out.println("Finished...");
		}
		catch (Exception e) 
		{
			System.out.println("An error has occured!");
		}
		
		myFile.close();
	}
}
