package com.iara.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGIT_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL_CHARS = LOWERCASE_CHARS + UPPERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;
    private static final String ALL_CHARS_WITHOUT_SPECIALS = LOWERCASE_CHARS + UPPERCASE_CHARS + DIGIT_CHARS;

    public static String generateSecurePassword(int length) {
        return generateSecurePassword(length, ALL_CHARS);
    }

    public static String generateSecurePasswordWithOutSpecials(int length) {
        return generateSecurePassword(length, ALL_CHARS_WITHOUT_SPECIALS);
    }

    public static String generateSecurePassword(int length, String chars) {
        if (length < 1) {
            throw new IllegalArgumentException("Password length must be at least 1.");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            passwordBuilder.append(chars.charAt(randomIndex));
        }

        List<Character> passwordChars = new ArrayList<>();
        for (char c : passwordBuilder.toString().toCharArray()) {
            passwordChars.add(c);
        }

        Collections.shuffle(passwordChars, random);

        StringBuilder shuffledPassword = new StringBuilder(length);
        for (char c : passwordChars) {
            shuffledPassword.append(c);
        }

        return shuffledPassword.toString();
    }

}
