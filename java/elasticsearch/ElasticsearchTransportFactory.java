package com.ucloudlink.canal.common.elasticsearch.transport;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ucloudlink.canal.common.CanalConfig;
import com.ucloudlink.canal.common.elasticsearch.ElasticsearchFactory;

public class ElasticsearchTransportFactory implements ElasticsearchFactory{
	private static Logger logger = LogManager.getLogger(ElasticsearchTransportFactory.class);
	private static Client client=null;
	private static String regex = "[-,:,/\"]";
	static{
		try {
			logger.info("-----------------Elasticsearch 初始化-----------------");
			init();
//			config();
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
		String clusterName = CanalConfig.getProperty("elasticsearch.cluster.name");
		String servers = CanalConfig.getProperty("elasticsearch.cluster.servers");
		String user = CanalConfig.getProperty("elasticsearch.cluster.user");
		String password = CanalConfig.getProperty("elasticsearch.cluster.password");
		Settings settings = Settings.builder()
				.put("cluster.name", clusterName)
//				.put("shield.user",user+":"+password)
				.put("client.transport.sniff", true).build();
		TransportClient xclient = new PreBuiltTransportClient(settings);
		for(String server : servers.split(",")){
			String[] address = server.split(":");
			String ip = address[0];
			int port=9300;
			if(address.length>1){
				port = Integer.valueOf(address[1]);
			}
			xclient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
		}
		client = xclient;
	}
	
	public static String select(String index,String type,String id){
		GetResponse result = client.prepareGet(index, type, id).execute().actionGet();
		return result.getSourceAsString();
	}
	public static String delete(String index,String type,String id){
		DeleteResponse result = client.prepareDelete(index, type, id).execute().actionGet();
		System.out.println(JSON.toJSONString(result));
		return result.toString();
	}
	
	public static String insert(String index,String type,String json){
		IndexResponse response = client.prepareIndex(index, type).setSource(json,XContentType.JSON).execute().actionGet();
		if(response.getResult().equals(Result.CREATED)){
			System.out.println(JSON.toJSONString(response));
		}
		return response.toString();
	}
	public static String update(String index,String type,String id,String json){
		UpdateResponse result = client.prepareUpdate(index, type, id).setDoc(json,XContentType.JSON).execute().actionGet();
		System.out.println(JSON.toJSONString(result));
		return result.toString();
	}
	public static String upsert(String index,String type,String id,String json){
		try {
			IndexRequest indexRequest = new IndexRequest(index, type, id).source(json,XContentType.JSON);
			UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(json,XContentType.JSON).upsert(indexRequest);              
			UpdateResponse result = client.update(updateRequest).get();
			return result.toString();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String bulkUpsert(String index,String type,List<String> jsons){
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (String json : jsons) {
				JSONObject obj = JSON.parseObject(json);
				String id = UUIDs.base64UUID();
				if(obj.containsKey("id")){
					id = obj.getString("id");
					obj.remove("id");
					bulkRequest.add(client.prepareUpdate(index, type, id).setDoc(obj.toJSONString(),XContentType.JSON));
				}else{
					bulkRequest.add(client.prepareIndex(index, type, id).setSource(obj.toJSONString(),XContentType.JSON));
				}
			}
			BulkResponse result = bulkRequest.execute().get();
			return result.toString();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String selectAll(String indexs,String types,String condition){
		SearchResponse response = client.prepareSearch(indexs.split(","))
		        .setTypes(types.split(","))
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.queryStringQuery(condition))                 // Query
		        .setFrom(0).setSize(60).setExplain(true)
		        .get();
		return response.toString();
	}
	public static String selectAll(String indexs,String types,Map<String,String> params){
		String body = "";
		for (String key : params.keySet()) {
			String value = params.get(key);
			body+="+"+key+":"+value;
		}
		SearchResponse response = client.prepareSearch(indexs.split(","))
				.setTypes(types.split(","))
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.queryStringQuery(body))                 // Query
				.setFrom(0).setSize(60).setExplain(true)
				.get();
		return response.toString();
	}
	public static String selectMatchAll(String indexs,String types,String field,String value){
		SearchResponse response = client.prepareSearch(indexs.split(","))
				.setTypes(types.split(","))
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchQuery(field, value))
				.highlighter(new HighlightBuilder().field(field))
				.addAggregation(AggregationBuilders.terms("data").field(field+".keyword"))
				.setFrom(0).setSize(60).setExplain(true)
				.get();
		return response.toString();
	}
	public static String selectMatchAll(String indexs,String types,Map<String,String> params){
		BoolQueryBuilder boolquery = QueryBuilders.boolQuery();
		HighlightBuilder highlight = new HighlightBuilder();
		for (String key: params.keySet()) {
			String value = params.get(key);
			boolquery.should(QueryBuilders.matchQuery(key, value));
			highlight.field(key);
		}
		SearchResponse response = client.prepareSearch(indexs.split(","))
				.setTypes(types.split(","))
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(boolquery)
				.highlighter(highlight)
				.setExplain(true)
				.get();
		return response.toString();
	}
}
