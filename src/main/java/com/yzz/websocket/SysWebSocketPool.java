package com.yzz.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

public class SysWebSocketPool {

	// 用来记录当前在线连接数，应该把它设计成线程安全的
	private static int onlineCount = 0;

	// 用来存放每个客户端对应的SysWebSocket对象
	private static Map<String, SysWebSocket> sysWebSocketMap = new HashMap<String, SysWebSocket>();

	/**
	 * 单发
	 * 
	 * @param toUserId
	 * @param message
	 */
	public static void sendMessageToUser(String toUserId, String message) {
		try {
			SysWebSocket sysWebSocket = sysWebSocketMap.get(toUserId);

			if (sysWebSocket != null) {
				Session session = sysWebSocket.getSession();
				if (session.isOpen()) {
					session.getBasicRemote().sendText("发给客户端" + sysWebSocket.getCurrentUserId() + "的消息[sessionId:"
							+ sysWebSocket.getSession().getId() + "]:" + message);// 发给对方
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 广播
	 * 
	 * @param message
	 */
	public static void broadcast(String message) {
		for (Map.Entry<String, SysWebSocket> entry : sysWebSocketMap.entrySet()) {
			SysWebSocket sysWebSocket = entry.getValue();

			if (sysWebSocket != null) {
				Session session = sysWebSocket.getSession();
				try {
					if (session.isOpen()) {
						session.getBasicRemote().sendText("发给客户端" + sysWebSocket.getCurrentUserId() + "的消息[sessionId:"
								+ sysWebSocket.getSession().getId() + "]:" + message);
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}

		}
	}

	public static synchronized int getOnlineCount() {
		return SysWebSocketPool.onlineCount;
	}

	public static synchronized void addOnlineCount() {
		SysWebSocketPool.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		SysWebSocketPool.onlineCount--;
	}

	public static synchronized Map<String, SysWebSocket> getSysWebSocketMap() {
		return SysWebSocketPool.sysWebSocketMap;
	}

	public static synchronized void addSysWebSocket(String userId, SysWebSocket sysWebSocket) {
		SysWebSocketPool.sysWebSocketMap.put(userId, sysWebSocket);
	}

	public static synchronized void removeSysWebSocket(String userId) {
		SysWebSocketPool.sysWebSocketMap.remove(userId);
	}

}
