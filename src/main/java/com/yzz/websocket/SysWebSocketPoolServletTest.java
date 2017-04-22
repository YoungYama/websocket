package com.yzz.websocket;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/sysWebSocketPoolServlet")
public class SysWebSocketPoolServletTest extends HttpServlet {

	private static final long serialVersionUID = -1571858366142320347L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("start");
		String toUserId = req.getParameter("toUserId");
		String message = req.getParameter("message");
		PrintWriter out = resp.getWriter();
		String result = null;
		if (toUserId != null) {
			result = "one：" + message;
			SysWebSocketPool.sendMessageToUser(toUserId, message);
		} else {
			result = "all：" + message;
			SysWebSocketPool.broadcast(message);
		}

		out.println(result);
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
