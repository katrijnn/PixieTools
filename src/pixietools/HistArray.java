package pixietools;

import java.io.*;

public class HistArray 
{
	public static void main(String[] args)
	{
		// Example of how to export a spectrum for ch0 and ch1 with 1024 bins
		
		HistArray newHistArray = new HistArray();
		
		int[] ch0 = new int[32768];
		int[] ch1 = new int[32768];
		int[] ch2 = new int[32768];
		int[] ch3 = new int[32768];
		
		String binFilePath = "/home/gtrees/Desktop/co60.bin";
		
		if (!newHistArray.getHistogram(binFilePath, 0, ch0, ch1, ch2, ch3))
			return;
		
		// For testing, lets just compress ch0 and ch1 and output them
		int[] ch0New = new int[1024];
		int[] ch1New = new int[1024];
		
		if (!newHistArray.getCompressedHistogram(ch0, ch0New))
			return;
		
		if (!newHistArray.getCompressedHistogram(ch1, ch1New))
			return;
		
		String out0Path = "/home/gtrees/Desktop/ch0.txt";
		String out1Path = "/home/gtrees/Desktop/ch1.txt";
		
		newHistArray.writeHistogramToAsciiFile(out0Path, ch0New);
		newHistArray.writeHistogramToAsciiFile(out1Path, ch1New);
		
		System.out.println("Done!");
	}

	private void writeHistogramToAsciiFile(String outPath, int[] histogram)
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream(outPath);
			OutputStreamWriter histFile = new OutputStreamWriter(fos, "UTF-8");
			
			for (int i = 0; i < histogram.length; i++)
			{
				histFile.write(String.valueOf(histogram[i]) + "\n");
			}
			
			histFile.close();
			fos.close();
		} 
		catch (Exception e) 
		{
			System.out.println("An error has occured in writeHistogramToAsciiFile: " + e.getMessage());
		}
	}
	
	private boolean getCompressedHistogram(int[] oldHistogram, int[] newHistogram)
	{
		// Define valid sizes
		int[] validNewBinSizes = {1024, 2048, 4096, 8192, 16384};
		boolean validBinSize = false;
		
		// Only compress histograms that were originally 32768 (for now)
		if (oldHistogram.length != 32768)
			return false;
		
		// Check all possible values of newBinSize
		for (int i = 0; i < validNewBinSizes.length; i++)
		{
			if (validNewBinSizes[i] == newHistogram.length)
			{
				validBinSize = true;
				break;
			}
		}

		// If not valid in bin size, return false
		if (!validBinSize)
			return false;
		
		// Zero out newHistogram
		for (int i = 0; i < newHistogram.length; i++)
		{
			newHistogram[i] = 0;
		}
		
		// Get scaleFactor
		double scaleFactor = ((double) newHistogram.length) / ((double) oldHistogram.length);
		
		// Create new histogram
		for (int i = 0; i < oldHistogram.length; i++)
		{
			int newBin = (int)Math.floor(scaleFactor * (double)i);
			newHistogram[newBin] += oldHistogram[i];
		}
		
		return true;
	}

	private boolean getHistogram(String binFilePath, int moduleId, int[] ch0, int[] ch1, int[] ch2, int[] ch3)
	{
		PixieBinFile myFile = new PixieBinFile(binFilePath);
		
		// Open file and return false if open returns false
		if (!myFile.open())
			return false;
		
		// Check to see if moduleId is 0 or 1
		if ((moduleId != 0) && (moduleId != 1))
			return false;
		
		// Check to see arrays are correct size
		if (ch0.length != 32768) return false;
		if (ch1.length != 32768) return false;
		if (ch2.length != 32768) return false;
		if (ch3.length != 32768) return false;
	
		// Zero out arrays
		for (int i=0; i < ch0.length; i++)
		{
			ch0[i] = 0;
		}
		
		for (int i=0; i < ch1.length; i++)
		{
			ch1[i] = 0;
		}
		
		for (int i=0; i < ch2.length; i++)
		{
			ch2[i] = 0;
		}
		
		for (int i=0; i < ch3.length; i++)
		{
			ch3[i] = 0;
		}
		
		// Add +1 for respective channel and bin
		try 
		{	
			for (boolean bBuffer = myFile.moveFirstBuffer(); bBuffer; bBuffer = myFile.moveNextBuffer())
			{
				// Check to see if this buffer belongs to moduleId
				if (myFile.getBufferModuleNumber() != moduleId)
					continue;
				
				for (boolean bEvent = myFile.moveFirstEvent(); bEvent; bEvent = myFile.moveNextEvent())
				{
					for (boolean bChannel = myFile.moveFirstChannel(); bChannel; bChannel = myFile.moveNextChannel())
					{
						int chanNum = myFile.getEventChannel();
						int chanEnergy = myFile.getEventEnergy();
	
						if (chanEnergy < 32768)
						{
							if (chanNum == 0)
							{
								ch0[chanEnergy]= ch0[chanEnergy] + 1;
							}
								
							else if (chanNum == 1)
							{
								ch1[chanEnergy] = ch1[chanEnergy] + 1;
							}
								
							else if (chanNum == 2)
							{
								ch2[chanEnergy] = ch2[chanEnergy] + 1;
							}
								
							else if (chanNum == 3)
							{
								ch3[chanEnergy] = ch3[chanEnergy] + 1;
							}
						}
						else 
						{
							continue;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("An error has occured in getHistogram: " + e.getMessage());
			myFile.close();
			return false;
		}
		
		myFile.close();
		
		return true;
	}
}