/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 05: Recommendation using a CF algorithm 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mxply.muei.riws.common.Mutable;
import com.mxply.muei.riws.common.math;
import com.mxply.muei.riws.recommendation.CFAlgorithm;
import com.mxply.muei.riws.recommendation.CosineSimilarity;
import com.mxply.muei.riws.recommendation.KNNAlgorithm;
import com.mxply.muei.riws.recommendation.PearsonCorrSimilarity;
import com.mxply.muei.riws.recommendation.SimilarityFunction;

public class RatingCommand extends Command {

	public RatingCommand()
	{
	}
	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length>=1 && params.length<=2;
	}

	@Override
	public String getKey() {
		return "rate";
	}

	@Override
	protected String getParams() {
		return "ratingfile knn";
	}

	@Override
	protected void action(String[] params) {		
		try {
			String ratingpath = params[0];
			File ratingfile = new File(ratingpath);
			if (!ratingfile.exists())
			{
				System.out.format("File %s not found. Current path: %s\n", ratingpath, new File(".").getCanonicalPath());
				return;
			}
            
			//1. Read excel
			FileInputStream file = new FileInputStream(ratingfile);
			 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            //Matrix ratings_all = getRatings(workbook.getSheetAt(0));
            double[][] ratings_training = math.transpose(getRatings(workbook.getSheetAt(1)));
            double[][] ratings_test = math.transpose(getRatings(workbook.getSheetAt(2)));
            
            file.close();

            Integer kNN = ratings_training.length;
            if (params.length==2)
            {
            	Mutable<Integer> output = new Mutable<Integer>();
            	if (com.mxply.muei.riws.common.parser.tryParseInt(params[1], output))
            		kNN = output.get();
            }            
            List<CFAlgorithm> algorithms = new ArrayList<CFAlgorithm>();
            for (SimilarityFunction sim : Arrays.asList(new SimilarityFunction[]{new CosineSimilarity(), new PearsonCorrSimilarity()})) {
                algorithms.add(new KNNAlgorithm(sim));
                algorithms.add(new KNNAlgorithm(sim, true));
                algorithms.add(new KNNAlgorithm(sim, kNN,false));
                algorithms.add(new KNNAlgorithm(sim, kNN,true));
			}
            
            for (CFAlgorithm cf : algorithms) {
                cf.process(ratings_training, ratings_test);
                log(String.format("%s\t-\tMAE:\t%.3f\t-\tRMSE:\t%.3f", cf.toString(), cf.getMAE(), cf.getRMSE()));
                log(cf.recommendationToString(), false);
			}
            
            logcommit();			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	private double[][] getRatings(XSSFSheet sheet)
	{
		return getRatings(sheet, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	private double[][] getRatings(XSSFSheet sheet, int maxrows, int maxcols)
	{
		double res[][] = null;
		try {
			//http://howtodoinjava.com/2013/06/19/readingwriting-excel-files-in-java-poi-tutorial/
			List<List<Double>> ratings= new ArrayList<List<Double>>();
            List<Integer> rows_to_skip = Arrays.asList(new Integer[]{0, 1});
            List<Integer> cols_to_skip = Arrays.asList(new Integer[]{0, 1,2});
            Integer index = -1;
            
            //Iterate through each rows one by one
            
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) 
            {            	
                Row row = rowIterator.next();
            	if (rows_to_skip.contains(row.getRowNum()))
            		continue;

            	if (ratings.size()==maxrows)
            		break;
            	
            	ratings.add(new ArrayList<Double>());
            	index++;
            	//For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                
                while (cellIterator.hasNext()) 
                {
                    Cell cell = cellIterator.next();

                	if (cols_to_skip.contains(cell.getColumnIndex()))
                		continue;

                	if (ratings.get(index).size()==maxcols)
                		break;
                    //Check the cell type and format accordingly
                    switch (cell.getCellType()) 
                    {
                        case Cell.CELL_TYPE_NUMERIC:
                            //System.out.print(cell.getNumericCellValue() + "\t");
                            ratings.get(index).add(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            System.out.print(cell.getStringCellValue() + "\t");
                            ratings.get(index).add(0.);
                            break;
                    }
                }
                //System.out.println("");
            }
            res = com.mxply.muei.riws.common.math.toArray(ratings);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	
}
