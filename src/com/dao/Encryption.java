package com.dao;

import java.security.Key;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encryption {
	
	

	public static String encrypt(String Data) throws Exception
	{
	        Key key = SKey.generateKey();
	        Cipher c = Cipher.getInstance("AES");
	        c.init(Cipher.ENCRYPT_MODE, key);
	        byte[] encVal = c.doFinal(Data.getBytes());
	        String encryptedValue = new BASE64Encoder().encode(encVal);
	        return encryptedValue;
	  }
	
	public static String decrypt(String encryptedData) throws Exception 
	{
        Key key = SKey.generateKey();
        System.out.println("key...!!!!  "+key);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        //System.out.println("key is :::"+Key.generateKey());
        return decryptedValue;
	}
	
}
