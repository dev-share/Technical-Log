package com.canal;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import com.ucloudlink.canal.pojo.MonitorInfo;
/**
 * @decription Canal服务(MySQL数据库监控)
 * @author yi.zhang
 * @time 2017年6月1日 上午10:09:03
 * @since 1.0
 * @jdk 1.8
 */
public class CanalFactory {
	private static Logger log = LogManager.getLogger(CanalFactory.class);
	private static CanalConnector connector;
	private static int BATCH_SIZE = 1000;
	static{
		try {
			log.info("-----------------Canal 初始化-----------------");
			init();
			log.info("-----------------Canal Service启动成功-----------------");
		} catch (Exception e) {
			close();
			log.error("-----------------Canal Service启动失败-----------------",e);
			System.exit(1);
		}
	}
	/**
	 * @description Canal服务配置
	 * @author yi.zhang
	 * @time 2017年4月19日 上午10:38:42
	 * @throws Exception
	 */
	protected static void init() throws Exception{
		String destination = CanalConfig.getProperty("canal.destination");
		String servers = CanalConfig.getProperty("canal.address");
		String username = CanalConfig.getProperty("canal.username");
		String password = CanalConfig.getProperty("canal.password");
		boolean isZookeeper = Boolean.valueOf(CanalConfig.getProperty("canal.zookeeper.enabled"));
		String batch_size = CanalConfig.getProperty("canal.batch_size");
		if(batch_size!=null){
			BATCH_SIZE = Integer.valueOf(batch_size);
		}
		if(isZookeeper){
			connector = CanalConnectors.newClusterConnector(servers, destination, username, password);
		}else{
			List<SocketAddress> addresses = new ArrayList<SocketAddress>();
			for(String address : servers.split(",")){
				String[] ips = address.split(":");
				String ip = ips[0];
				int port=11111;
				if(ips.length>1){
					port = Integer.valueOf(ips[1]);
				}
				addresses.add(new InetSocketAddress(ip, port));
			}
			connector = CanalConnectors.newClusterConnector(addresses, destination, username, password);
		}
		connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();
	}
	/**
	 * 关闭服务
	 */
	public static void close(){
		if(connector!=null){
			connector.disconnect();
		}
	}
	/**
	 * 提交数据
	 * @param batchId
	 */
	public static void ack(long batchId){
		connector.ack(batchId);
	}
	/**
	 * 回滚数据
	 * @param batchId
	 */
	public static void rollback(long batchId){
		connector.rollback(batchId);
	}
	/**
	 * @decription 监控数据
	 * @author yi.zhang
	 * @time 2017年6月1日 上午10:10:52
	 * @return
	 */
	public List<MonitorInfo> execute(){
		List<MonitorInfo> monitors = new ArrayList<MonitorInfo>();
		Message message = connector.getWithoutAck(BATCH_SIZE); // 获取指定数量的数据
        long batchId = message.getId();
        List<Entry> list = message.getEntries();
        if(list!=null&&list.size()>0){
        	for (Entry entry : list) {
        		if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                    continue;
                }
                RowChange event = null;
                try {
                	event = RowChange.parseFrom(entry.getStoreValue());
                } catch (Exception e) {
                    throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
                }
                String schema = entry.getHeader().getSchemaName();
                String table = entry.getHeader().getTableName();
                String type = event.hasEventType()?event.getEventType().name():null;
                String sql = event.getSql();
                System.out.println("-----{schema:"+schema+",table:"+table+",type:"+type+",sql:"+sql+"}");
                MonitorInfo monitor = new MonitorInfo();
                monitor.setSchema(schema);
                monitor.setTable(table);
                monitor.setType(type);
                monitor.setSql(sql);
                List<MonitorInfo.RowInfo> rows = monitor.getRows();
                for (RowData rowData : event.getRowDatasList()) {
                	MonitorInfo.RowInfo row = monitor.new RowInfo();
                	JSONObject before = row.getBefore();
                	JSONObject after = row.getAfter();
                	JSONObject change = row.getChange();
                	List<Column> cbefores = rowData.getBeforeColumnsList();
                	List<Column> cafters = rowData.getAfterColumnsList();
                    for (Column column : cbefores) {
                    	String key = column.getName();
                    	String value = column.getValue();
			String ctype = column.getMysqlType().toLowerCase();
                    	if(ctype.contains("int")){
                    		if(ctype.contains("bigint")){
                    			value = Long.valueOf(column.getValue());
                    		}else{
                    			value = Integer.valueOf(column.getValue());
                    		}
                    	}
                    	if(ctype.contains("decimal")||ctype.contains("numeric")||ctype.contains("double")||ctype.contains("float")){
                    		value = Double.valueOf(column.getValue());
                    	}
                    	if(ctype.contains("timestamp")||ctype.contains("date")){
                    		if(ctype.contains("timestamp")){
                    			value = DateUtil.formatDateTime(column.getValue());
                    		}else{
                    			value = DateUtil.formatDate(column.getValue());
                    		}
                    	}
                    	boolean update = column.getUpdated();
                    	before.put(key, value);
                    	if(update){
                    		change.put(key, value);
                    	}
                        System.out.println("--"+type+"--before----{"+key+ ": " + value + ",update: " + update+"}");
                    }
                    for (Column column : cafters) {
                    	String key = column.getName();
                    	String value = column.getValue();
                    	boolean update = column.getUpdated();
                    	after.put(key, value);
                    	if(update){
                    		change.put(key, value);
                    	}
                        System.out.println("--"+type+"--after----{"+key+ ": " + value + ",update: " + update+"}");
                    }
                    rows.add(row);
                }
                monitors.add(monitor);
			}
        }
        ack(batchId);
       return monitors; 
	}
}
