package com.dev-share.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 描述:XML转化工具
 * 作者:ZhangYi
 * 时间:2016年7月20日 下午5:41:23
 * JDK:1.7.80
 * </pre>
 */
public class XMLUtil {
	/**
	 * <pre>
	 * 描述:XML转JSON对象
	 * 作者:ZhangYi
	 * 时间:2016年7月21日 下午5:04:21
	 * 参数：(参数列表)
	 * @param xml	xml字符串
	 * @return
	 * </pre>
	 */
	public static JSONObject xmlToJSON(String xml) {
		JSONObject json = new JSONObject();
		try {
			Document doc = DocumentHelper.parseText(xml.replace("&amp;", "#;").replace("&", "#;"));
			Element root = doc.getRootElement();
			if (root != null) {
				Object obj = null;
				String key = root.getName();
				if (root.nodeCount() > 0) {
					obj = handleElement(root);
				} else {
					obj = root.getData();
				}
				if (obj instanceof JSONObject) {
					JSONObject data = (JSONObject) obj;
					if (data.containsKey(key)) {
						obj = data.get(key);
					}
				}
				json.put(key, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();// 异常信息
		}
		return json;
	}

	/**
	 * <pre>
	 * 描述:XML解析元素
	 * 作者:ZhangYi
	 * 时间:2016年7月21日 下午5:05:30
	 * 参数：(参数列表)
	 * @param element
	 * @return
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	protected static Object handleElement(Element element) {
		if (element.nodeCount() > 0) {
			if (element.isTextOnly()) {
				Object data = element.getData();
				if (data != null && (data instanceof String)) {
					data = element.getTextTrim().replace("#;", "&");
				}
				return data;
			} else {
				JSONObject json = new JSONObject();
				List<Element> list = element.elements();
				Element node = list.get(0);
				String key = element.getName();
				if (list.size() == 1) {
					String _key = node.getName();
					Object value = handleElement(node);
					if (value instanceof JSONObject) {
						JSONObject data = (JSONObject) value;
						if (data.containsKey(_key)) {
							value = data.get(_key);
						}
					}
					json.put(_key, value);
				} else {
					JSONObject obj = new JSONObject();
					Element next = list.get(1);
					if (node.getName().equals(next.getName())) {
						JSONArray array = new JSONArray();
						String _key = node.getName();
						for (Element _node : list) {
							Object value = handleElement(_node);
							if (value instanceof JSONObject) {
								JSONObject data = (JSONObject) value;
								if (data.containsKey(_key)) {
									array.add(data.get(_key));
								} else {
									String _okey = _node.getName();
									obj.put(_okey, data.get(_okey));
								}
							}
						}
						obj.put(_key, array);
						json.put(key, obj);
					} else {
						for (Element _node : list) {
							String _key = _node.getName();
							Object value = handleElement(_node);
							if (value instanceof JSONObject) {
								JSONObject data = (JSONObject) value;
								if (data.containsKey(_key)) {
									obj.put(_key, data.get(_key));
									continue;
								}
							}
							obj.put(_key, value);
						}
						json.put(key, obj);
					}
				}
				return json;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String xml = "<?xml version='1.0' encoding='UTF-8'?>";
		xml += "<serv:message xmlns:serv='http://www.webex.com/schemas/2002/06/service' xmlns:com='http://www.webex.com/schemas/2002/06/common' xmlns:meet='http://www.webex.com/schemas/2002/06/service/meeting' xmlns:att='http://www.webex.com/schemas/2002/06/service/attendee'>";
		xml += "	<serv:header>";
		xml += "		<serv:response>";
		xml += "			<serv:result>SUCCESS</serv:result>";
		xml += "			<serv:gsbStatus>PRIMARY</serv:gsbStatus>";
		xml += "		</serv:response>";
		xml += "	</serv:header>";
		xml += "	<serv:body>";
		xml += "		<serv:bodyContent xsi:type='meet:gethosturlMeetingResponse' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>";
		xml += "			<meet:hostMeetingURL>https://dev-share.webex.com.cn/dev-share/p.php?AT=LI&TK=5495ff056eeaaafe1ee494107e28573db27e8ee9020a9e145f9e91b235c41c99&MU=https://dev-share.webex.com.cn/dev-share/m.php?AT=HM&MK=180208468&Rnd=0.4129517587427822";
		xml += "			</meet:hostMeetingURL>";
		xml += "		</serv:bodyContent>";
		xml += "	</serv:body>";
		xml += "</serv:message>";
		System.out.println(xmlToJSON(xml));
	}
}
