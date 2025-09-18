package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.DBConnection;

/**
 * Servlet implementation class Approve
 */
@WebServlet("/Approve")
public class Approve extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Approve() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter pw=response.getWriter();
		String fid=request.getParameter("fid");
		String rid=request.getParameter("rid");
		String sql="update request set requests='Approved' where fid='"+fid+"' and rid='"+rid+"' ";
        int i=DBConnection.update(sql)	;
        if( i>0)
        {
        	pw.println("<script type=\"text/javascript\">");
        	pw.println("alert('Request Approved Successfully...');");
        	pw.println("window.location='Request.jsp';</script>");
        }
        else{
        	pw.println("<script type=\"text/javascript\">");
        	pw.println("alert('Request Not Approved...');");
        	pw.println("window.location='Request.jsp';</script>");
        }
}
				

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
