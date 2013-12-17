package com.mxply.muei.riws.recommendation;

public class CosineSimilarity extends SimilarityFunction
{

	@Override
	public String toString() {
		return "Cosine";
	}

	@Override
	public double get(double[] u, double[] v) {
		//http://www.blog.computergodzilla.com/2013/07/how-to-calculate-tf-idf-of-document.html
		double product = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double similarity = 0.0;
        double magnitude = 0;

        for (int i = 0; i < u.length; i++)
        {
        	product += u[i] * v[i];
            magnitude1 += Math.pow(u[i], 2); 
            magnitude2 += Math.pow(v[i], 2); 
        }

        magnitude = Math.sqrt(magnitude1 * magnitude2);
        
        if (magnitude != 0.0) {
        	similarity = product / magnitude;
        } else {
        	similarity= 0.0;
        }
        return similarity;
	}
	
}

