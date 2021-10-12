package com.rbm.web.prepago.dto;


	import javax.crypto.Cipher;
	import javax.crypto.spec.IvParameterSpec;
	import javax.crypto.spec.SecretKeySpec;
	import org.apache.commons.codec.binary.Base64;

	public class nuevo {
	    public static byte[] encrypt(String key, String initVector, String value) {
	        try {
	            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
	            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

	            byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
	         
	            return Base64.encodeBase64(encrypted);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }

	        return null;
	    }

	    public static String decrypt(String key, String initVector, String encrypted) {
	        try {
	            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
	            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

	            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes("UTF8")));
	           

	            return new String(original);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }

	        return null;
	    }

	    public static void main(String[] args) {
	        String key = "Bar12345Bar12345"; // 128 bit key
	        String initVector = "RandomInitVector"; // 16 bytes IV
	        String clave=new String(encrypt(key, initVector, "1234567890123456"));
	    }

}
