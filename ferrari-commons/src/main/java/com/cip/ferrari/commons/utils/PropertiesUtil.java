package com.cip.ferrari.commons.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * properties util
 * @author xuxueli 2015-8-28 10:35:53
 */
public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private static final String file_name = "ferrari.properties";
	
	private static final ConcurrentHashMap<String, String> localcache = new ConcurrentHashMap<String, String>();
	
	public static String getString(String key) {
		if(localcache.containsKey(key)){
			return localcache.get(key);
		}
		Properties prop = loadProperties(file_name);
		if (prop != null) {
			String v = prop.getProperty(key);
			localcache.put(key, v);
			return v;
		}
		return null;
	}
	
	/**
	 * load properties
	 * @param propertyFileName
	 * @param ifClassPath
	 * @return
	 */
	private static Properties loadProperties(String propertyFileName) {
		Properties prop = new Properties();
		InputStreamReader  in = null;
		try {
			URL url = null;
			ClassLoader loder = Thread.currentThread().getContextClassLoader();
			url = loder.getResource(propertyFileName); 
			in = new InputStreamReader(new FileInputStream(url.getPath()), "UTF-8");
			prop.load(in);
		} catch (IOException e) {
			logger.error("load {} error!", propertyFileName);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close {} error!", propertyFileName);
				}
			}
		}
		return prop;
	}
	
	public static void main(String[] args) {
		System.out.println(getString("receive_servletpath"));
		System.out.println(getString("receive_servletpath"));
	}

}
