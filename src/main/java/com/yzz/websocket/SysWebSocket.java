package com.yzz.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

//该注解用来指定一个URI，客户端可以通过这个URI来连接到WebSocket。类似Servlet的注解mapping。无需在web.xml中配置。
@ServerEndpoint("/websocket/{userId}")
public class SysWebSocket {
	// 当前用户ID
	private String currentUserId;

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(@PathParam("userId") String userId, Session session) {
		this.session = session;
		this.currentUserId = userId;
		SysWebSocketPool.addOnlineCount(); // 在线数加1
		SysWebSocketPool.addSysWebSocket(this.currentUserId, this);
		System.out.println("有新连接加入！当前在线人数为" + SysWebSocketPool.getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		SysWebSocketPool.subOnlineCount(); // 在线数减1
		SysWebSocketPool.removeSysWebSocket(this.currentUserId);
		System.out.println("有一连接关闭！当前在线人数为" + SysWebSocketPool.getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 * @param session
	 *            可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		String[] arr = message.split("@###toUserId###@");
		if (arr.length > 1) {
			String toUserId = arr[0];
			message = arr[1];
			// 单发
			SysWebSocketPool.sendMessageToUser(toUserId, message);
		} else {
			// 群发消息
			SysWebSocketPool.broadcast(message);
		}

	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
