package pixietools;

import java.io.*;

public class HistArray 
{
	public static void main(String[] args)
	{
		try
		{
			HistArray newHistArray = new HistArray();
			
			int[] ch0 = new int[32768];
			int[] ch1 = new int[32768];
			int[] ch2 = new int[32768];
			int[] ch3 = new int[32768];
			
	
			String binFilePath = "C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\co60.bin";
	
			
			if (!newHistArray.getHistogram(binFilePath, 0, ch0, ch1, ch2, ch3))
				return;
			
	
			int newBinSize = 32768;
			String newBin_line = null;
			
			System.out.println("Please enter new Bin Size (1024, 2048, 4096, 8192, 16384) " + "\n");
			BufferedReader newBinSize_reader = new BufferedReader(new InputStreamReader(System.in));
			newBin_line = newBinSize_reader.readLine();
			newBinSize = Integer.parseInt(newBin_line);
			
			int[] ch0New = new int[newBinSize];
			int[] ch1New = new int[newBinSize];
			int[] ch2New = new int[newBinSize];
			int[] ch3New = new int[newBinSize];
	
			
			if (!newHistArray.getCompressedHistogram(ch0, ch0New))
				return;	
			if (!newHistArray.getCompressedHistogram(ch1, ch1New))
				return;
			if (!newHistArray.getCompressedHistogram(ch2, ch2New))
				return;
			if (!newHistArray.getCompressedHistogram(ch3, ch3New))
				return;
			
	
			String out0Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\ch0.txt";
			String out1Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\ch1.txt";
			String out2Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\ch2.txt";
			String out3Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\ch3.txt";
			
			String userNum_line = null;
			int userChanNum = 0;
			
			System.out.println("Please enter channel number between 0 and 3");
			BufferedReader userChanNum_reader = new BufferedReader(new InputStreamReader(System.in));
			userNum_line = userChanNum_reader.readLine();
			userChanNum = Integer.parseInt(userNum_line);
			
			if (userChanNum == 0)
					newHistArray.writeHistogramToAsciiFile(out0Path, ch0New);
			else if (userChanNum == 1)
					newHistArray.writeHistogramToAsciiFile(out1Path, ch1New);
			else if (userChanNum == 2)
					newHistArray.writeHistogramToAsciiFile(out2Path, ch2New);
			else if (userChanNum == 3)
					newHistArray.writeHistogramToAsciiFile(out3Path, ch3New);
	
			System.out.println("Done!");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
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
								ch0[chanEnergy]++;
							}	
							else if (chanNum == 1)
							{
								ch1[chanEnergy]++;
							}	
							else if (chanNum == 2)
							{
								ch2[chanEnergy]++;
							}
							else if (chanNum == 3)
							{
								ch3[chanEnergy]++;
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