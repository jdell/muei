package com.mxply.muei.riws.common;

import java.util.List;

public abstract class math {
	public static double[][] toArray(List<List<Double>> in)
	{
		final int listSize = in.size();
		double[][] res = new double[listSize][];
		for(int i = 0; i < listSize; i++) {
		    List<Double> sublist = in.get(i);
		    final int sublistSize = sublist.size();
		    res[i] = new double[sublistSize];
		    for(int j = 0; j < sublistSize; j++) {
		        res[i][j] = sublist.get(j);
		    }
		}
		return res;
	}
	public static void print(double[][] matrix)
	{
		print(matrix, 2);
	}
	public static void print(double[][] matrix, int dec)
	{
		for (int i = 0; i < matrix.length; i++) {
			print(matrix[i], dec);
			/*
			for (int j = 0; j < matrix[i].length; j++) {
				System.out.printf("%."+dec+"f ", matrix[i][j]);
			}
			System.out.println();
			*/
		}
		System.out.println();
	}
	public static void print(double[] matrix, int dec)
	{
		for (int i = 0; i < matrix.length; i++)
			System.out.printf("%."+dec+"f ", matrix[i]);
		System.out.println();
	}
	public static double[][] transpose(double[][] matrix)
	{
		double transpose[][]=new double[matrix[0].length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				transpose[j][i] = matrix[i][j];
			}
		}
		
		return transpose;
	}
	public static double calcAverage(double[] ratings){
		  double sum = 0;
		  if (ratings.length!=0) {
			  int count = 0;
			  for (int i = 0; i < ratings.length; i++) {
			  {
				  if (ratings[i]!=0)
				  {
					  sum+=ratings[i];
					  count++;
					  //System.out.printf("%s\n", ratings[i]);
				  }
			  }
			}
			  //System.out.printf("%s / %s = %s\n", sum, count, sum/count);
		    return count!=0? sum / count:0;
		  }
		  return sum;
		}


}
