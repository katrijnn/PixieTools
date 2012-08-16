package com.pixietools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.Math;
import java.io.InputStreamReader;

/*
This program requests two channel numbers and a coincidence window length (in ns) 
from the user. It then iterates through the data file. When it finds an event
in the first channel, it marks that spot and keeps iterating. When it finds an 
event in the second channel, it looks to see if the time the second event occurred is
coincident to the time the first event occurred (if the coincidence window is smaller
than the difference in time-stamps)
If it is within the coincidence window, it increments the elements in the matrix that
correspond to the energies of those two events in the two channels by 1
It then "rolls back" to the original event, and continues searching other events to see
if they are coincident to the event in the first channel
When the matrix (4069 x 4069 elements) is filled, the program writes it to a file in binary
*/


public class MatArray 
{
	int _matrixBins = 4096; // default
	String _dataFilePath = "";
	
	public static void main(String[] args) 
	{
		String binFilePath = "C://Users//kaatrin.a.netherton//Desktop//PixieTestFiles//co60.bin";
		
		MatArray matar = new MatArray();
	
		// Initialize channels to compare
		// Initialize coincidence window;
		int userChanNum1 = 0;
		String userNum_line1 = null;
		
		int userChanNum2 = 0;
		String userNum_line2 = null;
		
		int coinWindow = 0;
		String coinWindow_line = null;
		
		// initialize variables to be used in loops
		double chNum1Time = 0;
		double chNum2Time = 0;
		double chNum1Energy = 0;
		double chNum2Energy = 0;
		
		// scaleFactor allows the matrix to be 
		//shrunk to a point where it does not
		// produce terabytes of data
		int scaleFactor = 1/8;
		
		// moduleId is the pixie card number
		int moduleId = 0;

		try 
		{	
			// figure out coincidence window -- in increments of 13.333ns?
			System.out.println("Please enter desired coincidence window in nanoseconds: ");
			BufferedReader coinWindow_reader = new BufferedReader(new InputStreamReader(System.in));
			coinWindow_line = coinWindow_reader.readLine();
			coinWindow = Integer.parseInt(coinWindow_line);
			
			// get first channel to view
			System.out.println("Please enter first channel number (between 0 and 3)");
			BufferedReader userChanNum_reader1 = new BufferedReader(new InputStreamReader(System.in));
			userNum_line1 = userChanNum_reader1.readLine();
			userChanNum1 = Integer.parseInt(userNum_line1);
			
			// get second channel to view
			System.out.println("Please enter second channel number (between 0 and 3");
			BufferedReader userChanNum_reader2 = new BufferedReader(new InputStreamReader(System.in));
			userNum_line2 = userChanNum_reader2.readLine();
			userChanNum2 = Integer.parseInt(userNum_line2);

			
			matar.getDetectorVsDetectorMatrix(userChanNum1, userChanNum2, moduleId, coinWindow);
			
			//matar.writeDataMatrixToFile(dataMatrix);
		}
		catch (Exception e)
		{
			System.out.println("An error has occured in getHistogram: " + e.getMessage());
			//dataFile.close();
		}
		
		//dataFile.close();
		
		
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
	
	public int getMatrixBins() 
	{
		return _matrixBins;
	}

	public void setMatrixBins(int matrixBins) 
	{
		this._matrixBins = matrixBins;
	}
	
	public double getEnergyScale()
	{
		return (double)_matrixBins / (double)32768;
	}
	
	public String getDataFilePath()
	{
		return _dataFilePath;
	}
	
	public void setDataFilePath(String dataFilePath)
	{
		this._dataFilePath = dataFilePath;
	}
	
	public int[][] getDetectorVsDetectorMatrix(int ch1, int ch2, int module, double coinWindow)
	{
		int[][] dataMatrix = null;
		int[] validMatrixBins = {4096};
		boolean matrixBinsValid = false;
		double energyScale = 0;
		boolean iterateForward = false;
		boolean iterateReturn = false;
		int iterateChannel = -1;
		double iterateStart = 0.0;
		int iterateEnergy = 0;
		
		// Check to see if _matrixBins is valid
		for (int i = 0; i < validMatrixBins.length; i++)
		{
			if (validMatrixBins[i] == _matrixBins)
			{
				matrixBinsValid = true;
				break;
			}
		}
		
		// Check to see if valid size
		if (!matrixBinsValid)
			return dataMatrix;
		
		energyScale = getEnergyScale();
		
		// Create matrix of proper size
		dataMatrix = new int[_matrixBins][_matrixBins];
		
		// Zero out matrix
		for (int i = 0; i < dataMatrix.length; i++)
		{
			for (int j = 0; j < dataMatrix[i].length; j++)
			{
				dataMatrix[i][j] = 0;
			}
		}
		
		// Get data file
		PixieBinFile dataFile = new PixieBinFile(_dataFilePath);
		
		// Iterate through every event and build matrix
		for (boolean bBuffer = dataFile.moveFirstBuffer(); bBuffer; bBuffer = dataFile.moveNextBuffer())
		{
			// Check to see if this buffer belongs to moduleId
			if (dataFile.getBufferModuleNumber() != module)
				continue;
			
			for (boolean bEvent = dataFile.moveFirstEvent(); bEvent; bEvent = dataFile.moveNextEvent())
			{
				for (boolean bChannel = dataFile.moveFirstChannel(); bChannel; bChannel = dataFile.moveNextChannel())
				{
					// If returning from an iteration, skip to next event
					if (iterateReturn)
					{
						iterateReturn = false;
						continue;
					}
					
					int chanNum = dataFile.getEventChannel();
					double eventTime = dataFile.getBufferTime();

					// Get info for first hit
					if (!iterateForward && (chanNum == ch1 || chanNum == ch2))
					{
						iterateChannel = chanNum;
						iterateForward = true;
						iterateStart = dataFile.getBufferTime();
						iterateEnergy = dataFile.getEventEnergy();
						dataFile.markPosition();
						continue;
					}
					
					// Check to see if in time window
					if (iterateForward && ((eventTime - iterateStart) > coinWindow))
					{
						iterateReturn = true;
						iterateForward = false;
						iterateChannel = -1;
						iterateStart = 0;
						iterateEnergy = 0;
						
						dataFile.rollbackPosition();
						
						continue;
					}
					
					if (iterateForward && 
						chanNum == ch2 && 
						iterateChannel == ch1)
					{
						int energyCh1 = (int)Math.floor(energyScale * (double)iterateEnergy);
						int energyCh2 = (int)Math.floor(energyScale * (double)dataFile.getEventEnergy());
						
						dataMatrix[energyCh1][energyCh2]++;
					}
					
					if (iterateForward && 
						chanNum == ch1 && 
						iterateChannel == ch2)
					{
						int energyCh1 = (int)Math.floor(energyScale * (double)dataFile.getEventEnergy());
						int energyCh2 = (int)Math.floor(energyScale * (double)iterateEnergy);
						
						dataMatrix[energyCh1][energyCh2]++;
					}
					
					
					// We will still need to handle the case where we hit the end of the file....
				}	
			}	
		}
		dataFile.close();
		
		return dataMatrix;
	}
	
	public void writeDataMatrixToFile(int[][] dataMatrix)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream("C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\dataMatrix_out.txt");
			OutputStreamWriter matOut = new OutputStreamWriter(fos, "UTF-8");
			
			for (int i=0; i < dataMatrix.length; i++)
			{
				for (int j=0; j < dataMatrix[i].length; j++)
				{
					matOut.write(String.valueOf(dataMatrix[i][j]) + " ");

				}
				matOut.write("\n");
			}
			matOut.close();


		}
		catch (Exception e)
		{
			System.out.println("Error ocurred in writeMatrixToFile " + e.getMessage());
		}
	}
	
	private int scaleEnergy(double scaleFactor, int energy)
	{
		return energy;
	}
	
}
