package com.iara.rest.controller;

import com.iara.core.model.Authentication;
import com.iara.core.service.AuthenticationService;
import com.iara.rest.dto.AuthenticationDTO;
import com.iara.rest.dto.LoginDTO;
import com.iara.rest.mapper.AuthenticationMapper;
import com.iara.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Boolean> isGoogleSSOEnabled() {
        return ResponseEntity.ok(service.isGoogleSSOEnabled());
    }

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody LoginDTO dto, HttpServletRequest request, HttpServletResponse response) {
        AuthenticationDTO authenticationDTO = mapper.toDTO(service.doLogin(dto.getEmail(), dto.getPassword()));
        cookieUtils.createIaraTokenCookies(request, response, authenticationDTO.getAccessToken(), String.valueOf(authenticationDTO.getExpiresIn()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/google-sso")
    public ResponseEntity<Void> loginSSO(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        AuthenticationDTO authenticationDTO = mapper.toDTO(service.doLoginSSO(code));
        cookieUtils.createIaraTokenCookies(request, response, authenticationDTO.getAccessToken(), String.valueOf(authenticationDTO.getExpiresIn()));
        return ResponseEntity.noContent().build();
    }
}
