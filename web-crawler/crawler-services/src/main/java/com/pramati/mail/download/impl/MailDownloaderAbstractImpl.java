package com.pramati.mail.download.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.pramati.mail.download.MailDownloader;

public abstract class MailDownloaderAbstractImpl implements MailDownloader{

	protected static BlockingQueue<String> downloadedQueue = null;
	final static Logger logger = Logger.getLogger(MailDownloaderAbstractImpl.class);
	static Properties CONFIGFILE = new Properties();
	volatile static boolean finishedcrawler = false;
	private static String FORMAT=null;

	public MailDownloaderAbstractImpl(BlockingQueue<String> downloadQueue) {
		downloadedQueue = downloadQueue;
	}

	public static void setFinishedcrawler(boolean val) {
		finishedcrawler = val;
	}
	
	public void downloadMail(String url) {
		{
			BufferedInputStream FIN  =null;
			FileOutputStream FOUT =null;
			try {
				FIN  = new BufferedInputStream(new URL(url).openStream());
				FOUT = getDownloadFileOutputStream(url);
				byte data[] = new byte[1024];
				int count;
				while ((count = FIN.read(data, 0, 1024)) != -1) {
					FOUT.write(data, 0, count);
				}
			} catch (Exception e) {
				logger.error("Cannot download file : ", e);
			} finally {
				if (FIN != null)
					try {
						FIN.close();
					} catch (IOException e) {
						logger.error("Cannot close input stream" ,e);
					}
				if (FOUT != null)
					try {
						FOUT.close();
					} catch (IOException e) {
						logger.error("Cannot close output stream" ,e);
					}
				logger.debug("File  for url " + url 
						+ " downloaded successfully");
			}

		}
	}
	/*
	 * This method provide FileOutputStream for 
	 */
	private  FileOutputStream getDownloadFileOutputStream(String currentUrl){
		FileOutputStream fout = null;
		try {
			CONFIGFILE.load(com.pramati.mail.crawler.Crawler.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			String dpath = CONFIGFILE.getProperty("downloadPath");
			String libfile = dpath+ "2014"+ currentUrl.substring(currentUrl.indexOf("@") - 15,currentUrl.indexOf("@") - 3);
			File path = new File(libfile);
			fout = new FileOutputStream(path);
		}
		catch(Exception e){
			logger.debug("Exception occurs while reading config file for downlaodPath"+ e);
		}
		return fout;
	}
	
	/*
	 * This method is used to get file format of the download mail i.e defined in configuration file
	 */
	
	private String getFileFormat(){
		String format=null;
		try {
			CONFIGFILE.load(com.pramati.mail.crawler.Crawler.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			format = CONFIGFILE.getProperty("format");
		}
		catch(Exception e){
			logger.debug("Exception occurs while reading config file for format"+ e);
		}
		return format;
	}
}
