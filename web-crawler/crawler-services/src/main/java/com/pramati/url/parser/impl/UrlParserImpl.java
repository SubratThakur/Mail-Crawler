package com.pramati.url.parser.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

import com.pramati.mail.crawler.Crawler;
import com.pramati.url.parser.UrlParser;

public class UrlParserImpl implements UrlParser{
	
	Properties configFile = new Properties();
	final static Logger logger = Logger.getLogger(UrlParserImpl.class);
	private final String DEFAULTDOMAIN="mail-archives.apache.org/mod_mbox/maven-users/";
	
	/*
	 * This method basically check the keyword and return set of url
	 * @see com.pramati.url.parser.UrlParser#getParsedUrl(org.jsoup.nodes.Document, java.lang.String)
	 */
	public Set<String> getParsedUrl(Document url, String searchWord) {
		Set<String> links = new HashSet<String>();
		org.jsoup.select.Elements linksOnPage = url.select("a[href]");
		String domainchk = getDomainFromPropertyFile() + searchWord;
		logger.debug("Links found on current page: " + linksOnPage.size()
				+ ") links");
		for (org.jsoup.nodes.Element link : linksOnPage) {
			if (link.absUrl("href").toLowerCase()
					.contains(domainchk.toLowerCase())

			) {
				links.add(link.absUrl("href"));
			}
		}

		return links;
	}

	public boolean searhForValidUrl(Document doc, String searchWord) {
		boolean result = false;
		String htmlString = doc.toString();
		com.pramati.mail.filter.IMailFilter filter = new com.pramati.mail.filter.impl.DateBasedFilter();
		//Check if serachKeyWord is not null and document should not null and searchkeyword should be part of documnet
		if (!StringUtils.isEmpty(searchWord) && !StringUtils.isEmpty(htmlString) && filter.evaluate(searchWord, htmlString)) {
			logger.debug("keyword"+searchWord+" found in the page");
			result = true;
		}

		return result;
	}
	
	/*
	 * This method is used to get domain from config.properties file
	 */
	private String getDomainFromPropertyFile(){
		String domainName=null;
		try {
			configFile.load(Crawler.class.getClassLoader().getResourceAsStream(
					"config.properties"));
			
			//if doamin is empty in config properties then set default domain name
		if(StringUtils.isEmpty(configFile.getProperty("domain"))){
			domainName=DEFAULTDOMAIN;
		}
		//else set the domain name from config file
		else{
			domainName=configFile.getProperty("domain");
		}
		} catch (IOException e) {
			logger.error("Exception in opening properties file", e);
		}

		return domainName;
	}

}
