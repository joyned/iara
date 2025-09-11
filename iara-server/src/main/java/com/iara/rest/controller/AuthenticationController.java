package com.iara.rest.controller;

import com.iara.core.service.AuthenticationService;
import com.iara.rest.dto.AuthenticationDTO;
import com.iara.rest.dto.LoginDTO;
import com.iara.rest.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final AuthenticationMapper mapper;

    @GetMapping("/google-sso")
    public ResponseEntity<Boolean> isGoogleSSOEnabled() {
        return ResponseEntity.ok(service.isGoogleSSOEnabled());
    }

    @PostMapping
    public ResponseEntity<AuthenticationDTO> login(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.doLogin(dto.getEmail(), dto.getPassword())));
    }

    @PostMapping("/google-sso")
    public ResponseEntity<AuthenticationDTO> loginSSO(@RequestParam("code") String code) {
        return ResponseEntity.ok(mapper.toDTO(service.doLoginSSO(code)));
    }
}
