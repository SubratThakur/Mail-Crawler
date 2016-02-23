package com.pramati.mail.download.impl;

import java.util.concurrent.BlockingQueue;


public class DownloaderThread extends MailDownloaderAbstractImpl implements Runnable {

	public DownloaderThread(BlockingQueue<String> downloadQueue) {
		super(downloadQueue);
	}


	public void run() {
		logger.debug("Staring the downloader thread ...");
		logger.debug("value of set finsihed" + finishedcrawler);
		while (!DownloaderThread.finishedcrawler) {
			try {
				String currentUrl = downloadedQueue.take();
				downloadMail(currentUrl);
				} catch (Exception e) {
					logger.error("Exception in opening properties file", e);
				}
			} 
		}


}
