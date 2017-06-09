package com.ucloudlink.canal.common.elasticsearch.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ucloudlink.canal.common.CanalConfig;
import com.ucloudlink.canal.common.elasticsearch.ElasticsearchFactory;
import com.ucloudlink.canal.util.HttpUtil;

public class ElasticsearchHttpFactory implements ElasticsearchFactory {
	private static Logger logger = LogManager.getLogger(ElasticsearchHttpFactory.class);
	private static String regex = "[-,:,/\"]";
	/**
	 * 认证信息(username:password)
	 */
	private static String auth=null;
	/**
	 * 服务地址
	 */
	private static String address=null;
	/**
	 * 服务器端口
	 */
	private static int port=9200;
	/**
	 * http访问API
	 */
	private static String api="http://localhost:9200";
	static{
		try {
			logger.info("-----------------Elasticsearch 初始化-----------------");
			init();
			logger.info("-----------------Elasticsearch Service启动成功-----------------");
		} catch (Exception e) {
			logger.error("-----------------Elasticsearch Service启动失败-----------------",e);
			System.exit(1);
		}
	}
	/**
	 * @description Elasticsearch服务配置
	 * @author yi.zhang
	 * @time 2017年4月19日 上午10:38:42
	 * @throws Exception
	 */
	protected static void init() throws Exception{
//		String clusterName = CanalConfig.getProperty("elasticsearch.cluster.name");
		String servers = CanalConfig.getProperty("elasticsearch.cluster.servers");
		String username = CanalConfig.getProperty("elasticsearch.cluster.username");
		String password = CanalConfig.getProperty("elasticsearch.cluster.password");
		if(username!=null&&password!=null){
			auth = username+":"+password;
		}
		for(String server : servers.split(",")){
			String[] addresses = server.split(":");
			address = addresses[0];
			port=9200;
			if(addresses.length>1){
				port = Integer.valueOf(addresses[1]);
			}
			api = "http://"+address+":"+port;
			boolean flag = HttpUtil.checkConnection(api,auth);
			if(flag){
				logger.info("--------------Elasticsearch["+api+"] connect success-------------");
				break;
			}else{
				logger.warn("--------------Elasticsearch["+api+"] connect error-------------");
			}
		}
	}
	
	public String base(String url,String method,String body){
		try {
			if(url!=null&&!(url.startsWith("http://")||url.startsWith("https://"))){
				url= api+(url.startsWith("/")?"":"/")+url;
			}
			if(body!=null){
				body = url.contains("_bulk")?body:JSON.parseObject(body).toJSONString();
			}
			String result  = HttpUtil.urlRequest(url, method, body, auth);
			if(result!=null&&result.contains("Connection refused")){
				init();
			}
			return result;
		} catch (Exception e) {
			logger.error("----Elasticsearch RESTFull[http api] 访问失败!------------",e);
		}
		return null;
	}
}
