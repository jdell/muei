package com.mxply.muei.riws.recommendation;

import com.mxply.muei.riws.common.math;

public class PearsonCorrSimilarity extends SimilarityFunction {

	@Override
	public String toString() {
		return "Pearson Correl.";
	}

	@Override
	public double get(double[] u, double[] v) {
        double similarity = 0.0;
        double product = 0.0;
        double magnitude = 0.0;
        double averageU = com.mxply.muei.riws.common.math.calcAverage(u);
        double averageV = com.mxply.muei.riws.common.math.calcAverage(v);
        
        for (int i = 0; i < u.length; i++)
        {
        	product += (u[i]-averageU)*(v[i]-averageV);
        	magnitude += Math.pow(u[i]-averageU,2) * Math.pow(v[i]-averageV,2);
        }        
        magnitude = Math.sqrt(magnitude);
        if (magnitude!=0)
        	similarity = product / magnitude;
        else
        	similarity = 0;
                
		return similarity;
	}

}
