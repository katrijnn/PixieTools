package pixietools;

import java.io.*;

public class Mat2D {
	public static void main(String[] args)
	{
		try
		{
			int[] ch0 = new int[32768];
			int[] ch1 = new int[32768];
			
			String binFilePath = "C:\\Users\\Katrjn\\Desktop\\PixieTestFiles\\co60_001.bin";
			
			Mat2D mat2D = Mat2D();
			
			mat2D.getMatrixComponents(String binFilePath, int moduleID, int[] ch0, int[] ch1);
			
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
			System.out.println("")
		}
	}

}
