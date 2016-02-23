package com.pramati.crawler.start;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.pramati.mail.crawler.impl.CrawlerThread;
import com.pramati.mail.download.impl.DownloaderThread;


public class CrawlerEntry {
	static BlockingQueue<String> urlQueue = new LinkedBlockingQueue<String>();
	static BlockingQueue<String> downloadQueue = new LinkedBlockingQueue<String>();
	static int curr_year = Calendar.getInstance().get(Calendar.YEAR);
	final static Logger logger = Logger.getLogger(CrawlerEntry.class);

	public static void main(String args[]) {
		Scanner scanner = new Scanner(System.in);
		try {
			logger.debug("Enter the URl to Parse");
			String URL = scanner.next();
			String year=scanner.next();
			while(Integer.valueOf(year)>curr_year || Integer.valueOf(year)<2000){
				logger.debug("Please provide correct year, it should be between 2000 to"+curr_year+"; your input is"+year );
				year=scanner.next();
			}
			CrawlerEntry crawlerEnt=new CrawlerEntry();
			crawlerEnt.getMailCrawlerBegin(URL,year);
		}
		catch(Exception e){
			logger.debug("Exception occurs in main"+e);
		}
		finally
		{
			scanner.close();
		}
	}
	
	public void getMailCrawlerBegin(String URL,String year){
			logger.debug("The entered URL IS :"+URL);
			try{
			if(Integer.valueOf(year)>curr_year || Integer.valueOf(year)<2000){
				logger.debug("Please provide correct year, it should be between 2000 to"+curr_year+"; your input is"+year );
			    return;
			}
			urlQueue.put(URL);
			ExecutorService executor = Executors.newFixedThreadPool(2);
			ExecutorService downexecutor = Executors.newFixedThreadPool(10);
			
			//**Starting Crawler Threads**
			for (int i = 0; i < 10; i++) {
				Runnable worker = new CrawlerThread(urlQueue,downloadQueue,year);
				executor.execute(worker);
			}
			// **Starting downloader threads**
			for (int i = 0; i < 10; i++) {
				Runnable downloaderWorker = new DownloaderThread(downloadQueue);
				downexecutor.execute(downloaderWorker);
			}
			executor.shutdown();
			downexecutor.shutdown();
			logger.debug("All tasks submitted.");
			
			try {
				executor.awaitTermination(1, TimeUnit.DAYS);

			} catch (InterruptedException e) {
				logger.debug("InterruptedException while awaiting terminationation of executor");
			}
			//** Setting the exiting condition for downloader threads**
			
			DownloaderThread.setFinishedcrawler(true);
			logger.debug("All crawler threads completed.");
			try {
				downexecutor.awaitTermination(1, TimeUnit.DAYS);

			} catch (InterruptedException e) {
				logger.debug("InterruptedException while awaiting terminationation of downexecutor");
			}
			logger.debug("Finished all threads");
		} catch (Exception e) {
			logger.error("Exception in main", e);
		}
	}

}
