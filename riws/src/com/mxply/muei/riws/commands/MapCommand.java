package com.mxply.muei.riws.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import com.mxply.muei.riws.common.IndexBuilder;
import com.mxply.muei.riws.common.Mutable;
import com.mxply.muei.riws.common.SearchBuilder;

public class MapCommand extends Command {

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length>=3 && params.length<=4;
	}

	@Override
	public String getKey() {
		return "map";
	}

	@Override
	protected String getParams() {
		return "collectionfile queryfile qrelfile [querynumber]";
	}

	@Override
	protected void action(String[] params) {
		//qrel: query-number 0 document-id relevance
		try {
		
			String collectionpath = params[0];
			File collectionfile = new File(collectionpath);
			if (!collectionfile.exists() || !collectionfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", collectionpath, new File(".").getCanonicalPath());
				return;
			}
		
			String querypath = params[1];
			File queryfile = new File(querypath);
			if (!queryfile.exists() || !queryfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", querypath, new File(".").getCanonicalPath());
				return;
			}
			
			String qrelpath = params[2];
			File qrelfile = new File(qrelpath);
			if (!qrelfile.exists() || !qrelfile.isFile())
			{			
				System.out.format("File not found [%s ]. Current folder: %s\n", qrelpath, new File(".").getCanonicalPath());
				return;
			}
			
			//1.- read qrel
			Hashtable<String, List<String>> qrels = getQRels(qrelfile);
			
			//2.- read queries
			List<String> queries = getQueries(queryfile);
			
			//3.- loop
			List<Similarity> similarities = new ArrayList<Similarity>();
			similarities.add(new DefaultSimilarity());
			//similarities.add(new LMDirichletSimilarity(0));
			//similarities.add(new LMDirichletSimilarity(1));
			//similarities.add(new LMDirichletSimilarity(2));
			//similarities.add(new LMDirichletSimilarity(5));
			similarities.add(new LMDirichletSimilarity(10));
			//similarities.add(new LMDirichletSimilarity(20));
			//similarities.add(new LMDirichletSimilarity(50));
			similarities.add(new LMDirichletSimilarity(100));
			similarities.add(new LMDirichletSimilarity(200));
			similarities.add(new LMDirichletSimilarity(500));
			similarities.add(new LMDirichletSimilarity(1000));
			similarities.add(new LMDirichletSimilarity(2000));
			//similarities.add(new LMDirichletSimilarity(2500));
			similarities.add(new LMDirichletSimilarity(5000));
			
			float map, apq, ap;
			Integer hit, missed;
			String querynumber;
			IndexBuilder ib = new IndexBuilder();
			SearchBuilder sb = new SearchBuilder();
			Directory dir = null;
			TopDocs topDocs = null;
			StringBuilder strb = null;
			List<String> precisionPerDoc = null;
			Hashtable<String, List<Point>> graphqueries = null;
			
			String querynumberToDraw = Long.toString((new Date()).getTime() % (queries.size() + 1));
			if (params.length==4)
			{
				Mutable<Integer> m = new Mutable<Integer>();
				if (com.mxply.muei.riws.common.parser.tryParseInt(params[3], m))
					querynumberToDraw = m.get().toString();
			}
			Hashtable<String, List<Point>> similaritiesToDraw = new Hashtable<String, List<Point>>();
			
			for (Similarity similarity : similarities) {
				//3.1 index
				dir = ib.build(collectionfile.getPath(), similarity);
				DirectoryReader reader = DirectoryReader.open(dir);
				ap = 0;
				strb = new StringBuilder();
				graphqueries = new Hashtable<String, List<Point>>();
				for (int i = 0; i < queries.size(); i++) {
					querynumber = Integer.toString(i+1);
					graphqueries.put(querynumber, new ArrayList<MapCommand.Point>());
					if (!qrels.containsKey(querynumber))
						continue;
										
					//3.2 search docs
					topDocs = sb.search(dir, queries.get(i), Integer.MAX_VALUE, similarity);
					
					//3.3 compare qrel
					precisionPerDoc = new ArrayList<String>();
					hit = 0;
					missed = 0;
					apq = 0;
					for (int h = 0; h < topDocs.totalHits ; h++) {
						Document doc = reader.document(topDocs.scoreDocs[h].doc);
						if (qrels.get(querynumber).contains(doc.get("ID")))
						{
							hit++;
							apq+= (float)hit/(hit+missed);
							precisionPerDoc.add(String.format("%s:%f",doc.get("ID"), (float)hit/(hit+missed)));
						
							
							if (querynumber.equals(querynumberToDraw))
							{
								if (!similaritiesToDraw.containsKey(similarity.toString()))
									similaritiesToDraw.put(similarity.toString(), new ArrayList<MapCommand.Point>());
								
								similaritiesToDraw.get(similarity.toString()).add(new Point((double)hit/qrels.get(querynumber).size(), (double)hit/(hit+missed)));
							}
							
							graphqueries.get(querynumber).add(new Point((double)hit/qrels.get(querynumber).size(), (double)hit/(hit+missed)));
						}
						else
							missed++;
						
						
						if (hit==qrels.get(querynumber).size())
							break;
					}	
					apq = hit==0?(float)0:apq/hit;
					ap += apq;
					//strb.append(String.format("Q%s\t:\t%f\tHits/Total:\t%d/%d\tDocs:\t{%s}\n", querynumber, apq, hit, (hit+missed), com.mxply.muei.riws.common.parser.join(qrels.get(querynumber), ", ", 0)));
					strb.append(String.format("Q%s\t:\t%f\tRelevant/Hits/Total:\t%4d %4d %4d\tRecall:%f\tDocs:\t{%s}\n", querynumber, apq, qrels.get(querynumber).size(), hit, (hit+missed), (float)hit/qrels.get(querynumber).size(), com.mxply.muei.riws.common.parser.join(precisionPerDoc, ", ", 0)));
				}
				//3.4 calc map
				map = ap / queries.size();
				//3.5 print map					
				log(String.format("Model:%20s\tMAP: %f", similarity.toString(), map));
				log(strb.toString(), false);
				DrawPRGraph(similarity, graphqueries);
				strb = new StringBuilder();
				
				reader.close();
				dir.close();
			}
			logcommit();
			DrawPRGraph(querynumberToDraw, similaritiesToDraw);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	private class Point
	{
		double x;
		double y;
		
		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public Point(double x, double y)
		{
			this.x = x;
			this.y = y;
		}
	}
	private void DrawPRGraph(Similarity similarity, Hashtable<String, List<Point>> queries)
	{
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<html><head>");
			sb.append("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>");
			sb.append("<script type=\"text/javascript\">");
			sb.append("google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});");
			sb.append("google.setOnLoadCallback(drawChart);");
			sb.append("function drawChart() {");
			
			StringBuilder divs = new StringBuilder();
			List<String> sortedList = new ArrayList<String>(queries.keySet());
			//Collections.sort(sortedList);
			Collections.sort(sortedList, new Comparator<String>() {
			    public int compare(String o2, String o1) {
			        Integer i1 = Integer.parseInt(o1);
			        Integer i2 = Integer.parseInt(o2);
			        return (i1 > i2 ? -1 : (i1 == i2 ? 0 : 1));
			    }
			});
			for (String key : sortedList) {
				sb.append(String.format("var data%s = google.visualization.arrayToDataTable([", key));
				sb.append("['Recall', 'Precision'],");
				for (int i = 0; i < queries.get(key).size(); i++) {
					Point p = queries.get(key).get(i);
					sb.append(String.format("%s[%f,  %f]", i!=0?",":"", p.getX(), p.getY()));
				}
				sb.append("]);");
				sb.append(String.format("new google.visualization.LineChart(document.getElementById('chart_div%1$s')).draw(data%1$s, {title: 'Precision-Recall Graph - Q%1$s',vAxis: {title: 'Precision', titleTextStyle: {color: 'blue'}}, hAxis: {title: 'Recall', titleTextStyle: {color: 'red'}}});", key));
				
				divs.append(String.format("<div id=\"chart_div%s\" style=\"width: 900px; height: 500px;\"></div>", key));
			}
			sb.append("}</script></head><body>");
			
			sb.append(divs.toString());
			
			sb.append("</body></html>");
			
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(encodeFilename(String.format("%s_%s", _logfile, similarity.toString() )) + ".html", true)));
			out.println(sb.toString());
			out.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	private void DrawPRGraph(String querynumber, Hashtable<String, List<Point>> similarities)
	{
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("<html><head>");
			sb.append("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>");
			sb.append("<script type=\"text/javascript\">");
			sb.append("google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});");
			sb.append("google.setOnLoadCallback(drawChart);");
			sb.append("function drawChart() {");
			
			sb.append(String.format("var data%s = google.visualization.arrayToDataTable([", querynumber));
			sb.append("['Recall' ");
			for (String key : similarities.keySet())
				sb.append(String.format(",'%s'", key));
			sb.append("],");
	
			for (String firstkey : similarities.keySet()) {
				for (int i = 0; i < similarities.get(firstkey).size(); i++) {
					sb.append(String.format("%s[%f, ", i!=0?",":"", similarities.get(firstkey).get(i).getX()));
					for (String key : similarities.keySet()) {
						sb.append(String.format("%s%f", !firstkey.equals(key)?",":"", similarities.get(key).get(i).getY()));
					}
					sb.append(String.format("]"));
				}
				break;
			}
			
			sb.append("]);");
			sb.append(String.format("new google.visualization.LineChart(document.getElementById('chart_div%1$s')).draw(data%1$s, {title: 'Precision-Recall Graph - Q%1$s',vAxis: {title: 'Precision', titleTextStyle: {color: 'blue'}}, hAxis: {title: 'Recall', titleTextStyle: {color: 'red'}}});", querynumber));
		
			sb.append("}</script></head><body>");
			
			sb.append(String.format("<div id=\"chart_div%s\" style=\"width: 900px; height: 500px;\"></div>", querynumber));
			
			sb.append("</body></html>");
			
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(encodeFilename(String.format("%s_Q%s", _logfile, querynumber )) + ".html", true)));
			out.println(sb.toString());
			out.close();
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	private String encodeFilename(String rawfilename) throws Exception
	{
		//return URLEncoder.encode(rawfilename, "UTF-8");
		for (char c : "(). ".toCharArray()) 
			rawfilename = rawfilename.replace(c, '_');
		
		return rawfilename;
	}
	private Hashtable<String, List<String>> getQRels(File qrelfile)
	{
		Hashtable<String, List<String>> qrels = new Hashtable<String, List<String>>();
		try {

			BufferedReader br = new BufferedReader(new FileReader(qrelfile));
			String line = null, querynumber=null, docid=null;
			String parts[] = null;
			while ((line = br.readLine()) != null) 
			{
				parts = line.split(" ");
				querynumber = parts[0];
				docid = parts[1];
				//rel = parts[2];
				if (!qrels.containsKey(querynumber))
					qrels.put(querynumber, new ArrayList<String>());
				if (!qrels.get(querynumber).contains(docid))
					qrels.get(querynumber).add(docid);
			}
			br.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return qrels;
		
	}
	private List<String> getQueries(File queryfile)
	{
		//refactorize!
		List<String> queries = new ArrayList<String>();
		try {
			List<String> tags = Arrays.asList(".I", ".W");
			BufferedReader br = new BufferedReader(new FileReader(queryfile));
			String line = null;
			
			String values[] = new String[2];
			for	(int v=0; v<values.length;v++)
				values[v]="";
			
			int currentIndex = -1;
			int tagIndex = -1;
			while ((line = br.readLine()) != null) 
			{
				if (line.length()>1)
				{						
					String startWith = line.substring(0, 2);
					if ((tagIndex=tags.indexOf(startWith))>-1)
					{
						if (tagIndex==0 && currentIndex>-1)
						{
							queries.add(values[1]);
							for	(int v=0; v<values.length;v++)
								values[v]="";
						}
						currentIndex = tagIndex;							
						values[currentIndex] += line.replaceFirst(startWith,  "").trim();
					}
					else{
						if (currentIndex>-1)
							values[currentIndex] += " " + line;
					}
				}
			}	
			if (currentIndex>-1)
				queries.add(values[1]);
			
			br.close();	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return queries;
	}
}
