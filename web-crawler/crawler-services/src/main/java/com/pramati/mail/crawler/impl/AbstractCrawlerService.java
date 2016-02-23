/**
 * 
 */
package com.pramati.mail.crawler.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import com.pramati.mail.crawler.Crawler;
import com.pramati.url.parser.UrlParser;
import com.pramati.url.parser.impl.UrlParserImpl;

/**
 * @author subratt
 *
 */
public abstract class AbstractCrawlerService implements Crawler{
	
	protected static BlockingQueue<String> pagesToVisit = null;
	protected static BlockingQueue<String> downloadQueue =new LinkedBlockingQueue<String>();
	protected static Set<String> pagesVisited = new HashSet<String>();
	Set<String> link = new HashSet<String>();
	final static Logger logger = Logger.getLogger(AbstractCrawlerService.class);
	protected static String YEAR="2014";
	
	public AbstractCrawlerService(BlockingQueue<String> urlQueue,BlockingQueue<String> downlQueue,String year) {
		pagesToVisit = urlQueue;
		downloadQueue = downlQueue;
		YEAR=year;
	}
	
	public void search(String url, String keyword) {
		try {
			String currentUrl = url;
			UrlParser urlCrawler = new UrlParserImpl();
			logger.debug("Current url:" + currentUrl);
				boolean success = urlCrawler.searhForValidUrl(
						getHtmlDoc(currentUrl), keyword);
				if (success) {
					logger.debug("search for mail using url" + currentUrl+" success");
					downloadQueue.put(currentUrl);
				} else {
					logger.debug("search for mail using url" + currentUrl+" failure");
					link = urlCrawler.getParsedUrl(getHtmlDoc(currentUrl), keyword);
					for (String s : link)
						pagesToVisit.put(s);
				}

			logger.debug("\n**Done** Visited " + pagesVisited.size()
					+ " web page(s) \nTo Visit pages " + pagesToVisit.size()
					+ " web page(s) \ndownload Queue" + downloadQueue.size()
					+ " web page(s)");
		} catch (Exception e) {
			logger.debug("Exception in getting the next url", e);
		}

	}

	/*
	 * This private method provide html document using url
	 */
	private Document getHtmlDoc(String url) throws IOException {
		Document document = Jsoup.connect(url).get();
		return document;
	}

	protected String nextUrl() throws Exception {
		String nextUrl;
		do {
			nextUrl = pagesToVisit.take();
		} while (pagesVisited.contains(nextUrl) && !pagesToVisit.isEmpty());
		pagesVisited.add(nextUrl);
		return nextUrl;
	}

}
