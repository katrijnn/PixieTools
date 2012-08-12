package com.pixietools;

public class MatArray {

	public static void main(String[] args) 
	{
		String binFilePath = "";
		
		
		
		PixieBinFile myFile = new PixieBinFile(binFilePath);
		
		int[][] dataMatrix = new int[4096][4096];
		
		// Zero out matrix
		for (int i = 0; i < dataMatrix.length; i++)
		{
			for (int j = 0; j < dataMatrix[i].length; j++)
			{
				dataMatrix[i][j] = 0;
			}
		}
		
		// Determine which channels to compare
		int ch0 = 0;
		int ch1 = 1;
		
		int time = 20;
		
		
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
						int chanTime = myFile.getEventTime();
	
						
						if chanNum == 0
						{
							myFile.markPosition();
							int timeDifference = myFile.getEventTime() + time;
							if timeDifference <= chanTime
								dataMatrix[chanEnergy][]
							else 
								continue;
							myFile.rollbackPosition();
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
		
		
		// iterate each event in PixieBinFile
		// for any event that has ch1 hit, iterate from that point
		// for time = x to find every event that ch2 hit and increment that value by 1.
		//
		// Matrix is dataMatrix[ch1 energy][ch2 energy]
		// 
		// Example dataMatrix[ch1EnergyHit][ch2energyhit]++;
		// once you reach time > x. return to original position in PixieBinFile
		// and proceed to next events looking for a hit on ch1 or ch2 and repeating 
		// procedure above.  
		
		// Note:  all your energies should be scaled down from 32k~ channels to 4k~ channels.  
		//        you can copy the procedure in the HistArray class for that, it will be identical
		
		
		// Once you complete that procedure, you need to write the matrix to a binary
		// file in a specific form.  You should look at how it is done for radware.  Radware
		// uses 4096 by 4096 * 2 bytes.  Here is the website http://radware.phy.ornl.gov/faq.html#8.1
	}

	
	private boolean getCompressedMatrix(int[][] oldMatrix, int[][] newMatrix)
	{
		// Define valid sizes
		int[] validNewBinSizes = {1024, 2048, 4096};
		boolean validBinSize = false;
		
		// Only compress matrices that were originally 32768 (for now)
		if (oldMatrix.length != 32768)
			return false;
		
		// Check all possible values of newBinSize
		for (int i = 0; i < validNewBinSizes.length; i++)
		{
			if (validNewBinSizes[i] == newMatrix.length)
			{
				validBinSize = true;
				break;
			}
		}

		// If not valid in bin size, return false
		if (!validBinSize)
			return false;
		
		// Zero out newHistogram
		for (int i = 0; i < newMatrix.length; i++)
		{
			for (int j=0; j < newMatrix[i].length; j++)
			{
				newMatrix[i][j] = 0;
			}
			
		}
		
		// Get scaleFactor
		double scaleFactor = ((double) newMatrix.length) / ((double) oldMatrix.length);
		
		// Create new histogram
		for (int i = 0; i < oldMatrix.length; i++)
		{
			int newBini = (int)Math.floor(scaleFactor * (double)i);
			int newBinj = (int)Math.floor(scaleFactor * (double)j);
			newMatrix[newBini][newBinj] += oldMatrix[i][j];
		}
		
		return true;
	}

}
