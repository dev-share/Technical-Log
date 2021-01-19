package com.dev-share.test.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * <pre>
 * 描述:SocketJS拦截器
 * 作者:ZhangYi
 * 时间:2016年8月4日 下午3:24:50
 * 版本:wrm_v4.0
 * JDK:1.7.80
 * </pre>
 */
public class SocketJSInterceptor extends HttpSessionHandshakeInterceptor {
	private static Logger logger = LoggerFactory.getLogger(SocketJSInterceptor.class);

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
			HttpServletRequest req = servlet.getServletRequest();
			HttpSession session = servlet.getServletRequest().getSession();
			if (session != null) {
				// 使用userName区分WebSocketHandler，以便定向发送消息
				String date = (String) session.getAttribute("date");
//				attributes.put("token", token);
				logger.info("--SocketJS消息握手前date:" + date);
			}
		}
		logger.info("--SocketJS消息握手前........");
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
		logger.info("--SocketJS消息握手后........");
		super.afterHandshake(request, response, handler, e);
	}
}
