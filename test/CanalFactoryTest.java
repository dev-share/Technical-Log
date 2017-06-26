package com.ucloudlink.canal;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import com.ucloudlink.canal.common.CanalFactory;
import com.ucloudlink.canal.common.elasticsearch.transport.ElasticsearchTransportFactory;
import com.ucloudlink.canal.common.greenplum.GreenplumFactory;
import com.ucloudlink.canal.common.mongodb.MongoDBFactory;
import com.ucloudlink.canal.pojo.MonitorInfo;
import com.ucloudlink.canal.pojo.MonitorInfo.RowInfo;
import com.ucloudlink.canal.util.DateUtil;

public class CanalFactoryTest {

	public static void test1(){
		String ip = "127.0.0.1";
		int port = 11111;
		String username = "canal";
		String password = "canal";
		String destination = "example";
        // 创建链接  
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip, port), destination,username, password);  
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            int totalEmptyCount = 1200;
            while (emptyCount < totalEmptyCount) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
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
                        EventType eventType = event.getEventType();
                        String schema = entry.getHeader().getSchemaName();
                        String table = entry.getHeader().getTableName();
                        String type = eventType.name();
                        String sql = event.getSql();
                        System.out.println("-----{schema:"+schema+",table:"+table+",type:"+type+",sql:"+sql+"}");
                        for (RowData rowData : event.getRowDatasList()) {
                        	List<Column> befores = rowData.getBeforeColumnsList();
                        	List<Column> afters = rowData.getAfterColumnsList();
                            for (Column column : befores) {
                            	String key = column.getName();
                            	String value = column.getValue();
                            	boolean update = column.getUpdated();
                            	System.out.println("--"+type+"--before----{"+key+ ": " + value + ",update: " + update+","+column.getSqlType()+":"+column.getMysqlType()+":"+column.getLength()+"}");
                            }
                            for (Column column : afters) {
                            	String key = column.getName();
                            	String value = column.getValue();
                            	boolean update = column.getUpdated();
                                System.out.println("--"+type+"--after----{"+key+ ": " + value + ",update: " + update+"}");
                            }
                        }
        			}
                }else{
                	System.out.println("-----{totalEmptyCount:"+totalEmptyCount+",emptyCount:"+emptyCount+",batchId:"+batchId+"}");
                	try {
                		totalEmptyCount++;
						Thread.sleep(5*1000l);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                emptyCount++;
                connector.ack(batchId); // 提交确认
//                connector.rollback(batchId); // 处理失败, 回滚数据
            }

            System.out.println("empty too many times, exit");
        } finally {
            connector.disconnect();
        }
	}
	
	public static void test2(){
		boolean flag = true;
		while(flag){
			long start = System.currentTimeMillis();
			CanalFactory factory = new CanalFactory();  
			List<MonitorInfo> monitors = factory.service();
			System.out.println("-------------------------Canal[开始]---------------------");
			try {
				System.out.println(JSON.toJSONString(monitors));
				ElasticsearchTransportFactory tfactory = new ElasticsearchTransportFactory();
				for (MonitorInfo monitor : monitors) {
					String index = monitor.getSchema();
					String type = monitor.getTable();
					List<RowInfo> datas = monitor.getRows();
					for (RowInfo row : datas) {
						JSONObject json = row.getBefore().isEmpty()?row.getAfter():row.getBefore();
						if(json!=null){
							for(String key:json.keySet()){
								Object value = json.get(key);
								if(value instanceof Date){
									value = Long.valueOf(DateUtil.formatDateTimeStr((Date)value, "yyyyMMddHHmmss"));
									json.fluentPut(key, value);
								}
							}
						}
						String id = json.getString(row.getKid());
						if("delete".equalsIgnoreCase(monitor.getType())){
							tfactory.delete(index, type, id);
						}else{
							tfactory.upsert(index, type, id, json.toJSONString());
						}
					}
				}
				long end = System.currentTimeMillis();
				double time = (end-start)/1000.0000;
				System.out.println("-------耗时[单位:秒]:"+time);
				Thread.sleep(5*1000l);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("-------------------------Canal[结束]---------------------");
		}
	}
	public static void test3(){
		String table = "css_vsim_cdr";
		long start = System.currentTimeMillis();
		MongoDBFactory factory = new MongoDBFactory();
//		GreenplumFactory tfactory = new GreenplumFactory();
//		JSONObject obj = new JSONObject();
//		obj.fluentPut("_id", "222");
//		obj.fluentPut("mvno_code", "100");
//		obj.fluentPut("org_code", "A100");
//		obj.fluentPut("imsi", "13699998888");
//		obj.fluentPut("imei", "13699998888");
//		obj.fluentPut("start_time", new Date());
//		obj.fluentPut("end_time", new Date(new Date().getTime()+60*60*1000l));
//		obj.fluentPut("user_code", "test");
//		obj.fluentPut("device_type", "G2");
//		obj.fluentPut("device_version", "G2_LTSV1.1.001.028.151226");
//		obj.fluentPut("visit_mcc", 460);
//		obj.fluentPut("flow_size", 123456789);
//		obj.fluentPut("session_id", "");
//		obj.fluentPut("visitcountry", "");
//		obj.fluentPut("createtime", new Date());
//		factory.save(table, obj);
		List<JSONObject> list = (List<JSONObject>)factory.executeQuery("css_vsim_cdr", null, null);
		for (JSONObject json : list) {
			System.out.println(json.toJSONString());
//			String keys = "";
//        	String values = "";
//        	for (String key : json.keySet()) {
//        		Object value = json.get(key);
//        		if(value instanceof Date){
//        			value = DateUtil.formatDateTimeStr((Date)value);
//        		}
//				if("".equals(keys)){
//					keys = key;
//					values = (value instanceof String?"'"+value+"'":value+"");
//				}else{
//					keys +=',' + key;
//					values +=',' + (value instanceof String?"'"+value+"'":value+"");
//				}
//			}
//        	String sql = "insert into "+table+"("+keys+")values("+values+");";
//        	tfactory.excuteUpdate(sql);
		}
		long end = System.currentTimeMillis();
		double time = (end-start)/1000.0000;
		System.out.println("-------耗时[单位:秒]:"+time);
	}
	public static void main(String args[]) {
//		test1();
//		test2();
		test3();
    }

}
