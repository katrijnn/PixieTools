package com.pixietools;

import java.io.*;

public class MatArray {
	
	
	public static void main(String[] args)
	{
		try
		{
			MatArray matArray = new MatArray();
			
			String outPath = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Matrix.txt";
			
			int[] ch0 = new int[32768];
			int[] ch1 = new int[32768];
			int[] ch2 = new int[32768];
			int[] ch3 = new int[32768];
			
			String binFilePath = "C:\\Users\\Katrijn\\Desktop\\PixieTestFiles\\co60_001.bin";
			
			FileOutputStream fos = new FileOutputStream(outPath);
			OutputStreamWriter matFile = new OutputStreamWriter(fos, "UTF-8");
			
			/*
			String out01Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Mat01.txt";
			String out02Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Mat02.txt";
			String out03Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Mat03.txt";
			String out12Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Mat12.txt";
			String out13Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Mat13.txt";
			String out23Path = "C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\Mat23.txt";
			*/
			String chanNum1_line = null;
			int chanNum1 = 0;
			
			String chanNum2_line = null;
			int chanNum2 = 0;
			
			System.out.println("Please enter first of the 2 channels you wish to see in this matrix: ");
			BufferedReader chanNum1_reader = new BufferedReader(new InputStreamReader(System.in));
			chanNum1_line = chanNum1_reader.readLine();
			chanNum1 = Integer.parseInt(chanNum1_line);
			
			System.out.println("Please enter second of the 2 channels you wish to see in this matrix: ");
			BufferedReader chanNum2_reader = new BufferedReader(new InputStreamReader(System.in));
			chanNum2_line = chanNum2_reader.readLine();
			chanNum2 = Integer.parseInt(chanNum2_line);

			if (chanNum1 >= 0 && chanNum1 <= 3)
			{
				if (chanNum2 >= 1 && chanNum2 <= 3)
				{
					if (chanNum1 == 0 && chanNum2 == 1)
					{
						for (int i=0; i < ch0.length; i++)
						{
							for (int j=0; j < ch1.length; j++)
							{
								int resultMat01[][] = {ch0, ch1};
							}
						}
					}
					
					
					if (chanNum1 == 0 && chanNum2 == 2)
					{
						for (int i=0; i < ch0.length; i++)
						{
							for (int j=0; j < ch2.length; j++)
							{
								int resultMat02[][] = {ch0, ch2};
								
							}
						}
					}
					
					if (chanNum1 == 0 && chanNum2 == 3)
					{
						for (int i=0; i < ch0.length; i++)
						{
							for (int j=0; j < ch3.length; j++)
							{
								int resultMat03[][] = {ch0, ch3};
							}
						}
					}
					
					if (chanNum1 == 1 && chanNum2 == 2)
					{
						for (int i=0; i < ch1.length; i++)
						{
							for (int j=0; j < ch2.length; j++)
							{
								int resultMat12[][] = {ch1, ch2};
							}
						}
					}
					
					if (chanNum1 == 1 && chanNum2 == 3)
					{
						for (int i=0; i < ch1.length; i++)
						{
							for (int j=0; j < ch3.length; j++)
							{
								int resultMat13[][] = {ch1, ch3};
							}
						}
					}
					
					if (chanNum1 == 2 && chanNum2 == 3)
					{
						for (int i=0; i < ch2.length; i++)
						{
							for (int j=0; j < ch3.length; j++)
							{
								int resultMat23[][] = {ch2, ch3};
								
							}
						}
					}
					
				}
			}	
			matFile.close();
			fos.close();
		}
		
		catch (Exception e)
		{
			System.out.println("Error in MatArray! " + e.getMessage());
		}
			
	}

	private void writeMatrixToAscii(String matOutPath, int[][] resultMat)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(matOutPath);
			OutputStreamWriter matFile = new OutputStreamWriter(fos, "UTF-8");
			
			for (int i=0; i < resultMat[i].length; i++)
			{
				for (int j=0; j < resultMat[j].length; j++)
				{
					matFile.write(String.valueOf(resultMat[i][j] + "\n"));
				}
			}
			
			matFile.close();
			fos.close();
		}
		catch (Exception e)
		{
			System.out.println("Error occured in writeMatrix! " + e.getMessage());
		}
	}


	private boolean getMatrixComponents(String binFilePath, int moduleId, int [] ch0, int [] ch1, int [] ch2, int [] ch3)
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
		if (ch2.length != 32768) 
			return false;
		if (ch3.length != 32768) 
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

		for (int i=0; i < ch2.length; i++)
		{
			ch2[i] = 0;
		}
		
		for (int i=0; i < ch3.length; i++)
		{
			ch3[i] = 0;
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
			System.out.println("Error Occured in getMatrixComponents!" + e.getMessage());
			myFile.close();
			return false;
		}
		
		myFile.close();
		return true;
	}	
}
 