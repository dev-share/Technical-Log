package com.hollysys.smartfactory.test.websocket;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.dev.share.util.DateUtils;

/**
 * <pre>
 * 项目:ReserveMeeting
 * 描述:Spring Web Socket消息处理
 * 作者:ZhangYi
 * 时间:2016年7月28日 下午4:13:37
 * 版本:wrm_v4.0
 * JDK:1.7.80
 * </pre>
 */
public class WebSocketBaseHandler extends AbstractWebSocketHandler {
	private static Logger logger = LoggerFactory.getLogger(WebSocketBaseHandler.class);

	/**
	 * <pre>
	 * 描述:连接建立后
	 * 作者:ZhangYi
	 * 时间:2016年7月28日 下午4:13:59
	 * 参数：(参数列表)
	 * @param session
	 * @throws Exception
	 * </pre>
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String token = WebSocketHolder.handleSession(session);
		InetSocketAddress address = session.getRemoteAddress();
		logger.info("--客户端[" + (address != null ? address.getHostString() : "") + "]端口[" + (address != null ? address.getPort() : "") + "]{sessionId:" + session.getId() + "}建立连接!-------" + token);
		logger.info("--客户端[" + token + "]参数{sessionId:" + session.getId() + ",Protocol:" + session.getAcceptedProtocol() + ",Protocol:" + session.getPrincipal() + ",Headers:" + JSON.toJSONString(session.getHandshakeHeaders()) + ",Attributes:" + JSON.toJSONString(session.getAttributes()) + ",Extensions:" + JSON.toJSONString(session.getExtensions()) + "}");
		session.sendMessage(new TextMessage("[" + DateUtils.formatDateTime(new Date()) + "]连接成功!"));
	}

	/**
	 * <pre>
	 * 描述:消息处理
	 * 作者:ZhangYi
	 * 时间:2016年7月28日 下午4:13:59
	 * 参数：(参数列表)
	 * @param session
	 * @param message
	 * @throws Exception
	 * </pre>
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		InetSocketAddress address = session.getRemoteAddress();
		String result = "";
		if (message != null) {
			result = message instanceof TextMessage && message.getPayload() != null ? (String) message.getPayload() : message.toString();
		}
		WebSocketHolder.test(session);
		logger.info("--客户端[" + (address != null ? address.getHostString() : "") + "]端口[" + (address != null ? address.getPort() : "") + "]接收消息:" + result);
	}

	/**
	 * <pre>
	 * 描述:异常处理
	 * 作者:ZhangYi
	 * 时间:2016年7月28日 下午4:13:59
	 * 参数：(参数列表)
	 * @param session
	 * @param exception
	 * @throws Exception
	 * </pre>
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
		InetSocketAddress address = session.getRemoteAddress();
		if (session.isOpen()) {
			session.close();
		}
		logger.error("--客户端[" + (address != null ? address.getHostString() : "") + "]端口[" + (address != null ? address.getPort() : "") + "]异常!", e);
	}

	/**
	 * <pre>
	 * 描述:连接关闭后执行
	 * 作者:ZhangYi
	 * 时间:2016年7月28日 下午4:13:59
	 * 参数：(参数列表)
	 * @param session
	 * @param closeStatus
	 * @throws Exception
	 * </pre>
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		InetSocketAddress address = session.getRemoteAddress();
		logger.info("--客户端[" + (address != null ? address.getHostString() : "") + "]端口[" + (address != null ? address.getPort() : "") + "]连接关闭!" + status);
		WebSocketHolder.removeSession(session);
	}

	/**
	 * <pre>
	 * 描述:数据是否分包
	 * 作者:ZhangYi
	 * 时间:2016年7月28日 下午4:13:59
	 * 参数：(参数列表)
	 * @return
	 * </pre>
	 */
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
