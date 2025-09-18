package com.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.dao.DBConnection;
import com.dao.ImageEncrypt;
import com.dao.NoobChain;
import com.dao.RandomeString;



/**
 * Servlet implementation class FileUpload
 */
@MultipartConfig(maxFileSize = 16177215)
@WebServlet("/FileUpload")
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUpload() {
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
		Connection conn = null; 
        InputStream inputStream = null;
        String filename;
        PrintWriter out=response.getWriter();
        File file = null;
        String id=RandomeString.getID();
        Part filePart = request.getPart("file");
        String ctype=filePart.getContentType();
        String email=(String)request.getSession().getAttribute("email");
       		 
        filename=request.getParameter("filename");
       String content=request.getParameter("content");
       
        if (filePart != null) 
        {
       	     System.out.println(filePart.getName());
       	     System.out.println(filePart.getSize());
       	     System.out.println(filePart.getContentType());
       	 
       	     try {
       	     System.out.println(email);
       	     
       	     inputStream = filePart.getInputStream();
       	     ByteArrayOutputStream bs = new ByteArrayOutputStream();

       	      int bytesRead = -1;
       	     byte[] buffer = new byte[BUFFER_SIZE];
       	     while ((bytesRead = inputStream.read(buffer)) != -1) 
       	     {
       	    	 	bs.write(buffer, 0, bytesRead);
       	     }

       	     KeyGenerator keyGenerator;
				
						keyGenerator = KeyGenerator.getInstance("AES");
					
				
       	        keyGenerator.init(128);
       	        Key key = keyGenerator.generateKey();
       	        System.out.println(key);
       	        byte[] keybit=key.getEncoded();
       	        
       	        

       	        byte[] encrypted = ImageEncrypt.encryptPdfFile(key, bs.toByteArray() );
       	        System.out.println(encrypted);
       	        
       	        String k=key.getEncoded().toString();
       	
       	     
       	    	 String hash=NoobChain.getBlock(content);
       	    	 	conn=DBConnection.connect();
       	    	 	String sql="insert into upload values(?,?,?,?,?,?,?,?)";
	            
       	    	 	PreparedStatement statement = conn.prepareStatement(sql);
       	    	    statement.setString(1, id);
       	    	 	statement.setString(2, hash);
       	    	 	statement.setString(3, email);
       	    	 	statement.setString(4, filename);
       	    	 	statement.setString(5, ctype);
       	    	 	statement.setBytes(6, encrypted);
       	    	 	statement.setString(7, k);
       	    	 	statement.setBytes(8,keybit);
       	    	 	int row = statement.executeUpdate();
       	    	 	
       	    	 	if (row > 0)
       	    	 	{
       	    	 		DBConnection.addActivity((String)request.getSession().getAttribute("email"), "Uploaded file "+filename+" successfully", new Date().toLocaleString());
       	    	 	//DBConnection.addActivity(cspid, email);
       	    	 		out.println("<script type=\"text/javascript\">");
       					out.println("alert('Uploaded Successfully');");
       					out.println("window.location='UploadFile.jsp'</script>");
       	    	 	}else{
       	    	 		out.println("<script type=\"text/javascript\">");
       					out.println("alert('File Uploading Failed');");
       					out.println("window.location='UploadFile.jsp'</script>");
       	    	 	}
       	     	} catch (SQLException ex)
       	     	{
       	     		ex.printStackTrace();
       	     	} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
       	     		if (conn != null) {
       	     			try {
       	     				conn.close();
       	     				} catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            }
       }    
   }
	}
}
