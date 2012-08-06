package pixietools;

import java.io.*;

public class MatrixTest2 {
	public static void main(String[] args)
	{
		// create arrays
		int[] op1;
		int[] op2;

		try
		{
			MatrixTest2 newMatTest2 = new MatrixTest2();
					
			newMatTest2.CreateOp1();
			newMatTest2.CreateOp2();
			newMatTest2.createMatrix();
			
			// declare 2D array with 30 elements in each component
			 int[][] Mat = {op1, op2};
			
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Katrijn\\Desktop\\PixieOutFiles\\MatTest2_out.txt");
			OutputStreamWriter matOut = new OutputStreamWriter(fos, "UTF-8");
		
		}
		catch (Exception e)
		{
			System.out.println("Error occured in MatrixTest2! " + e.getMessage());
		}
		
		
	}
	
	public int[] CreateOp1()
	{
		int[] op1 = new int[30];
		try
		{
			
			//zero array
			for (int i=0; i < op1.length; i++)
			{
				op1[i] = 0;
			}
			
			/*
			//populate array
			for (int i=0; i < op1.length; i++)
			{
				op1[i]++;
			}
			*/
			
			// change element 20 to 5
			for (int k=0; k < 20; k++)
				op1[5]++;
			
		}
		catch (Exception e)
		{
			System.out.print(e.getMessage());
		}
		return op1;
		
	}
	
	public int[] CreateOp2()
	{
		int[] op2 = new int[30];
		
		try
		{
			for (int i=0; i < op2.length; i++)
			{
				op2[i] = 0;
			}
			/*
			for (int i=0; i < op2.length; i++)
			{
				op2[i]++;
			}
			*/
			for (int k=0; k < 45; k++)
			{
				op2[16]++;
			}
			
		}
		catch (Exception e)
		{
			System.out.print(e.getMessage());
		}
		return op2;
	}
	
	public int[][] createMatrix()
	{
		// declare 2D array with 30 elements in each component
		int[][] Mat = new int[30][30];
		
		try
		{
			// zero out matrix
			for (int i=0; i < Mat.length; i++)
			{
				for (int j=0; j < Mat.length; j++)
				{
					Mat[i][j]= 0;

				}
			}
		}
		catch (Exception e)
		{
			System.out.print(e.getMessage());
		}
		return Mat;
		
	}

}
