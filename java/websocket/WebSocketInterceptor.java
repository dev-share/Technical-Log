package com.dev-share.test.websocket;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;

/**
 * 
     *   项目: zdev-maven-test
     *   描述: Web Socket拦截器
  * @author ZhangYi
  * @date 2019-12-09 20:53:13
     *   版本: v1.0
  * JDK: 1.8
 */
public class WebSocketInterceptor extends OriginHandshakeInterceptor {
	private static Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);
	
	public WebSocketInterceptor(String... allowedOrigins) {
		if (!ObjectUtils.isEmpty(allowedOrigins)) {
			setAllowedOrigins(Arrays.asList(allowedOrigins));
		}
	}
	/**
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
	 * 描述:消息握手后
	 * 作者:ZhangYi
	 * 时间:2016年8月1日 下午1:56:17
	 * 参数：(参数列表)
	 * @param request
	 * @param response
	 * @param wsHandler
	 * @param exception
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception e) {
		logger.info("--WebSocket消息握手后........");
		super.afterHandshake(request, response, handler, e);
	}
	
	@Override
	public Collection<String> getAllowedOrigins() {
		return super.getAllowedOrigins();
	}
}
