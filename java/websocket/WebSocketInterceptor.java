package com.wafersystems.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;

/**
 * <pre>
 * 项目:ReserveMeeting
 * 描述:Web Socket拦截器
 * 作者:ZhangYi
 * 时间:2016年8月4日 下午3:25:24
 * 版本:wrm_v4.0
 * JDK:1.7.80
 * </pre>
 */
public class WebSocketInterceptor extends OriginHandshakeInterceptor {
	private static Logger	logger	= Logger.getLogger(WebSocketInterceptor.class);

	/**
	 * <pre>
	 * 描述:消息握手前
	 * 作者:ZhangYi
	 * 时间:2016年8月1日 下午1:56:17
	 * 参数：(参数列表)
	 * @param request
	 * @param response
	 * @param wsHandler
	 * @param attributes
	 * @return
	 * @throws Exception
	 * </pre>
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servlet = (ServletServerHttpRequest) request;
			HttpSession session = servlet.getServletRequest().getSession(false);
			if (session != null) {
				// 使用userName区分WebSocketHandler，以便定向发送消息
				String token = (String) session.getAttribute("token");
				logger.info("--SocketJS消息握手前Token:" + token);
			}
		}
		logger.info("--WebSocket消息握手前........");
		return super.beforeHandshake(request, response, handler, attributes);
	}

	/**
	 * <pre>
	 * 描述:消息握手后
	 * 作者:ZhangYi
	 * 时间:2016年8月1日 下午1:56:17
	 * 参数：(参数列表)
	 * @param request
	 * @param response
	 * @param wsHandler
	 * @param exception
	 * </pre>
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception e) {
		logger.info("--WebSocket消息握手后........");
		super.afterHandshake(request, response, handler, e);
	}
}
