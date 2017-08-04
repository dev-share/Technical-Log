package com.share.common.elasticsearch.http;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.share.common.elasticsearch.ElasticsearchFactory;
import com.share.util.HttpUtil;

public class ElasticsearchHttpFactory implements ElasticsearchFactory {
	private static Logger logger = LogManager.getLogger();
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
	
	private String servers;
	private String username;
	private String password;
	
	
	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @description Elasticsearch服务配置
	 * @author yi.zhang
	 * @time 2017年4月19日 上午10:38:42
	 * @throws Exception
	 */
	public void init(String servers,String username,String password){
		try {
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
		} catch (Exception e) {
			logger.error("-----Elasticsearch Config init Error-----", e);
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
				init(servers, username, password);
			}
			return result;
		} catch (Exception e) {
			logger.error("----Elasticsearch RESTFull[http api] 访问失败!------------",e);
		}
		return null;
	}
}