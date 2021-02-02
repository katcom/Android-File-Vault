package com.katcom.androidFileVault.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PasswordHelper {
    private static final String SHA_256_TAG = "SHA-256";
    private static final String TAG = "PasswordHelper";

    public static String getSHA256MessageDigest(String str) {
        String digestText = "";
        try{

            MessageDigest md  = MessageDigest.getInstance(SHA_256_TAG);
            md.update(str.getBytes());
            byte[] tempDigestArray = md.digest();
            digestText = Arrays.toString(tempDigestArray);

        }catch (NoSuchAlgorithmException e){
            Log.e(TAG,"No algorithm found with tag : "+ SHA_256_TAG,e);
        }
        return digestText;
    }
    public static String getPasswordHash(String password){
        return getSHA256MessageDigest(password);
    }
    public static boolean hasPassword(){
        return false;
    }

}
