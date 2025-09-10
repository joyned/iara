package com.iara.rest.controller;

import com.iara.core.entity.specification.SecretSpecification;
import com.iara.core.service.SecretService;
import com.iara.rest.dto.SecretDTO;
import com.iara.rest.dto.SecretVersionDTO;
import com.iara.rest.mapper.SecretMapper;
import com.iara.rest.mapper.SecretVersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/secret")
@RequiredArgsConstructor
public class SecretController {

    private final SecretService service;
    private final SecretMapper mapper;
    private final SecretVersionMapper secretVersionMapper;

    @GetMapping
    public ResponseEntity<Page<SecretDTO>> search(@RequestParam(required = false) String id,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String namespace,
                                                  @RequestParam(required = false) String environment,
                                                  Pageable pageable) {
        return ResponseEntity.ok(service.search(SecretSpecification
                .builder()
                .id(id)
                .name(name)
                .environment(environment)
                .namespace(namespace)
                .build(), pageable).map(mapper::toDTO));
    }

    @GetMapping("/{secretId}/{secretVersionId}")
    public ResponseEntity<String> getVersionValue(@PathVariable String secretId, @PathVariable String secretVersionId) throws IllegalAccessException {
        return ResponseEntity.ok(service.getSecretVersionValue(secretId, secretVersionId));
    }

    @PostMapping
    public ResponseEntity<SecretDTO> persist(@RequestBody SecretDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }

    @PutMapping("/version/{secretId}")
    public ResponseEntity<SecretVersionDTO> addSecret(@PathVariable String secretId,
                                                      @RequestBody SecretVersionDTO dto,
                                                      @RequestHeader(name = "Iara-Disable-Past-Version") Boolean disablePastVersion) {
        return ResponseEntity.ok(secretVersionMapper.toDTO(service.addVersion(secretId, secretVersionMapper.toEntity(dto), disablePastVersion)));
    }

    @PostMapping("/{secretId}/disable/{secretVersion}")
    public ResponseEntity<Void> disableSecretVersion(@PathVariable String secretId, @PathVariable Integer secretVersion) {
        service.disableSecretVersion(secretId, secretVersion);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{secretId}/destroy/{secretVersion}")
    public ResponseEntity<Void> destroySecretVersion(@PathVariable String secretId, @PathVariable Integer secretVersion) {
        service.destroySecretVersion(secretId, secretVersion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{secretId}")
    public ResponseEntity<Void> delete(@PathVariable String secretId) {
        service.delete(secretId);
        return ResponseEntity.noContent().build();
    }

}
