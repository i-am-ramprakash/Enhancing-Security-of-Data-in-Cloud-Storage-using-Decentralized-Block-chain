package com.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.DBConnection;


/**
 * Servlet implementation class SendKey
 */
@WebServlet("/SendKey")
public class SendKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendKey() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String fid=request.getParameter("fid");
		String to=request.getParameter("to");
		String from=(String)request.getSession().getAttribute("email");
		String key=DBConnection.getkey(fid);
		Connection con=DBConnection.connect();
		try {
			Statement st=con.createStatement();
			int i=st.executeUpdate("insert into ukeys values(0,'"+from+"','"+to+"','"+fid+"','"+key+"')");
			DBConnection.addActivity((String)request.getSession().getAttribute("email"), "Sended key responce to "+to+" successfully", new Date().toLocaleString());
			response.sendRedirect("SendKey.jsp");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
