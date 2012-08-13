package com.pixietools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
		int userChanNum1 = 0;
		String userNum_line1 = null;
		
		int userChanNum2 = 0;
		String userNum_line2 = null;
		
		int coinWindow = 0;
		String coinWindow_line = null;
		
		// initialize variables to be used in loops
		int coinWind = 20;
		double ch0Time = 0;
		int ch0Energy = 0;
		int moduleId = 0;

		try 
		{	
			System.out.println("Please enter desired coincidence window in nanoseconds: ");
			BufferedReader coinWindow_reader = new BufferedReader(new InputStreamReader(System.in));
			coinWindow_line = coinWindow_reader.readLine();
			coinWindow = Integer.parseInt(coinWindow_line);
			
			// FOR NOW JUST USING CH0, CH1
			System.out.println("Please enter channel number between 0 and 3");
			BufferedReader userChanNum_reader1 = new BufferedReader(new InputStreamReader(System.in));
			userNum_line1 = userChanNum_reader1.readLine();
			userChanNum1 = Integer.parseInt(userNum_line1);
			
			System.out.println("Please enter channel number between 0 and 3");
			BufferedReader userChanNum_reader2 = new BufferedReader(new InputStreamReader(System.in));
			userNum_line2 = userChanNum_reader2.readLine();
			userChanNum2 = Integer.parseInt(userNum_line2);
			
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

						// if hit is ch0, mark position in file, save time and energy
						// then continue iterating through events
						if (chanNum == 0)
						{
							myFile.markPosition();
							ch0Energy = myFile.getEventEnergy();
							ch0Time = myFile.getEventTime();
							//continue;
							
							// PROBLEM: how to continue iterating after first event
							// also, how to skip over event that has already been
							// checked in ch1 (after rollback)
						}
						// if hit is ch1, save time and energy
						// check if it is close enough to event in ch0
						// to be coincident. If so, increment that element
						// in matrix by 1
						// then go back to hit in ch0 to search for other events
						else if (chanNum == 1)
						{
							int ch1Energy = myFile.getEventEnergy();
							double ch1Time = myFile.getEventTime();
							if ((ch1Time - ch0Time) <= coinWind);
							{
								dataMatrix[ch0Energy][ch1Energy]++;
							}			
						}
					}	
				}
						myFile.rollbackPosition();
						continue;
			}
		}
		catch (Exception e)
		{
			System.out.println("An error has occured in getHistogram: " + e.getMessage());
			myFile.close();
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

	/*
	private boolean getCompressedMatrix(int[][] oldMatrix, int[][] newMatrix)
	{
		// Define valid sizes
		int[] validNewBinSizes = {4096};
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
		
		// Create new matrix
		for (int i = 0; i < oldMatrix.length; i++)
		{
			int newBini = (int)Math.floor(scaleFactor * (double)i);
			int newBinj = (int)Math.floor(scaleFactor * (double)j);
			newMatrix[newBini][newBinj] += oldMatrix[i][j];
		}
		
		return true;
	}
	*/
}
