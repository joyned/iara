package com.iara.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HMACUtil {
    public static byte[] hmacSha1(byte[] key, byte[] data) {
        try {
            byte[] paddedKey = new byte[64];
            if (key.length > 64) {
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                byte[] hash = sha1.digest(key);
                System.arraycopy(hash, 0, paddedKey, 0, hash.length);
            } else {
                System.arraycopy(key, 0, paddedKey, 0, key.length);
            }

            byte[] ipad = new byte[64];
            byte[] opad = new byte[64];
            for (int i = 0; i < 64; i++) {
                ipad[i] = (byte) (paddedKey[i] ^ 0x36);
                opad[i] = (byte) (paddedKey[i] ^ 0x5C);
            }

            MessageDigest innerDigest = MessageDigest.getInstance("SHA-1");
            innerDigest.update(ipad);
            innerDigest.update(data);
            byte[] innerHash = innerDigest.digest();

            MessageDigest outerDigest = MessageDigest.getInstance("SHA-1");
            outerDigest.update(opad);
            outerDigest.update(innerHash);

            return outerDigest.digest();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not available", e);
        }
    }

}
