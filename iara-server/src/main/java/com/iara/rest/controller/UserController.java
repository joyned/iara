package com.iara.rest.controller;

import com.iara.core.entity.specification.UserSpecification;
import com.iara.core.service.ApplicationTokenService;
import com.iara.core.service.AuthenticationService;
import com.iara.core.service.UserService;
import com.iara.rest.dto.ChangePasswordDTO;
import com.iara.rest.dto.UserDTO;
import com.iara.rest.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationService authenticationService;
    private final ApplicationTokenService applicationTokenService;
    private final UserMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#*:READ') or hasAuthority('#USERS:WRITE') or hasAuthority('#USERS:READ')")
    public ResponseEntity<Page<UserDTO>> search(@RequestParam(required = false) String id,
                                                @RequestParam(required = false) String email,
                                                Pageable pageable) {
        return ResponseEntity.ok(service.search(UserSpecification
                .builder()
                .id(id)
                .email(email)
                .build(), pageable).map(mapper::toDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() {
        UserDTO res = mapper.toDTO(service.me());
        res.setId(null);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#USERS:WRITE')")
    public ResponseEntity<UserDTO> persist(@RequestBody UserDTO dto) {
        UserDTO response = mapper.toDTO(service.persist(mapper.toEntity(dto)));
        applicationTokenService.updateUserTokensPolicies(mapper.toEntity(response));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#USERS:WRITE')")
    public ResponseEntity<Void> resetPassword(@PathVariable String id) {
        service.resetPassword(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-otp")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#USERS:WRITE')")
    public ResponseEntity<Void> resetOtp(@PathVariable String id) {
        service.resetOtp(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        service.changePassword(dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
