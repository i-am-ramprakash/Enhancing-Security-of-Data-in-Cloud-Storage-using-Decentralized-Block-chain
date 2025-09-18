package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.bean.UserBean;

public class DBConnection {
	public static Connection connect(){
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vtjns04-2022", "root", "root");
			return con;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return con;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return con;
		}
		
	}

	public static boolean getData(String sql) {
		// TODO Auto-generated method stub
		boolean b=false;
		Connection con=connect();
		try {
			PreparedStatement ps=con.prepareStatement(sql) ;
			ResultSet rs=ps.executeQuery();
			b=rs.next();
			ps.close();
			rs.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	public static String getName(String sql) {
		// TODO Auto-generated method stub
		String name="";
		Connection con=connect();
		try {
			PreparedStatement ps=con.prepareStatement(sql);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				name=rs.getString(1);
			}
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return name;
	}

	public static int setOwner(String sql, UserBean ub) {
		// TODO Auto-generated method stub
		int i=0;
		Connection con=connect();
		try {
			PreparedStatement ps=con.prepareStatement(sql);
			ps.setString(1, ub.getName());
			ps.setString(2, ub.getEmail());
			ps.setString(3, ub.getAge());
			ps.setString(4, ub.getGen());
			ps.setString(5, ub.getPass());
			i=ps.executeUpdate();
			ps.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return i;
	}

	public static int setUser(String sql, UserBean ub) {
		// TODO Auto-generated method stub
		int i=0;
		Connection con=connect();
		try {
			PreparedStatement ps=con.prepareStatement(sql);
			ps.setString(1, ub.getName());
			ps.setString(2, ub.getEmail());
			ps.setString(3, ub.getAge());
			ps.setString(4, ub.getGen());
			ps.setString(5, ub.getPass());
			i=ps.executeUpdate();
			ps.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	 public static int addblock(String pre_hash,String hash,String data) 
		{	Connection con=null;
		int i=0;	
		try{
			 con =DBConnection.connect();
			String sql="insert into blockchain values(?,?,?,?);";
			PreparedStatement p=con.prepareStatement(sql);
			p.setString(1, pre_hash);
			p.setString(2, hash);
			p.setString(3, data);
			Date d = new Date();
		    String da = "" + d;
			p.setString(4, da);
			i=p.executeUpdate();
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return i;
		}
	 public static int addActivity(String email,String activity,String time) throws SQLException
		{	Connection con=null;
		int i=0;	
		try{
			 con =DBConnection.connect();
			String sql="insert into activity values(0,?,?,?);";
			PreparedStatement p=con.prepareStatement(sql);
	
			p.setString(3, time);
			p.setString(1, email);
			p.setString(2, activity);
			
			i=p.executeUpdate();
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return i;
		}
	 public static ResultSet getMyFiles(String email) throws SQLException
		{
			Connection con =DBConnection.connect();
			String sql="select * from upload where email='"+email+"'";
			Statement s=con.createStatement();
			ResultSet r=s.executeQuery(sql);
			return r;
		}
	 public static ResultSet getAllFiles() throws SQLException
		{
			Connection con =DBConnection.connect();
			String sql="select * from upload";
			Statement s=con.createStatement();
			ResultSet r=s.executeQuery(sql);
			return r;
		}
	 public static ResultSet getAllHash(String content,String content1) throws SQLException
		{
			Connection con =DBConnection.connect();
			String sql="select * from blockchain where prehash='"+content+"' and hash='"+content1+"'";
			Statement s=con.createStatement();
			ResultSet r=s.executeQuery(sql);
			return r;
		}
	 public static String getFileContent(String hash) throws SQLException
		{
	    	String str="";
			Connection con =DBConnection.connect();
			String sql1="SELECT DATA FROM blockchain WHERE HASH='"+hash+"'";//(SELECT HASH FROM blockchain WHERE pre_hash=(SELECT HASH FROM blockchain WHERE pre_hash='hash1' ) );";
			Statement s1=con.createStatement();
			ResultSet r1=s1.executeQuery(sql1);
			if(r1.next())
			{
				str=str+r1.getString(1);
			}
			
			String sql2="SELECT DATA FROM blockchain WHERE HASH=(SELECT HASH FROM blockchain WHERE prehash='"+hash+"')";//(SELECT HASH FROM blockchain WHERE pre_hash='hash1' ) );";
			Statement s2=con.createStatement();
			ResultSet r2=s2.executeQuery(sql2);
			if(r2.next())
			{
				str=str+r2.getString(1);
			}
			

			String sql3="SELECT DATA FROM blockchain WHERE HASH=(SELECT HASH FROM blockchain WHERE prehash=(SELECT HASH FROM blockchain WHERE prehash='"+hash+"' ) );";
			Statement s3=con.createStatement();
			ResultSet r3=s3.executeQuery(sql3);
			if(r3.next())
			{
				str=str+r3.getString(1);
			}
			return str;
		}
	 public static ResultSet getAllRequest(String email) throws SQLException
		{
			Connection con =DBConnection.connect();
			String sql="select * from request where to1='"+email+"'";
			Statement s=con.createStatement();
			ResultSet r=s.executeQuery(sql);
			return r;
		}

	 public static int update(String sql) {
			// TODO Auto-generated method stub
			int i = 0;
			Connection con = connect();
			try {
				PreparedStatement ps = con.prepareStatement(sql);
				i = ps.executeUpdate();
				ps.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return i;
		}

	 public static String getkey (String fid)
		{
	    	String key="";
			Connection con =DBConnection.connect();
			String sql="select filekey from upload where fid='"+fid+"'";
			Statement s;
			try {
				s = con.createStatement();
				ResultSet r=s.executeQuery(sql);
				r.next();
				key=r.getString(1);
				r.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return key;
		}
	 public static ResultSet getApprovedRequests(String email) throws SQLException
		{
			Connection con =DBConnection.connect();
			String sql="select * from request where  to1='"+email+"' and requests='Approved' ";
			Statement s=con.createStatement();
			ResultSet r=s.executeQuery(sql);
			return r;
		}
	 public static ResultSet getMyRequestedFiles(String email) throws SQLException
		{
			Connection con =DBConnection.connect();
			String sql="select * from ukeys where to1='"+email+"'";
			Statement s=con.createStatement();
			ResultSet r=s.executeQuery(sql);
			return r;
		}
	 public static ResultSet GetOwnerDetails() throws SQLException
	 {
		 Connection con=connect();
		 String sql="select * from owner";
		 Statement st=con.createStatement();
		 ResultSet rs=st.executeQuery(sql);
		return rs;
	 }
	 public static ResultSet getUserDetails() throws SQLException
	 {
		 Connection con=connect();
		 Statement st=con.createStatement();
		 String sql="select * from user";
		 ResultSet rs=st.executeQuery(sql);
		 return rs;
		 
	 }
	 public static ResultSet getBlocksInfo() throws SQLException
	 {
		 Connection con=connect();
		 Statement st=con.createStatement();
		 String sql="select * from blockchain";
		 ResultSet rs=st.executeQuery(sql);
		 return rs;
	 }
	 public static String getFileContent1(String content,String content1) throws SQLException
		{
	    	String str="";
			Connection con =DBConnection.connect();
			String sql1="SELECT DATA FROM blockchain WHERE HASH='"+content1+"' and prehash='"+content+"'";//(SELECT HASH FROM blockchain WHERE pre_hash=(SELECT HASH FROM blockchain WHERE pre_hash='hash1' ) );";
			Statement s1=con.createStatement();
			ResultSet r1=s1.executeQuery(sql1);
			if(r1.next())
			{
				str=str+r1.getString(1);
			}
			
			String sql2="SELECT DATA FROM blockchain WHERE HASH=(SELECT HASH FROM blockchain WHERE prehash='"+content+"')";//(SELECT HASH FROM blockchain WHERE pre_hash='hash1' ) );";
			Statement s2=con.createStatement();
			ResultSet r2=s2.executeQuery(sql2);
			if(r2.next())
			{
				str=str+r2.getString(1);
			}
			

			String sql3="SELECT DATA FROM blockchain WHERE HASH=(SELECT HASH FROM blockchain WHERE prehash=(SELECT HASH FROM blockchain WHERE prehash='"+content+"' ) );";
			Statement s3=con.createStatement();
			ResultSet r3=s3.executeQuery(sql3);
			if(r3.next())
			{
				str=str+r3.getString(1);
			}
			return str;
		}
}
