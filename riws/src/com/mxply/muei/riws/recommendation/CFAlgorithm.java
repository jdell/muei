package com.mxply.muei.riws.recommendation;

import java.util.ArrayList;
import java.util.List;

public abstract class CFAlgorithm {
	protected double MAE=0;
	protected double RMSE=0;
	protected SimilarityFunction sim = null;
	
	public CFAlgorithm(SimilarityFunction sim)
	{
		this.sim = sim;
	}
	
	public String recommendationToString()
	{
		StringBuilder sb = new StringBuilder();
		for (Recommendation recommendation : recommendations) {
			sb.append(String.format("%s\t%s\t%.2f\t%.2f\n", recommendation.x, recommendation.y, recommendation.v, recommendation.r));
		}
		return sb.toString();
	}
	
	public double getMAE()
	{
		return MAE;
	}
	public double getRMSE()
	{
		return RMSE;
	}
	protected List<Recommendation> recommendations = new ArrayList<CFAlgorithm.Recommendation>();
	
	public abstract void process(double[][] training, double[][] test);
	public abstract String toString(); 
	
	protected class Recommendation{
		private int x;
		private int y;
		private double v;
		private double r;
		
		public Recommendation(int x, int y, double v, double r)
		{
			this.x = x;
			this.y = y;
			this.v = v;
			this.r= r;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public double getV() {
			return v;
		}
		public double getR() {
			return r;
		}
	}
}
