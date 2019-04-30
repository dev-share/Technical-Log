package com.wafersystems.websocket;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.alibaba.fastjson.support.spring.FastjsonSockJsMessageCodec;

/**
 * <pre>
 * 项目:ReserveMeeting
 * 描述:Spring Web Socket适配器
 * 作者:ZhangYi
 * 时间:2016年7月28日 下午4:00:35
 * 版本:wrm_v4.0
 * JDK:1.7.80
 * </pre>
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketAdapter extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
	private static Logger	logger	= Logger.getLogger(WebSocketAdapter.class);

	/**
	 * <pre>
	 * 描述:
	 * 作者:ZhangYi
	 * 时间:2016年7月28日 下午4:02:18
	 * 参数：(参数列表)
	 * @param registry
	 * </pre>
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		WebSocketHandler handler = new WebSocketBaseHandler();
		logger.info("--------------Spring Web Socket注册[开始]--------------");
		// 1.注册WebSocket方式
//		registry.addHandler(handler, "/websocket").setAllowedOrigins("*");
		registry.addHandler(handler, "/websocket").setAllowedOrigins("*").addInterceptors(new WebSocketInterceptor());
		logger.info("--------------注册WebSocket方式[OK]--------------");
		// 2.注册SocketJS方式
//		registry.addHandler(handler, "/websocket").setAllowedOrigins("*").withSockJS().setWebSocketEnabled(true).setDisconnectDelay(10 * 60 * 1000).setHeartbeatTime(10 * 60 * 1000);
		registry.addHandler(handler, "/websocket").setAllowedOrigins("*").addInterceptors(new SocketJSInterceptor()).withSockJS().setMessageCodec(new FastjsonSockJsMessageCodec());
		logger.info("--------------注册SocketJS方式[OK]--------------");
		logger.info("--------------Spring Web Socket注册[完成]--------------");
	}

}
