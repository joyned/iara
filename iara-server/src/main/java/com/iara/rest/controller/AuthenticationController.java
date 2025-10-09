package com.iara.rest.controller;

import com.iara.core.model.OTPConfig;
import com.iara.core.service.AuthenticationService;
import com.iara.rest.dto.AuthenticationDTO;
import com.iara.rest.dto.LoginDTO;
import com.iara.rest.dto.OTPDTO;
import com.iara.rest.mapper.AuthenticationMapper;
import com.iara.utils.CookieUtils;
import com.iara.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final AuthenticationMapper mapper;
    private final CookieUtils cookieUtils;

    @GetMapping("/google-sso")
    public ResponseEntity<String> isGoogleSSOEnabled() {
        return ResponseEntity.ok(service.isGoogleSSOEnabled());
    }

    @PostMapping
    public ResponseEntity<OTPConfig> login(@RequestBody LoginDTO dto, HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtils.getIp(request);
        return ResponseEntity.ok(service.doLogin(dto.getEmail(), dto.getPassword(), ip));
    }

    @PostMapping("/otp-verify")
    public ResponseEntity<Void> otpVerify(@RequestBody OTPDTO otpdto, HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtils.getIp(request);
        AuthenticationDTO authenticationDTO = mapper.toDTO(service.otpVerify(otpdto.getCode(), otpdto.getSession(), ip));
        cookieUtils.createIaraTokenCookies(request, response, authenticationDTO.getAccessToken(), String.valueOf(authenticationDTO.getExpiresIn()));
        return ResponseEntity.noContent().build();
        // TODO: create DTO
    }

    @PostMapping("/google-sso")
    public ResponseEntity<Void> loginGoogleSSO(@RequestParam("code") String code, @RequestParam("redirect_uri") String redirectUri,
                                               HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtils.getIp(request);
        AuthenticationDTO authenticationDTO = mapper.toDTO(service.doLoginGoogleSSO(code, redirectUri, ip));
        cookieUtils.createIaraTokenCookies(request, response, authenticationDTO.getAccessToken(), String.valueOf(authenticationDTO.getExpiresIn()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = cookieUtils.getTokenFromCookies(request);
        service.doLogout(token);
        cookieUtils.invalidateCookies(request, response);
        return ResponseEntity.noContent().build();
    }
}
