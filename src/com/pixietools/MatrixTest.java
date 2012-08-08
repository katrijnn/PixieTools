package com.pixietools;

import java.io.*;

public class MatrixTest
{	
	public static void main(String[] args)
	{
		MatrixTest newMatTest = new MatrixTest();
		int[][] Mat;
		
		try
		{
			Mat = newMatTest.CreateMatrix();

			FileOutputStream fos = new FileOutputStream("C:\\Users\\kaatrin.a.netherton\\Desktop\\PixieOutFiles\\MatTest_out.txt");
			OutputStreamWriter matOut = new OutputStreamWriter(fos, "UTF-8");

			for (int i=0; i < Mat.length; i++)
			{
				for (int j=0; j < Mat[i].length; j++)
				{
					matOut.write(String.valueOf(Mat[i][j]) + " ");

				}
				matOut.write("\n");
			}
			matOut.close();

		}
		catch (Exception e)
		{
			System.out.println("Error in MatrixTest! " + e.getMessage());
		}
	}
	
	public int[][] CreateMatrix()
	{
		// declare 2d array with 30 elements in each component
		int[][] Mat = new int[30][30];

		// zero out arrays
		for (int i=0; i < Mat.length; i++)
		{
			for (int j=0; j < Mat.length; j++)
			{
				Mat[i][j] = 0;
			}
		}
	
		try
		{
			// increment element [20][14]
			for (int k=0; k < 20; k++)
			{
				Mat[20][14]++;
			}
			
			for (int m=0; m < 75; m++)
			{
				Mat[16][28]++;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error occurred in Matrix Test! " + e.getMessage());
		}
		
		return Mat;
	}
}