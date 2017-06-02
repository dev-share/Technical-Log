package com.ucloudlink.canal.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemConfig {
	private static Logger log = LogManager.getLogger(CanalConfig.class);
	private static Properties config = null;
	static{
		init();
	}
	private static void init(){
		InputStream in = null; 
		try {
			in = ClassLoader.getSystemResourceAsStream("canal.properties");
			config = new Properties();
			config.load(in);
		} catch (IOException e) {
			log.error("--Canal Properties read error!",e);
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (Exception e) {
					log.error("--Canal InputStream read error!",e);
				}
			}
		}
	}
	
	public static String getProperty(String key) throws Exception{
		return config.getProperty(key);
	}
}
