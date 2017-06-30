package com.ucloudlink.canal.common.greenplum;

import java.util.List;

import com.ucloudlink.canal.common.CanalConfig;
import com.ucloudlink.canal.common.jdbc.JDBCFactory;
/**
 * @decription 数据仓库(Greenplum)服务封装
 * @author yi.zhang
 * @time 2017年6月2日 下午2:45:10
 * @since 1.0
 * @jdk 1.8
 */
public class GreenplumFactory extends JDBCFactory {
	public static String GREENPLUM_SCHEMA = null;
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
			String driverName = "org.postgresql.Driver";
			String address = CanalConfig.getProperty("greenplum.address");
			String database = CanalConfig.getProperty("greenplum.database");
			GREENPLUM_SCHEMA = CanalConfig.getProperty("greenplum.schema");
			String url = "jdbc:postgresql://"+address+"/"+database;
			String username = CanalConfig.getProperty("greenplum.username");
			String password = CanalConfig.getProperty("greenplum.password");
			config(driverName, url, username, password, true, 100, 10);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return super.excuteUpdate(handleSQL(sql), params);
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
		return super.executeQuery(handleSQL(sql),clazz, params);
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
