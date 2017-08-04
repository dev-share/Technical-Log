package com.share.common.greenplum;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.share.common.jdbc.JDBCFactory;


/**
 * @decription 数据仓库(Greenplum)服务封装
 * @author yi.zhang
 * @time 2017年6月2日 下午2:45:10
 * @since 1.0
 * @jdk 1.8
 */
public class GreenplumFactory extends JDBCFactory {
	private static Logger logger = LogManager.getLogger();
	public static String GREENPLUM_SCHEMA = null;
	private String address;
	private String database;
	private String schema;
	private String username;
	private String password;
	private boolean isDruid;
	private int max_pool_size=100;
	private int init_pool_size=10;
	
	/**
	 * @decription 初始化配置
	 * @author yi.zhang
	 * @time 2017年6月2日 下午2:15:57
	 */
	public void init(String address,String database,String schema,String username,String password,boolean isDruid,Integer max_pool_size,Integer init_pool_size){
		try {
			String driverName = "org.postgresql.Driver";
			GREENPLUM_SCHEMA = schema;
			String url = "jdbc:postgresql://"+address+"/"+database;
			super.init(driverName, url, username, password, isDruid, max_pool_size, init_pool_size);
		} catch (Exception e) {
			logger.error("-----Greenplum Config init Error-----", e);
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
			if(connect==null){
				this.init(address, database, schema, username, password, isDruid, max_pool_size, init_pool_size);
			}
			return super.excuteUpdate(handleSQL(sql), params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
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
	@SuppressWarnings("rawtypes")
	public List<?> executeQuery(String sql,Class clazz,Object...params){
		try {
			if(connect==null){
				this.init(address, database, schema, username, password, isDruid, max_pool_size, init_pool_size);
			}
			return super.executeQuery(handleSQL(sql),clazz, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
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
			if(connect==null){
				this.init(address, database, schema, username, password, isDruid, max_pool_size, init_pool_size);
			}
			if(GREENPLUM_SCHEMA!=null&&!table.contains(GREENPLUM_SCHEMA+".")){
				table = GREENPLUM_SCHEMA+"."+table;
			}
			return super.queryColumns(table);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @decription SQL语句处理
	 * @author yi.zhang
	 * @time 2017年6月30日 下午3:24:58
	 * @param sql	SQL语句
	 * @return
	 */
	private String handleSQL(String sql){
		if(GREENPLUM_SCHEMA!=null&&!sql.contains(GREENPLUM_SCHEMA+".")){
			sql= sql.trim();
			if(sql.toLowerCase().startsWith("insert")){
				String temp = sql.substring(0, sql.indexOf("("));
				String table = temp.substring(temp.lastIndexOf(" ")+1);
				sql = sql.replaceFirst(table, GREENPLUM_SCHEMA+"."+table);
			}
			if(sql.toLowerCase().startsWith("update")){
				String temp = sql.substring(0, sql.toLowerCase().indexOf("set"));
				String table = temp.substring(temp.indexOf(" ")+1);
				sql = sql.replaceFirst(table, GREENPLUM_SCHEMA+"."+table);
			}
			if(sql.toLowerCase().startsWith("select")||sql.toLowerCase().startsWith("delete")){
				String temp = sql.substring(sql.toLowerCase().indexOf("from")).trim();
				String table = !temp.contains(" ")?temp.substring(0):temp.substring(0,temp.toLowerCase().contains("where")?temp.toLowerCase().indexOf("where"):temp.indexOf(" "));
				sql = sql.replaceFirst(table, GREENPLUM_SCHEMA+"."+table);
			}
		}
		return sql;
	}
}