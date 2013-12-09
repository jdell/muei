package com.mxply.muei.riws.common;

public abstract class parser {
	public static Boolean tryParseInt(String value, Mutable<Integer> output)  
	{  
	     try  
	     {  
	         output.set(Integer.parseInt(value));  
	         return true;  
	      } catch(NumberFormatException nfe)  
	      {  
	          return false;  
	      }  
	}
	
	public static String join(String r[],String d, int index)
	{		  
		if (r.length == 0 || index>r.length) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(r[index]);
		for (int x=index+1;x<r.length;++x)
			sb.append(d).append(r[x]);
		return sb.toString();
	}
}
