package com.pixietools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.Math;
import java.io.InputStreamReader;

public class MatArrayFin 
{
	int _matrixBins = 4096; // default
	
	public static void main(String[] args)
	{
		MatArrayFin matar = new MatArrayFin();

		String _dataFilePath = "C://Users/kaatrin.a.netherton//Desktop//PixieTestFiles//co60.bin";
		
		// Get data file
		PixieBinFile dataFile = new PixieBinFile(_dataFilePath);
							
		int[][] dataMatrix = null;
		
		
		// Initialize channels to compare
		// Initialize coincidence window;
		
		// moduleId is the pixie card number
		int moduleId = 0;
				
		try
		{
			int userChanNum1 = 0;
			String userNum_line1 = null;
					
			int userChanNum2 = 0;
			String userNum_line2 = null;
					
			int coinWindow = 0;
			String coinWindow_line = null;
			
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
					
			String outPath01 = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Matrix01_out.txt";
			String outPath02 = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Matrix02_out.txt";
			String outPath03 = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Matrix03_out.txt";
			String outPath12 = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Matrix12_out.txt";
			String outPath13 = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Matrix13_out.txt";
			String outPath23 = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Matrix23_out.txt";
			
			
			if (userChanNum1 == 0 && userChanNum2 == 1)
				matar.writeMatrixToAsciiFile(outPath01, dataMatrix);
			else if (userChanNum1 == 0 && userChanNum2 == 2)
				matar.writeMatrixToAsciiFile(outPath02, dataMatrix);
			else if (userChanNum1 == 0 && userChanNum2 == 3)
				matar.writeMatrixToAsciiFile(outPath03, dataMatrix);
			else if (userChanNum1 == 1 && userChanNum2 == 2)
				matar.writeMatrixToAsciiFile(outPath12, dataMatrix);
			else if (userChanNum1 == 1 && userChanNum2 == 3)
				matar.writeMatrixToAsciiFile(outPath13, dataMatrix);
			else if (userChanNum1 == 2 && userChanNum2 == 3)
				matar.writeMatrixToAsciiFile(outPath23, dataMatrix);

		}
		catch (Exception e)
		{
			System.out.println("Error occurred in MatArrayFin! " + e.getMessage());
		}
		dataFile.close();
	}
	
	public void writeMatrixToAsciiFile(String outPath, int [][] matrix)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(outPath);
			OutputStreamWriter matOut = new OutputStreamWriter(fos, "UTF-8");
			
			for (int i=0; i < matrix.length; i++)
			{
				for (int j=0; j < matrix[i].length; j++)
				{
					matOut.write(String.valueOf(matrix[i][j]) + " ");

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
	
	private boolean createMatrix(String _dataFilePath, int moduleId, int[][] dataMatrix, int userChanNum1, int userChanNum2, int coinWindow)
	{
		PixieBinFile dataFile = new PixieBinFile(_dataFilePath);
		
		// Open file and return false if open returns false
		if (!dataFile.open())
			return false;
		
		
		int[] validMatrixBins = {4096};
		boolean matrixBinsValid = false;
		//double energyScale = 0;
		boolean iterateForward = false;
		boolean iterateReturn = false;
		int iterateChannel = -1;
		double iterateStart = 0.0;
		int iterateEnergy = 0;
		
		
		try
		{
			// Check to see if _matrixBins is valid (a length in the array)
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
				return false;
									
								
			double energyScaleFactor = 1/8;
								
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
			
			// Iterate through every event and build matrix
			for (boolean bBuffer = dataFile.moveFirstBuffer(); bBuffer; bBuffer = dataFile.moveNextBuffer())
			{
				// Check to see if this buffer belongs to moduleId
				if (dataFile.getBufferModuleNumber() != moduleId)
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
						if (!iterateForward && (chanNum == userChanNum1 || chanNum == userChanNum2))
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
										
						if (iterateForward && chanNum == userChanNum2 && iterateChannel == userChanNum1)
						{
							int energyCh1 = (int)Math.floor(energyScaleFactor * (double)iterateEnergy);
							int energyCh2 = (int)Math.floor(energyScaleFactor * (double)dataFile.getEventEnergy());
												
							dataMatrix[energyCh1][energyCh2]++;
						}
											
						if (iterateForward && 
							chanNum == userChanNum1 && 
							iterateChannel == userChanNum2)
						{
							int energyCh1 = (int)Math.floor(energyScaleFactor * (double)dataFile.getEventEnergy());
							int energyCh2 = (int)Math.floor(energyScaleFactor * (double)iterateEnergy);
												
							dataMatrix[energyCh1][energyCh2]++;
						}
											
											
						// We will still need to handle the case where we hit the end of the file....
					}	
				}	
			}
	
			dataFile.close();
		}
		catch (Exception e)
		{
			System.out.println("Problem occurred in createMatrix! " + e.getMessage());
		}
		
		return true;
	}
	
}
