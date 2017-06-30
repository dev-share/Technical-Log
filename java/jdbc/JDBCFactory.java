package com.ucloudlink.canal.common.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ucloudlink.canal.common.CanalConfig;
/**
 * @decription 数据库(MySQL|SQL Server|Oracle|Postgresql)服务封装
 * @author yi.zhang
 * @time 2017年6月2日 下午2:14:31
 * @since 1.0
 * @jdk 1.8
 */
public class JDBCFactory {
	private static Connection connect = null;
	static{
		init();
	}
	/**
	 * @decription 初始化配置
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:15:57
	 */
	private static void init(){
		try {
			String driverName = CanalConfig.getProperty("jdbc.driver");
			String url = CanalConfig.getProperty("jdbc.url");
			String username = CanalConfig.getProperty("jdbc.username");
			String password = CanalConfig.getProperty("jdbc.password");
			boolean isDruid = Boolean.valueOf(CanalConfig.getProperty("jdbc.druid.enabled"));
			String max_pool_size = CanalConfig.getProperty("jdbc.druid.max_pool_size");
			String init_pool_size = CanalConfig.getProperty("jdbc.druid.init_pool_size");
			config(driverName, url, username, password, isDruid, Integer.valueOf(max_pool_size), Integer.valueOf(init_pool_size));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @decription 数据库或数据仓库配置
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:15:57
	 */
	public static void config(String driverName,String url,String username,String password,boolean isDruid,Integer max_pool_size,Integer init_pool_size) throws Exception{
		if(isDruid){
			@SuppressWarnings("resource")
			DruidXADataSource dataSource = new DruidXADataSource();
			dataSource.setDriverClassName(driverName);
			dataSource.setUrl(url);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			if(max_pool_size!=null&&max_pool_size>0){
				dataSource.setMaxActive(max_pool_size);
			}
			if(init_pool_size!=null&&init_pool_size>0){
				dataSource.setInitialSize(init_pool_size);
			}
			dataSource.init();
			connect = dataSource.getConnection();
		}else{
			Class.forName(driverName);
			connect = DriverManager.getConnection(url,username,password);
		}
	}
	/**
	 * @decription 数据库操作(Insert|Update|Delete)
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:16:12
	 * @param sql		sql语句
	 * @param params	占位符参数
	 * @return
	 */
	public int excuteUpdate(String sql,Object...params ){
		try {
			PreparedStatement ps = connect.prepareStatement(sql);
			if(params!=null&&params.length>0){
				for(int i=1;i<=params.length;i++){
					Object value = params[i-1];
					ps.setObject(i, value);
				}
			}
			int result = ps.executeUpdate();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * @decription 数据库查询(Select)
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:16:12
	 * @param sql		sql语句
	 * @param params	占位符参数
	 * @param clazz		映射对象
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<?> executeQuery(String sql,Class clazz,Object...params){
		try {
			List<Object> list=new ArrayList<Object>();
			PreparedStatement ps = connect.prepareStatement(sql);
			if(params!=null&&params.length>0){
				for(int i=1;i<=params.length;i++){
					Object value = params[i-1];
					ps.setObject(i, value);
				}
			}
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			Map<String,String> reflect = new HashMap<String,String>();
			for(int i=1;i<=count;i++){
				String column = rsmd.getColumnName(i);
				String tcolumn = column.replaceAll("_", "");
				if(clazz==null){
					reflect.put(column, column);
				}else{
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						String tfield = field.getName();
						if(tcolumn.equalsIgnoreCase(tfield)){
							reflect.put(column, tfield);
							break;
						}
					}
				}
			}
			while(rs.next()){
				JSONObject obj = new JSONObject();
				for(String column:reflect.keySet()){
					String key = reflect.get(column);
					Object value = rs.getObject(column);
					obj.put(key, value);
				}
				Object object = obj;
				if(clazz!=null){
					object = JSON.parseObject(obj.toJSONString(), clazz);
				}
				list.add(object);
			}
			rs.close();
			ps.close();
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @decription 查询数据表字段名(key:字段名,value:字段类型名)
	 * @author yi.zhang
	 * @time 2017年6月30日 下午2:16:02
	 * @param table	表名
	 * @return
	 */
	public Map<String,String> queryColumns(String table){
		try {
			String sql = "select * from "+table;
			PreparedStatement ps = connect.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			Map<String,String> reflect = new HashMap<String,String>();
			for(int i=1;i<=count;i++){
				String column = rsmd.getColumnName(i);
				String type = rsmd.getColumnTypeName(i);
				reflect.put(column, type);
			}
			rs.close();
			ps.close();
			return reflect;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
