package com.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.DBConnection;
import com.dao.ImageEncrypt;

/**
 * Servlet implementation class ViewData
 */
@WebServlet("/ViewData1")
public class ViewData1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewData1() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String iid=request.getParameter("fid");
		String	file_key=request.getParameter("key");
		byte[] image = null;
		Connection con = null;
		byte[] imgData = null;
		byte[] key=null;
		String ctype="";
		Statement stmt = null;
		String filename="";
		ResultSet rs = null;
		try {
		
			con = DBConnection.connect();
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from upload where fid="+iid+"  and filekey='"+file_key+"'");
			System.out.println("select * from upload where fid="+iid+"  and filekey='"+file_key+"'");
			if (rs.next()) {
				System.out.println("ok");
				image = rs.getBytes("data");
				key=rs.getBytes("en_key");
				filename=rs.getString("filename");
				ctype=rs.getString("contenttype");
				DBConnection.addActivity((String)request.getSession().getAttribute("email"), "Downloaded file "+filename+" successfully", new Date().toLocaleString());
				//imgData = image.getBytes(1, (int) image.length());
			} else {
				
			}
			// display the image
			response.setContentType(ctype);
			//response.setHeader("Content-disposition", "attachment; filename="+ filename);
			SecretKey key2 = new SecretKeySpec(key, 0, key.length, "AES");
			imgData=ImageEncrypt.decryptPdfFile(key2, image);
			//imgData=ImageEncrypt.encryptPdfFile(key2, image);
			System.out.println(key2);
			OutputStream o = response.getOutputStream();
			o.write(imgData);
			o.flush();
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
			/* out.println("Unable To Display image");
			out.println("Image Display Error=" + e.getMessage());
			 */return;
		} 
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				}
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
