package com.neo.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    String sha1 = DigestUtils.sha1Hex("12345jkghjkhkjhkjhkjhkjhkjhkjhkjhbk152121564654654654654561321321454654566");

    public static void main(String[] args){
        String sha1 = DigestUtils.sha1Hex("12345jkghjkhkjhkjhkjhkjhkjhkjhkjhbk152121564654654654654561321321454");
        System.out.print(sha1);
    }
}
