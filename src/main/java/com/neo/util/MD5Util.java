package com.neo.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static void main(String[] args){
        String sha1 = DigestUtils.sha1Hex("123456789");
        System.out.print(sha1);
    }
}
