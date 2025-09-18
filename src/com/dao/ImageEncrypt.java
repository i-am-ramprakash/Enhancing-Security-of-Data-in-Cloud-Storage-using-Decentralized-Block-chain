package com.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class ImageEncrypt {

    public static byte[] getFile() {

        File f = new File("D:/LavanN/workspace/vtjcc10_2017/WebContent/img1.jpg");
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        byte[] content = null;
        try {
            content = new byte[is.available()];
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            is.read(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return content;
    }

    public static byte[] encryptPdfFile(Key key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;

    }

    public static byte[] decryptPdfFile(Key key, byte[] textCryp) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(textCryp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decrypted;
    }

    public static Connection connect()
    {
    	Connection con = null;
    	try{  
    		Class.forName("com.mysql.jdbc.Driver");  
    		con=DriverManager.getConnection("jdbc:mysql://localhost:3306/vtjcc10_2017","root","root");
    		return con;
    	}catch(ClassNotFoundException e)
    	{
    		e.printStackTrace();
    		return con;
    	}catch (SQLException e) {
    		// TODO: handle exception
    		e.printStackTrace();
    		return con;
    	}
    	
    	
    }

    public static void saveFile(byte[] bytes) throws IOException {

        FileOutputStream fos = new FileOutputStream("temp.jpg");
        fos.write(bytes);
        fos.close();
        
    }

    public static void main(String args[])
            throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IOException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Key key = keyGenerator.generateKey();
        System.out.println(key);
        byte[] keybit=key.getEncoded();
        byte[] content = getFile();
        System.out.println(content);

        byte[] encrypted = encryptPdfFile(key, content);
        System.out.println(encrypted);
        
        byte[] decrypted = decryptPdfFile(key, encrypted);
        System.out.println(decrypted);
   
        Connection con=connect();
        try {
			PreparedStatement p=con.prepareStatement("insert into storeimg values(0,?,?)");
			p.setBytes(1,keybit);
			p.setBytes(2,encrypted);
			System.out.println(p.executeUpdate());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

      
    }

}