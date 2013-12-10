package com.mxply.muei.riws.common;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class SearchBuilder {
	/*
}
	public TopDocs search(Directory dir, String querystring, int top) throws Exception
	{
		return search(dir, querystring, top, new DefaultSimilarity());
	}*/
	public TopDocs search(Directory dir, String querystring, int top, Similarity similarity) throws Exception
	{
		DirectoryReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(similarity);
		QueryParser parser = new QueryParser(Version.LUCENE_40, "CONTENT", new StandardAnalyzer(Version.LUCENE_40));
		Query query = parser.parse(querystring.replace("?", " ").replace("*", " "));
		
		TopDocs res = searcher.search(query, top);
		reader.close();
		
		return res;
	}
}
