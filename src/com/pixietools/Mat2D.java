package com.pixietools;

import java.io.*;

public class Mat2D {
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Starting...");	
			
			int[] ch0 = new int[32768];
			int[] ch1 = new int[32768];
		
			String binFilePath = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieTestFiles\\co60_001.bin";

			
			
			Mat2D mat2D = new Mat2D();

			int resultMat[][] = new int[ch0.length][ch1.length];

			
			if (mat2D.getMatrixComponents(binFilePath, 0, ch0, ch1))
				return;
			
			FileOutputStream fos = new FileOutputStream("C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\Mat2D_out.txt");
			OutputStreamWriter matFile = new OutputStreamWriter(fos, "UTF-8");
			
			// comparison: if the element in array x is the same as
			// the element in array y, then the element in the matrix
			// where they meet is incremented by 1
			// otherwise it returns the matrix in it's untouched format
			// default value for all elements of the matrix is 0
			for (int x=0; x < ch0.length; x++)
			{
				for (int y=0; y < ch1.length; y++)
				{
					resultMat[x][y] = 0;
					if (ch0[x] == ch1[y])
					{
						resultMat[x][y]++;
						
						for (int i=0; i < resultMat.length; i++)
						{
							for (int j=0; j < resultMat[i].length; j++)
							{
								matFile.write(String.valueOf(resultMat[i][j]) + " ");
							}
							matFile.write("0 \n");
						}
					}
					else
						continue;
				}
				matFile.close();
			}

		}
		catch (Exception e)
		{
			System.out.println("Error occured in Mat2D! " + e.getMessage());
		}
		System.out.println("Finished!");	
	}
	
	private boolean getMatrixComponents(String binFilePath, int moduleId, int [] ch0, int [] ch1)
	{
		
		PixieBinFile myFile = new PixieBinFile(binFilePath);

		// open file, if not open return false
		if (!myFile.open())
			return false;
		

		// check to make sure moduleId is either 0 or 1
		if ((moduleId != 0) && (moduleId != 1))
			return false;
		

		// check that arrays are correct length
		if (ch0.length != 32768) 
			return false;
		if (ch1.length != 32768) 
			return false;
		
		
		// zero out arrays
		for (int i=0; i < ch0.length; i++)
		{
			ch0[i] = 0;
		}
		
		for (int i=0; i < ch1.length; i++)
		{
			ch1[i] = 0;
		}
	
		// increment arrays for each particular energy
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
			System.out.println("Error Occured in getMatrixComponents!" + e.getMessage());
			myFile.close();
			return false;
		}
		
		myFile.close();
		return true;
	}	
	
}