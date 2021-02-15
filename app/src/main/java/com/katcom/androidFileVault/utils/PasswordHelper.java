package com.katcom.androidFileVault.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PasswordHelper {
    private static final String SHA_256_TAG = "SHA-256";
    private static final String TAG = "PasswordHelper";

    /**
     * Given a text, this method generates a SHA-256 message digest of the plain text
     * @param str  text to be hashed
     * @return  SHA-256  digest of the text
     */
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

    /**
     * Given a password in plain text, this method returns the SHA-256 hash of the password
     * @param password
     * @return SHA-256 hash code of the password
     */
    public static String getPasswordHash(String password){
        return getSHA256MessageDigest(password);
    }


}
