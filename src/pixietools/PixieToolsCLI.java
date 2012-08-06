package pixietools;

import java.io.*;
import java.util.*;

public class CLIMenu {
	public static void main(String[] args)
	{
		
	}
	
	private int MainMenu()
	{
		int mainmenu_choice = 0;

		try
		{
			String mainmenu_line = null;
			
			System.out.println("Please enter number of desired function: " + "\n"+
			"0 - BinStatistics (1D only)" + "\n" + "1 - Histogram of 1D data" + "\n" +
					" 2 - Matrix of 2D data" + "\n" + "-1 - Exit Menu and Program" + "\n");
			BufferedReader mainmenu_reader = new BufferedReader(new InputStreamReader(System.in));
			mainmenu_line = mainmenu_reader.readLine();
			mainmenu_choice = Integer.parseInt(mainmenu_line);
			
		}
		catch (Exception e)
		{
			System.out.println("Error occured in mainMenu! " + e.getMessage());
		}
		return mainmenu_choice;

	}
	
	
	private int Histogram_Menu()
	{
		int channelNum = 0;

		try
		{
			String channelNum_line = null;
			
			System.out.println("Please enter desired channel number between 0 and 3");
			BufferedReader channelNum_reader = new BufferedReader(new InputStreamReader(System.in));
			channelNum_line = channelNum_reader.readLine();
			channelNum = Integer.parseInt(channelNum_line);
			
			
		}
		
		catch (Exception e)
		{
			System.out.println("Error occured in histogram menu! " + e.getMessage());
		}
		
		return channelNum;
	}
	
	private int MatArray1Ch()
	{
		int matChNum1 = 0;

		try
		{
			String matChNum1_line = null;
			
			System.out.println("Please enter first channel of two in matrix");
			BufferedReader matChNum1_reader = new BufferedReader(new InputStreamReader(System.in));
			matChNum1_line = matChNum1_reader.readLine();
			matChNum1 = Integer.parseInt(matChNum1_line);
			
		}
		catch (Exception e)
		{
			System.out.println("Error occured in MatArray1Ch!" + e.getMessage());
		}
		return matChNum1;
	}
	
	private int MatArray2Ch()
	{
		int matChNum2 = 0;
		
		try
		{
			String matChNum2_line = null;
			
			System.out.println("Please enter second channel of two in matrix");
			BufferedReader matChNum2_reader = new BufferedReader(new InputStreamReader(System.in));
			matChNum2_line = matChNum2_reader.readLine();
			matChNum2 = Integer.parseInt(matChNum2_line);
		}
		catch (Exception e)
		{
			System.out.println("Error occured in MatArray2Ch!" + e.getMessage());
		}
		return matChNum2;
	}
	
	private int CompressionOptions()
	{
		int newBinSize = 32768;
		
		try
		{
			String newBinSize_line = null;
			
			System.out.println("Please enter desired compressed bin size:" + "\n" +
			"0 - 1024" + "\n" + "1 - 2048" + "\n" + "2 - 4096" + "\n" + "3 - 8192" + 
					"\n" + "4 - 16384");
			
			BufferedReader newBinSize_reader = new BufferedReader(new InputStreamReader(System.in));
			newBinSize_line = newBinSize_reader.readLine();
			newBinSize = Integer.parseInt(newBinSize_line);
		}
		catch (Exception e)
		{
			System.out.println("Error occured in CompressionOptions!" + e.getMessage());
		}
		return newBinSize;
	}
	
	private String getFilePath()
	{
		String filePath_line = null;
		
		try
		{
			System.out.println("Please enter path of desired file");
			BufferedReader filePath_reader = new BufferedReader(new InputStreamReader(System.in));
			filePath_line = filePath_reader.readLine();
		
		}
		catch (Exception e)
		{
			System.out.println("Error occured in getFilePath" + e.getMessage());
		}
		return filePath_line;
	}
	
}

