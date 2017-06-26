package com.ucloudlink.canal.common.mongodb;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.ucloudlink.canal.common.CanalConfig;

/**
 * @decription MongoDB数据服务封装
 * @author yi.zhang
 * @time 2017年6月2日 下午2:48:49
 * @since 1.0
 * @jdk 1.8
 */
@SuppressWarnings("all")
public class MongoDBFactory {

	private static MongoDatabase session = null;
	static {
		init();
	}

	/**
	 * @decription 初始化配置
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:15:57
	 */
	private static void init() {
		try {
			String servers = CanalConfig.getProperty("mongodb.servers");
			String database = CanalConfig.getProperty("mongodb.database");
			String schema = CanalConfig.getProperty("mongodb.schema");
			String username = CanalConfig.getProperty("mongodb.username");
			String password = CanalConfig.getProperty("mongodb.password");

			List<ServerAddress> saddress = new ArrayList<ServerAddress>();
			if (servers != null && !"".equals(servers)) {
				for (String server : servers.split(",")) {
					String[] address = server.split(":");
					String ip = address[0];
					int port = 27017;
					if (address != null && address.length > 1) {
						port = Integer.valueOf(address[1]);
					}
					saddress.add(new ServerAddress(ip, port));
				}
			}
			MongoCredential credential = MongoCredential.createScramSha1Credential(username, database,password.toCharArray());
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);
			// 通过连接认证获取MongoDB连接
			MongoClient client = new MongoClient(saddress, credentials, new MongoClientOptions.Builder().build());
			// 连接到数据库
			session = client.getDatabase(schema);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @decription 保存数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:18:49
	 * @param collectionName	文档名称(表名)
	 * @param obj
	 * @return
	 */
	public int save(String collectionName, Object obj) {
		try {
			MongoCollection<Document> collection = session.getCollection(collectionName);
			if (collection == null) {
				session.createCollection(collectionName);
				collection = session.getCollection(collectionName);
			}
			collection.insertOne(Document.parse(JSON.toJSONString(obj)));
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @decription 更新数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:19:08
	 * @param collectionName	文档名称(表名)
	 * @param obj
	 * @return
	 */
	public int update(String collectionName, Object obj) {
		try {
			MongoCollection<Document> collection = session.getCollection(collectionName);
			if (collection == null) {
				return 0;
			}
			JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
			collection.updateOne(Filters.eq("_id", json.get("id")), Document.parse(JSON.toJSONString(obj)));
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @decription 删除数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:19:25
	 * @param collectionName	文档名称(表名)
	 * @param obj
	 * @return
	 */
	public int delete(String collectionName, Object obj) {
		try {
			MongoCollection<Document> collection = session.getCollection(collectionName);
			if (collection == null) {
				return 0;
			}
			JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
			collection.findOneAndDelete(Filters.eq("_id", json.get("id")));
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @decription 数据库查询
	 * @author yi.zhang
	 * @time 2017年6月26日 下午4:12:59
	 * @param collectionName	文档名称(表名)
	 * @param clazz		映射对象
	 * @param params	参数
	 * @return
	 */
	public List<?> executeQuery(String collectionName, Class clazz, JSONObject params) {
		try {
			MongoCollection<Document> collection = session.getCollection(collectionName);
			if (collection == null) {
				return null;
			}
			List<Object> list = new ArrayList<Object>();
			FindIterable<Document> documents = null;
			if (params != null) {
				List<Bson> filters = new ArrayList<Bson>();
				for (String key : params.keySet()) {
					Object value = params.get(key);
					filters.add(Filters.eq(key, value));
				}
				documents = collection.find(Filters.and(filters));
			} else {
				documents = collection.find();
			}
			MongoCursor<Document> cursor = documents.iterator();
			while (cursor.hasNext()) {
				JSONObject obj = new JSONObject();
				Document document = cursor.next();
				for (String column : document.keySet()) {
					Object value = document.get(column);
					if (clazz == null) {
						obj.put(column, value);
					} else {
						String tcolumn = column.replaceAll("_", "");
						Field[] fields = clazz.getDeclaredFields();
						for (Field field : fields) {
							String tfield = field.getName();
							if (column.equalsIgnoreCase(tfield) || tcolumn.equalsIgnoreCase(tfield)) {
								obj.put(tfield, value);
								break;
							}
						}
					}
				}
				Object object = obj;
				if (clazz != null) {
					object = JSON.parseObject(obj.toJSONString(), clazz);
				}
				list.add(object);
			}
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
