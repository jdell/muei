package com.mxply.muei.riws.recommendation;

public abstract class SimilarityFunction
{
	public abstract String toString();
	public abstract double get(double[] u, double[] v);
}
