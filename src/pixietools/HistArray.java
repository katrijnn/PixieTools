package pixietools;

import java.io.*;
//import java.util.*;

public class HistArray 
{
	public static void main(String[] args)
	{
		PixieBinFile myFile = new PixieBinFile("C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\test_file_0004.bin");
		myFile.open();
	
		int[] ch0Array = new int[32768];
		int[] ch1Array = new int[32768];
		int[] ch2Array = new int[32768];
		int[] ch3Array = new int[32768];
	
		// zero out arrays
		for (int i=0; i < ch0Array.length; i++)
		{
			ch0Array[i] = 0;
		}
		
		for (int i=0; i < ch1Array.length; i++)
		{
			ch1Array[i] = 0;
		}
		
		for (int i=0; i < ch2Array.length; i++)
		{
			ch2Array[i] = 0;
		}
		
		for (int i=0; i < ch3Array.length; i++)
		{
			ch3Array[i] = 0;
		}
		
		try 
		{
			System.out.println("Starting HistArray...");
			String line = null;
			int userChanNum = 0;
			
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\hist_out.txt");
			OutputStreamWriter hist_out = new OutputStreamWriter(fos, "UTF-8");

			// create header
			hist_out.write("Energy Histogram Data" + "\n");
			hist_out.write("---------------------");
			hist_out.write("\n\n");

			// read string input from console
			// depending on which channel user requests
			System.out.println("Please enter channel number between 0 and 3");
			BufferedReader hist_reader = new BufferedReader(new InputStreamReader(System.in));
			line = hist_reader.readLine();
			userChanNum = Integer.parseInt(line);

			
			for (boolean bBuffer = myFile.moveFirstBuffer(); bBuffer; bBuffer = myFile.moveNextBuffer())
			{
				
				// Loop over all events and print them to text file
				for (boolean bEvent = myFile.moveFirstEvent(); bEvent; bEvent = myFile.moveNextEvent())
				{
					// loop over each channel hit in this event
					for (boolean bChannel = myFile.moveFirstChannel(); bChannel; bChannel = myFile.moveNextChannel())
					{
						int chanNum = myFile.getEventChannel();
						int chanEnergy = myFile.getEventEnergy();

						if (chanEnergy < 32768)
						{
							if (chanNum == 0)
							{
								ch0Array[chanEnergy]= ch0Array[chanEnergy] + 1;
							}
								
							else if (chanNum == 1)
							{
								ch1Array[chanEnergy] = ch1Array[chanEnergy] + 1;
							}
								
							else if (chanNum == 2)
							{
								ch2Array[chanEnergy] = ch2Array[chanEnergy] + 1;
							}
								
							else if (chanNum == 3)
							{
								ch3Array[chanEnergy] = ch3Array[chanEnergy] + 1;
							}
							
						}
						
						else 
						{
							continue;
						}
						
					}
				}
			}
			
			// iterate over arrays to write to output file

			if (userChanNum == 0)
			{
				for(int i=0; i < ch0Array.length; i++)
				{
					hist_out.write(String.valueOf(ch0Array[i]) + "\n");
				}
			}
				
			if (userChanNum == 1)
			{
				for(int i=0; i < ch1Array.length; i++)
				{
					hist_out.write(String.valueOf(ch1Array[i]) + "\n");
				}
			}
				
			if (userChanNum == 2)
			{
				for(int i=0; i< ch2Array.length; i++)
				{
					hist_out.write(String.valueOf(ch2Array[i]) + "\n");
				}
			}
				
			if (userChanNum == 3)
			{
				for(int i=0; i < ch3Array.length; i++)
				{
					hist_out.write(String.valueOf(ch3Array[i]) + "\n");
				}
			}
			
			hist_out.close();
			
			
			String compYN_line = null;
			int compYN = 0;
			
			System.out.println("Do you wish to compress this histogram data? Please enter 0 for yes, 1 for no" + "\n");
			BufferedReader compYN_reader = new BufferedReader(new InputStreamReader(System.in));
			compYN_line = compYN_reader.readLine();
			compYN = Integer.parseInt(compYN_line);
			
			if (compYN == 0)
			{
				FileOutputStream fos1 = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\compHist_out.txt");
				OutputStreamWriter compHist_out = new OutputStreamWriter(fos1, "UTF-8");
				
				// create header
				compHist_out.write("Compressed Energy Histogram Data" + "\n");
				compHist_out.write("---------------------");
				compHist_out.write("\n\n");
				
				int[] validNewBinSizes = {1024, 2048, 4096, 8192, 16384};
				boolean validBinSize = false;

				
				String newBin_line = null;
				int newBinSize = 32768;
				
				System.out.println("Please enter new Bin Size (1024, 2048, 4096, 8192, 16384) " + "\n");
				BufferedReader newBinSize_reader = new BufferedReader(new InputStreamReader(System.in));
				newBin_line = newBinSize_reader.readLine();
				newBinSize = Integer.parseInt(newBin_line);
				
				// check all possible values of newBinSize to make sure in array
				for (int i = 0; i < validNewBinSizes[i]; i++)
				{
					if (validNewBinSizes[i] == newBinSize)
						System.out.println("yes");
						continue;	
				}
				System.out.println("got stuck here");

				// if not valid in bin size, return false
				if (!validBinSize)
					validBinSize = false;
				

				// get scale factor
				double scaleFactor = ((double)newBinSize)/32768;
				
			 	// create new Histogram
				int[] compHist = new int[newBinSize];
				for (int i = 0; i < compHist.length; i++)
				{
					compHist[i] = 0;
				}
				
				// convert unscaled histogram to scaled histogram
				if (userChanNum == 0) 
				{

					for (int i = 0; i < ch0Array.length; i++)
					{

						int newBin = (int)Math.floor(scaleFactor * (double)i);
						compHist[newBin] += ch0Array[i];
						for (int j=0; j < compHist.length; j++)
						{
							compHist_out.write(String.valueOf(compHist[j])+ "\n");
						}
					}
				}
				else if (userChanNum == 1)
				{
					for (int i = 0; i < ch1Array.length; i++)
					{
						int newBin = (int)Math.floor(scaleFactor * (double)i);
						compHist[newBin] += ch1Array[i];
						for (int j = 0; i < compHist.length; j++)
						{
							compHist_out.write(String.valueOf(compHist[j]) + "\n");
						}
					}
				}
				else if (userChanNum == 2)
				{
					for (int i = 0; i < ch2Array.length; i++)
					{
						int newBin = (int)Math.floor(scaleFactor * (double)i);
						compHist[newBin] += ch2Array[i];
						for (int j = 0; j < compHist.length; j++)
						{
							compHist_out.write(String.valueOf(compHist[j]) + "\n");

						}
					}
				}
				else if (userChanNum == 3)
				{
					for (int i = 0; i < ch3Array.length; i++)
					{
						int newBin = (int)Math.floor(scaleFactor * (double)i);
						compHist[newBin] += ch3Array[i];
						for (int j = 0; j < compHist.length; j++)
						{
							compHist_out.write(String.valueOf(compHist[j]) + "\n");

						}
					}
				}
				
				compHist_out.close();
			
			}
			else if (compYN == 1)
			{
				System.out.println("Finished HistArray...");
			}
			
			else if (compYN > 1)
			{
				System.out.println("Error! Please enter 0 for yes and 1 for no");
			}
			
		}	
		catch (Exception e)
		{
			System.out.println("An error has occured in arrays!");
		}
		
		myFile.close();
	}
}