package com.shorif.e_commerce;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MyMethods {
    public static String my_key = "";

    public static String encryptData(String text) throws Exception {
        byte[] plainTextBytes = text.getBytes(StandardCharsets.UTF_8);

        // Ensure AES key length (16, 24, or 32 bytes)
        byte[] passwordBytes = Arrays.copyOf("Juba#12345678901".getBytes(StandardCharsets.UTF_8), 16);

        // Encrypt
        SecretKeySpec secretKey = new SecretKeySpec(passwordBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] securedBytes = cipher.doFinal(plainTextBytes);

        // Encode to Base64
        return Base64.encodeToString(securedBytes, Base64.DEFAULT);
    }

    public static void encryptData() throws Exception {

        // Plain text
        String str = "hello";
        byte[] plainTextBytes = str.getBytes(StandardCharsets.UTF_8);

        // Password (AES key must be 16 bytes for AES-128)
        byte[] passwordBytes = Arrays.copyOf(
                "Shorif123".getBytes(StandardCharsets.UTF_8),
                16
        );

        // Secret key
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(passwordBytes, "AES");

        // Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        // Encrypt data
        byte[] securedBytes = cipher.doFinal(plainTextBytes);

        // Base64 encode
        String finalEncode =
                Base64.encodeToString(securedBytes, Base64.DEFAULT);

        System.out.println("Encrypted Text: " + finalEncode);
    }

    //Q0Jt9m7xXv1F2z0m0nX8aw==

    public static String decryptData(String encryptedText) throws Exception {

        byte[] encryptedByte= Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] passBytes= Arrays.copyOf("".getBytes(StandardCharsets.UTF_8),16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(passBytes,"");
        Cipher cipher=Cipher.getInstance("");
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedByte);

        String str= new String(decryptedBytes,StandardCharsets.UTF_8);

        return str;
    }



}
