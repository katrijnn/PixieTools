package com.pixietools;

import java.io.*;

public class Mat2D {
	
	public static void main(String[] args)
	{
		try
		{
			
			int[] ch0 = new int[32768];
			int[] ch1 = new int[32768];
		
			
			String binFilePath = "C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieTestFiles\\co60_001.bin";
			
			Mat2D mat2D = Mat2D();
			
			mat2D.getMatrixComponents(String binFilePath, int moduleId, int [] ch0, int [] ch1);
			
			for (int x=0; x < ch0.length; x++)
			{
				for (int y=0; y < ch1.length; y++)
				{
					int resultMat[x][y];
				}
			}
			
			
		}
		catch (Exception e)
		{
			System.out.println("Error occured in Mat2D! " + e.getMessage());
		}
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