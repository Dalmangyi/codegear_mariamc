package com.codegear.mariamc_rfid.cowchronicle.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Base64Util {
    /**
     * Base64 인코딩
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.NO_WRAP);
        //return Base64.encodeToString(data, Base64.NO_WRAP);
        //Android에서 Base64 인코딩 시에 개행문자를 삽입하지 않는 옵션은 NO_WRAP입니다.
    }

    /**
     * Base64 인코딩
     * @param digest
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(byte[] digest) throws UnsupportedEncodingException {
        return Base64.encodeToString(digest, Base64.NO_WRAP);
    }

    /**
     * Base64 디코딩
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String text) throws UnsupportedEncodingException {
        return new String(Base64.decode(text, Base64.NO_WRAP), "UTF-8");
    }


    public static String getURLEncode(String content){
        try {
            return URLEncoder.encode(content, "utf-8");   // UTF-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getURLDecode(String content){
        try {
            return URLDecoder.decode(content, "utf-8");   // UTF-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}