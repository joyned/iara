package com.iara.core.topt;

import com.iara.utils.Base32Util;
import com.iara.utils.HMACUtil;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TOTPGenerator {

    private static final int TIME_STEP = 30;
    private static final int CODE_DIGITS = 6;

    public String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return base32Encode(bytes);
    }

    public boolean verifyCode(String secret, String code) {
        return verifyCode(secret, code, System.currentTimeMillis());
    }

    public boolean verifyCode(String secret, String code, long currentTimeMillis) {
        String expectedCode = generateTOTP(secret, currentTimeMillis);
        return expectedCode.equals(code);
    }

    public String generateTOTP(String secret) {
        return generateTOTP(secret, System.currentTimeMillis());
    }

    private String generateTOTP(String secret, long currentTimeMillis) {
        try {
            byte[] key = Base32Util.decode(secret);
            long timeStep = currentTimeMillis / 1000 / TIME_STEP;

            byte[] timeBytes = longToBytes(timeStep);
            byte[] hash = HMACUtil.hmacSha1(key, timeBytes);

            int offset = hash[hash.length - 1] & 0x0F;

            int binary = ((hash[offset] & 0x7F) << 24) |
                    ((hash[offset + 1] & 0xFF) << 16) |
                    ((hash[offset + 2] & 0xFF) << 8) |
                    (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, CODE_DIGITS);

            return String.format("%0" + CODE_DIGITS + "d", otp);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar TOTP", e);
        }
    }

    private byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    private String base32Encode(byte[] data) {
        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : data) {
            buffer <<= 8;
            buffer |= (b & 0xFF);
            bitsLeft += 8;

            while (bitsLeft >= 5) {
                int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                result.append(Base32Util.BASE32_CHARS.charAt(index));
                bitsLeft -= 5;
            }
        }

        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            result.append(Base32Util.BASE32_CHARS.charAt(index));
        }

        return result.toString();
    }
}
