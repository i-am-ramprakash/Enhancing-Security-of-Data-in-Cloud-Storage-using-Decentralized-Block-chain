package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.DBConnection;


/**
 * Servlet implementation class Owner
 */
@WebServlet("/Owner")
public class Owner extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Owner() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter o = response.getWriter();
		String uid = request.getParameter("email");
		String pwd = request.getParameter("password");
		String sql = "select * from owner where email='"+uid+"' and password='"+pwd+"'";
		boolean b = DBConnection.getData(sql);
		HttpSession session = request.getSession();
		if(b == true){
			session.setAttribute("email", uid);
			sql = "select name from owner where email='"+uid+"'";
			String name = DBConnection.getName(sql);
			session.setAttribute("name", name);
			response.sendRedirect("DataOwnerHome.jsp");
		}else{
			o.println("<script type=\"text/javascript\">");
			o.println("alert('Please enter valid Details');");
			o.println("window.location='DataOwnerLogin.jsp';</script>");
		}
	}
	}
