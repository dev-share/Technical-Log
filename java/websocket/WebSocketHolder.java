package com.wafersystems.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.wafersystems.util.AccessTokenUtils;
import com.wafersystems.util.StringUtil;

/**
 * <pre>
 * 项目:ReserveMeeting
 * 描述:Spring Web Socket消息广播
 * 作者:ZhangYi
 * 时间:2016年8月2日 上午10:40:00
 * 版本:wrm_v4.0
 * JDK:1.7.80
 * </pre>
 */
public class WebSocketHolder {
	private static Logger							logger		= Logger.getLogger(WebSocketHolder.class);
	private static Map<String, WebSocketSession>	sessions	= new HashMap<String, WebSocketSession>();

	/**
	 * <pre>
	 * 描述:推送客户端消息
	 * 作者:ZhangYi
	 * 时间:2016年8月2日 上午10:52:23
	 * 参数：(参数列表)
	 * @param userId	客户端用户UserId
	 * @param content	消息内容
	 * </pre>
	 */
	public static void sendMessage(String userId, String content) {
		logger.info("-----------------------------------发送socket消息[开始]-----------------------------------------------");
		try {
			if (sessions.containsKey(userId)) {
				WebSocketSession session = sessions.get(userId);
				session.sendMessage(new TextMessage(content));
			}
		} catch (IOException e) {
			logger.error("--发送socket消息失败!", e);
		} finally {
			logger.info("-----------------------------------发送socket消息[结束]-----------------------------------------------");
		}
	}

	/**
	 * <pre>
	 * 描述:用户信息与Web Socket对应关系
	 * 作者:ZhangYi
	 * 时间:2016年8月2日 上午10:56:19
	 * 参数：(参数列表)
	 * @param session	Web Socket通道session
	 * </pre>
	 */
	public static String handleSession(WebSocketSession session) {
		if (session != null && session.getUri() != null) {
			String token = "";
			String url = session.getUri().getPath();
			String param = session.getUri().getQuery();
			if (!StringUtil.isEmptyStr(url) && url.contains("token") || !StringUtil.isEmptyStr(param) && param.contains("token")) {
				token = !StringUtil.isEmptyStr(url) && url.contains("token") ? url.substring(url.indexOf("token=") + "token=".length()) : param.substring(param.indexOf("token=") + "token=".length());
				if (!StringUtil.isEmptyStr(token) && token.contains("&")) {
					token = token.substring(0, token.indexOf("&"));
				} else {
					if (!StringUtil.isEmptyStr(token) && token.contains("/" + session.getId())) {
						token = token.substring(0, token.indexOf("/" + session.getId()));
					}
				}
			} else {
				if (session.getAttributes() != null && session.getAttributes().containsKey("token")) {
					token = session.getAttributes().get("token").toString();
				}
			}
			if (!StringUtil.isEmptyStr(token)) {
				String userId = AccessTokenUtils.decryptToken(token);
				sessions.put(userId, session);
				return userId;
			}
		}
		return null;
	}

	/**
	 * <pre>
	 * 描述:移除session
	 * 作者:ZhangYi
	 * 时间:2016年8月2日 上午11:07:46
	 * 参数：(参数列表)
	 * @param session	Web Socket通道session
	 * </pre>
	 */
	public static void removeSession(WebSocketSession session) {
		if (session != null && session.getUri() != null) {
			String token = "";
			String url = session.getUri().getPath();
			String param = session.getUri().getQuery();
			if (!StringUtil.isEmptyStr(url) && url.contains("token") || !StringUtil.isEmptyStr(param) && param.contains("token")) {
				token = !StringUtil.isEmptyStr(url) && url.contains("token") ? url.substring(url.indexOf("token=") + "token=".length()) : param.substring(param.indexOf("token=") + "token=".length());
				if (!StringUtil.isEmptyStr(token) && token.contains("&")) {
					token = token.substring(0, token.indexOf("&"));
				} else {
					if (!StringUtil.isEmptyStr(token) && token.contains("/" + session.getId())) {
						token = token.substring(0, token.indexOf("/" + session.getId()));
					}
				}
			} else {
				if (session.getAttributes() != null && session.getAttributes().containsKey("token")) {
					token = session.getAttributes().get("token").toString();
				}
			}
			if (!StringUtil.isEmptyStr(token)) {
				String userId = AccessTokenUtils.decryptToken(token);
				sessions.remove(userId);
			}
		}
	}
}
