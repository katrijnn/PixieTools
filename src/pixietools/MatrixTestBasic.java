package pixietools;

import java.io.*;

public class MatrixTestBasic {
	public static void main(String[] args)
	{
		int[] x = {2,5,7,9};
		int[] y = {11,22,33,44};
		
		int result[][] = {x,y};
		
		for (int i=0; i < result.length; i++)
		{
			for (int j=0; j < result[0].length; j++)
			{
				System.out.println(result[i][j] + " ");
			}
			System.out.println();
		}
	}
}