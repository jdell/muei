/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 02: Index searching method using Lucene 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class SimpleSearching implements IModule{

	private int processedItems = 0;
	
	private void printSummary()
	{
		System.out.format("Operación finalizada. %d items indexados.", processedItems);
	}
	
	//Otra alternativa: 
	//http://3kbzotas.wordpress.com/2011/08/01/creando-xml-en-java-de-la-forma-mas-facil-rapida-e-intuitiva/
	
	private void addRoot(Document xmlDoc, Integer size, String filter, Integer excluded)
	{
		Element rootElement = new Element("collection");
		rootElement.setAttribute(new Attribute("size", size.toString()));
		rootElement.setAttribute(new Attribute("filter", filter));
		rootElement.setAttribute(new Attribute("excluded", excluded.toString()));
		
		xmlDoc.setRootElement(rootElement);
	}
	private void addDocument(Document xmlDoc, String id, String title, String author, String institution, String content)
	{		
		Element document =  new Element("document");
		document.setAttribute(new Attribute("id", id));
		document.addContent(new Element("title").setText(title));
		document.addContent(new Element("author").setText(author));
		document.addContent(new Element("institution").setText(institution));
		document.addContent(new Element("content").setText(content));

		xmlDoc.getRootElement().addContent(document);
	}
	private void generateXML(Document xmlDoc, String opath)
	{
		try{

			/*
			 * Salida:
			 * <?xml version="" encoding="" ?>
			 * <collection>
			 * 		<document id="1">
			 * 			<title>...</title>
			 * 			<author>...</author>
			 * 			<institution>...</institution>
			 * 			<content>...</content>
			 * 		</document>
			 * </collection>
			 */

	        XMLOutputter xmlOutput = new XMLOutputter();
	        xmlOutput.setFormat(Format.getPrettyFormat());
	        xmlOutput.output(xmlDoc, new FileWriter(opath));
		
		}catch(Exception ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run(String[] params) {
		if (params.length<3)
		{
			System.out.println("Usage: java riws 2 indexFolder outputfile field:term [field:term ...]");
			return;
		}

		List<Term> terms =  new ArrayList<Term>();
		StringBuilder sbfilter = new StringBuilder();
		for(int i=2; i<params.length; i++)
		{
			if (!params[i].contains(":"))
			{
				System.out.println("Usage: java riws 2 indexFolder field:term [field:term ...]");
				return;
			}
			String pair[] = params[i].split(":");
			terms.add(new Term(pair[0],pair[1]));
			
			sbfilter.append(params[i]);
			if (i<params.length-1) sbfilter.append(", ");
		}
		
		try{
			
			String ipath = params[0];
			String opath = params[1];
			File ifile = new File(ipath);
			if (!ifile.exists())
			{
				System.out.format("Folder %s not found. Current path: %s", ipath, new File(".").getCanonicalPath());
				return;
			}
			
			Directory idir = FSDirectory.open(ifile);			
			if (!DirectoryReader.indexExists(idir))
			{
				System.out.format("There is no Index in the specified folder [%s]. Current path: %s", ipath, new File(".").getCanonicalPath());
				return;
			}
			
			IndexReader indexreader = DirectoryReader.open(idir);
			SlowCompositeReaderWrapper atomicreader = new SlowCompositeReaderWrapper((CompositeReader)indexreader);
			List<Integer> excludedDocs = new ArrayList<Integer>();
			for(Term t:terms)
			{
				DocsEnum docs = atomicreader.termDocsEnum(t);
				int doc;
				while((doc=docs.nextDoc())!=DocsEnum.NO_MORE_DOCS)
					if (!excludedDocs.contains(doc))
						excludedDocs.add(doc);
			}
			
			//Output
			processedItems =  atomicreader.numDocs() - excludedDocs.size();
					
			Document xmlDoc = new Document();
			xmlDoc.setProperty("version", "1.0");
			xmlDoc.setProperty("encoding", "UTF-8");
			addRoot(xmlDoc, processedItems, sbfilter.toString().trim(), excludedDocs.size());
			
			for (int i=0; i<atomicreader.maxDoc();i++)
			{
				org.apache.lucene.document.Document doc = atomicreader.document(i);
				
				if (!excludedDocs.contains(i))
				{
					addDocument(xmlDoc, doc.get("ID"), 
							doc.get("TITLE"), 
							doc.get("AUTHOR"), 
							doc.get("INSTITUTION"), 
							doc.get("CONTENT"));					
				}
			}
			
			atomicreader.close();
			indexreader.close();
			idir.close();
			
			generateXML(xmlDoc, opath);
			printSummary();
			
		}catch(CorruptIndexException ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}catch(LockObtainFailedException ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}catch(IOException ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			System.out.println("Describir excepcion");
			ex.printStackTrace();
		}
	}

}
