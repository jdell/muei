package com.mxply.muei.riws.recommendation;
import java.util.ArrayList;
import java.util.List;

import com.mxply.muei.riws.common.math;

public class KNNAlgorithm extends CFAlgorithm {

	private Integer kNumber = null;
	private boolean centered = false;
	
	public KNNAlgorithm(SimilarityFunction sim)
	{
		this(sim,null);
	}
	public KNNAlgorithm(SimilarityFunction sim, Integer knumber)
	{
		this(sim, knumber, false);
	}
	public KNNAlgorithm(SimilarityFunction sim, boolean centered)
	{
		this(sim, null, centered);
	}
	public KNNAlgorithm(SimilarityFunction sim, Integer knumber, boolean centered)
	{
		super(sim);
		this.kNumber = knumber;
		this.centered = centered;
	}
	public Integer getKNumber()
	{
		return this.kNumber;
	}
	@Override
	public void process(double[][] training, double[][] test) {
		recommendations.clear();
		
        double bigC = 0;
        double prediction = 0;
        
        double[][] similarities = new double[training.length][training.length];
        for (int u = 0; u < training.length; u++) {
			for (int v = 0; v < training.length; v++) {
				similarities[u][v] = sim.get(training[u], training[v]);
			}
        }
        for (int u = 0; u < test.length; u++) {
			for (int i = 0; i < test.length; i++) {
				if (test[u][i]!=0)
				{
					prediction = 0;
					bigC = 0;
					List<Integer> neighbors = kNumber==null?null:getNeighbors(this.getKNumber(), u, similarities[u]);
					for (int v = 0; v < test.length; v++) {
						if (v!=u && training[v][i]!=0 && (kNumber==null || neighbors.contains(v)))
						{
							prediction += similarities[u][v]* (training[v][i]-(centered?math.calcAverage(training[v]):0));
							bigC += Math.abs(similarities[u][v]);
						}
					}
					prediction = bigC!=0?(centered?math.calcAverage(training[u]):0)+prediction/bigC:0;
					recommendations.add(new Recommendation(u, i, test[u][i], prediction));
				}
			}
		}
        
        double mae=0, rmse=0;
        for (Recommendation recommendation : recommendations) {
			mae += Math.abs(recommendation.getR()-recommendation.getV())/recommendations.size();
			rmse += Math.pow(recommendation.getR()-recommendation.getV(),2)/recommendations.size();
		}
        RMSE = Math.sqrt(rmse);
        MAE = mae;
	}
	
	private List<Integer> getNeighbors(Integer k, int u, double[] similarities)
	{
		List<Integer> neighbors = new ArrayList<Integer>();
		List<Double> values = new ArrayList<Double>();
		for (int v = 0; v < similarities.length; v++) {
			if (v!=u)
			{
				if (neighbors.size()<k)
				{
					neighbors.add(v);
					values.add(similarities[v]);
				}
				else
				{
					for (int i = 0; i < neighbors.size(); i++) {
						if (similarities[v]>neighbors.get(i))
						{
							neighbors.set(i, v);
							values.set(i, similarities[v]);
							break;
						}
					}
				}
			}
		}
				
		return neighbors;
	}
	@Override
	public String toString() {
		return String.format("KNN (centered: %5s, k: %5s) - Sim: %15s", centered, kNumber!=null?kNumber:"all", sim);
	}

}
