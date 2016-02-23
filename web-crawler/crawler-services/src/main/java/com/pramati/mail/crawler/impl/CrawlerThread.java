package com.pramati.mail.crawler.impl;

import java.util.concurrent.BlockingQueue;

public class CrawlerThread extends AbstractCrawlerService implements Runnable  {

	public CrawlerThread(BlockingQueue<String> urlQueue, BlockingQueue<String> downlQueue,String year) {
		super(urlQueue, downlQueue,year);
	}

	public void run() {
		try {
			while (pagesToVisit.isEmpty()) {
				Thread.sleep(10);
			}
			while (!pagesToVisit.isEmpty()) {
				logger.debug("Thread" + Thread.currentThread().getName());
				String currentUrl = nextUrl();
				search(currentUrl, YEAR);
			}
		} catch (Exception e) {
			logger.error("Exception in getting the next URl", e);
		}
		logger.debug(" i am out of while");
	}

}
