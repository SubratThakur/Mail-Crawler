package com.pramati.url.parser;

import java.util.Set;

import org.jsoup.nodes.Document;



public interface UrlParser {
	
	public Set<String> getParsedUrl(Document url, String searchWord);
	
	public boolean searhForValidUrl(Document doc, String searchWord);

}
