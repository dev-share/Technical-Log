package com.ucloudlink.canal.common.cassandra;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Mapper.Option;
import com.datastax.driver.mapping.MappingManager;
import com.ucloudlink.canal.common.CanalConfig;

/**
 * @decription Cassandra数据服务封装
 * @author yi.zhang
 * @time 2017年6月2日 下午2:48:49
 * @since 1.0
 * @jdk 1.8
 */
@SuppressWarnings("all")
public class CassandraFactory {
	/**
	 * 过期时间(单位:秒)
	 */
	private static int EXPIRE_TIME = 15 * 24 * 60 * 60;

	private static Session session = null;
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
			String servers = CanalConfig.getProperty("cassandra.servers");
			String keyspace = CanalConfig.getProperty("cassandra.keyspace");
			String username = CanalConfig.getProperty("cassandra.username");
			String password = CanalConfig.getProperty("cassandra.password");

			PoolingOptions options = new PoolingOptions();
			// options.setMaxRequestsPerConnection(HostDistance.LOCAL, 32);
			// options.setMaxRequestsPerConnection(HostDistance.REMOTE, 32);
			// options.setCoreConnectionsPerHost(HostDistance.LOCAL, 2);
			// options.setCoreConnectionsPerHost(HostDistance.REMOTE, 2);
			// options.setMaxConnectionsPerHost(HostDistance.LOCAL, 4);
			// options.setMaxConnectionsPerHost(HostDistance.REMOTE, 4);
			options.setHeartbeatIntervalSeconds(60);
			options.setIdleTimeoutSeconds(120);
			options.setPoolTimeoutMillis(5 * 1000);
			List<InetSocketAddress> saddress = new ArrayList<InetSocketAddress>();
			if (servers != null && !"".equals(servers)) {
				for (String server : servers.split(",")) {
					String[] address = server.split(":");
					String ip = address[0];
					int port = 9042;
					if (address != null && address.length > 1) {
						port = Integer.valueOf(address[1]);
					}
					saddress.add(new InetSocketAddress(ip, port));
				}
			}
			InetSocketAddress[] addresses = new InetSocketAddress[saddress.size()];
			saddress.toArray(addresses);
			Builder builder = Cluster.builder();
			builder.withPoolingOptions(options);
			builder.addContactPointsWithPorts(addresses);
			builder.withCredentials(username, password);
			Cluster cluster = builder.build();
			if (keyspace != null && !"".equals(keyspace)) {
				session = cluster.connect(keyspace);
			} else {
				session = cluster.connect();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @decription 保存数据
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:18:49
	 * @param obj
	 * @return
	 */
	public int save(Object obj) {
		try {
			Mapper mapper = new MappingManager(session).mapper(obj.getClass());
			mapper.save(obj, Option.saveNullFields(true));
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
	 * @param obj
	 * @return
	 */
	public int update(Object obj) {
		try {
			Mapper mapper = new MappingManager(session).mapper(obj.getClass());
			mapper.save(obj, Option.saveNullFields(false),Option.ttl(EXPIRE_TIME));
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
	 * @param obj
	 * @return
	 */
	public int delete(Object obj) {
		try {
			Mapper mapper = new MappingManager(session).mapper(obj.getClass());
			mapper.delete(obj);
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @decription 数据操作(Insert|Update|Delete)
	 * @author yi.zhang
	 * @time 2017年6月2日 下午6:19:40
	 * @param cql
	 * @param params
	 * @return
	 */
	public int executeUpdate(String cql, Object... params) {
		try {
			ResultSet rs = session.execute(cql, params);
			return rs.getAvailableWithoutFetching();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @decription 数据库查询(Select)
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:16:12
	 * @param cql
	 *            cql语句
	 * @param params
	 *            占位符参数
	 * @param clazz
	 *            映射对象
	 * @return
	 */
	public List<?> executeQuery(String cql, Class clazz, Object... params) {
		try {
			List<Object> list = new ArrayList<Object>();
			ResultSet rs = session.execute(cql, params);
			ColumnDefinitions rscd = rs.getColumnDefinitions();
			int count = rscd.size();
			Map<String, String> reflect = new HashMap<String, String>();
			for (int i = 0; i < count; i++) {
				String column = rscd.getName(i);
				String tcolumn = column.replaceAll("_", "");
				if (clazz == null) {
					reflect.put(column, column);
				} else {
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						String tfield = field.getName();
						if (tcolumn.equalsIgnoreCase(tfield)) {
							reflect.put(column, tfield);
							break;
						}
					}
				}
			}
			for (Row row : rs.all()) {
				JSONObject obj = new JSONObject();
				for (String column : reflect.keySet()) {
					String key = reflect.get(column);
					Object value = row.getObject(column);
					obj.put(key, value);
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
