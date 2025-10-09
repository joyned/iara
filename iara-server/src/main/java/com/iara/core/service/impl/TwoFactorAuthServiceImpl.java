package com.iara.core.service.impl;

import com.iara.core.entity.User;
import com.iara.core.exception.UserNotFoundException;
import com.iara.core.service.TwoFactorAuthService;
import com.iara.core.service.UserService;
import com.iara.core.topt.TOTPGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

    public static final String format = "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30";

    private final TOTPGenerator totpGenerator;
    private final UserService userService;

    @Override
    public User register(String userEmail) {
        Optional<User> userOptional = userService.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User %s was not found.", userEmail);
        }

        User user = userOptional.get();

        String secret = totpGenerator.generateSecret();
        String url = String.format(format,
                URLEncoder.encode("iara.com", StandardCharsets.UTF_8),
                URLEncoder.encode(userEmail, StandardCharsets.UTF_8),
                secret,
                URLEncoder.encode("iara.com", StandardCharsets.UTF_8)
        );

        user.setOtpAuthUrl(url);
        user.setOtpBase32(secret);

        return userService.persist(user);
    }

    @Override
    public boolean verify(String code, String secret) {
        return totpGenerator.verifyCode(secret, code);
    }
}
