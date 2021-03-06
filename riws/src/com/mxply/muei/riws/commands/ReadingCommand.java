/* 
 * ***********************************************************
 * MUEI - RIWS - Practice 02: Index reading method using Lucene 
 * Author: W. Joel Castro Reynoso
 * Email: joemismito(at)gmail.com
 * ***********************************************************   
 */
package com.mxply.muei.riws.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
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

public class ReadingCommand extends Command {

	private int processedItems = 0;

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
			ex.printStackTrace();
		}
	}

	@Override
	public Boolean canExecute(String[] params) {
		return params!=null && params.length>=3;
	}

	@Override
	public String getKey() {
		return "read";
	}

	@Override
	protected String getParams() {
		return "indexFolder outputfile field:term [field:term ...]";
	}

	@Override
	protected void action(String[] params) {
		List<Term> terms =  new ArrayList<Term>();
		StringBuilder sbfilter = new StringBuilder();
		for(int i=2; i<params.length; i++)
		{
			if (!params[i].contains(":"))
			{
				System.out.println(toString());
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
				System.out.format("Folder %s not found. Current path: %s\n", ipath, new File(".").getCanonicalPath());
				return;
			}
			
			Directory idir = FSDirectory.open(ifile);			
			if (!DirectoryReader.indexExists(idir))
			{
				System.out.format("There is no Index in the specified folder [%s]. Current path: %s\n", ipath, new File(".").getCanonicalPath());
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
				if (!excludedDocs.contains(i))
				{
					org.apache.lucene.document.Document doc = atomicreader.document(i);
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
			
			System.out.format("%d item%s processed.\n", processedItems, processedItems==1?"":"s");
			
		}catch(CorruptIndexException ex)
		{
			ex.printStackTrace();
		}catch(LockObtainFailedException ex)
		{
			ex.printStackTrace();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

}
