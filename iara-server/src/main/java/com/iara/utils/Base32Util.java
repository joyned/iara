package com.iara.utils;

import java.util.Arrays;

public class Base32Util {

    public static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    public static byte[] decode(String encoded) {
        encoded = encoded.replaceAll("[\\s-]+", "").toUpperCase();

        if (encoded.isEmpty()) {
            return new byte[0];
        }

        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        byte[] result = new byte[encoded.length() * 5 / 8];

        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            int value = BASE32_CHARS.indexOf(c);
            if (value < 0) {
                throw new IllegalArgumentException("Invalid Base32 character: " + c);
            }

            buffer <<= 5;
            buffer |= value;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[count++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }

        return Arrays.copyOf(result, count);
    }
}