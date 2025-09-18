package com.dao;

import java.security.*;

import javax.crypto.spec.SecretKeySpec;


public class SKey {
	

	private static final String ALGO = "AES";
    private static final byte[] keyValue =
        new byte[] { 'T', 'h' ,'a', 'B', 'e', 's', 't','S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };

   
    public static Key generateKey() throws Exception
    {
    	
    	
        Key key = new SecretKeySpec(keyValue, ALGO);
        
        
        return  key;
        
    }

}
