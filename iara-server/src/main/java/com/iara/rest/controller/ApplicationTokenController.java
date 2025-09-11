package com.iara.rest.controller;

import com.iara.core.service.ApplicationTokenService;
import com.iara.rest.dto.ApplicationTokenDTO;
import com.iara.rest.mapper.ApplicationTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/application-token")
@RequiredArgsConstructor
public class ApplicationTokenController {

    private final ApplicationTokenService service;
    private final ApplicationTokenMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#*:READ') or hasAuthority('#TOKENS:WRITE') or hasAuthority('#TOKEN:READ')")
    public ResponseEntity<Page<ApplicationTokenDTO>> search(@RequestParam(required = false) String id,
                                                            Pageable pageable) {
        return ResponseEntity.ok(service.search(null, pageable).map(mapper::toDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#TOKEN:WRITE')")
    public ResponseEntity<ApplicationTokenDTO> create(@RequestBody ApplicationTokenDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#TOKEN:WRITE')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-tokens")
    public ResponseEntity<Page<ApplicationTokenDTO>> userTokens(Pageable pageable) {
        return ResponseEntity.ok(service.userTokens(pageable).map(mapper::toDTO));
    }

    @PostMapping("/user-tokens")
    public ResponseEntity<ApplicationTokenDTO> userTokens(@RequestBody ApplicationTokenDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persistUserToken(mapper.toEntity(dto))));
    }

    @DeleteMapping("/user-tokens/{id}")
    public ResponseEntity<Void> deleteUserToken(@PathVariable String id) {
        service.deleteUserToken(id);
        return ResponseEntity.noContent().build();
    }
}
