package com.iara.rest.controller;

import com.iara.core.entity.specification.PolicySpecification;
import com.iara.core.service.PolicyService;
import com.iara.rest.dto.PolicyDTO;
import com.iara.rest.mapper.PolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/policy")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService service;
    private final PolicyMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#*:READ') or hasAuthority('#POLICIES:WRITE') or hasAuthority('#POLICIES:READ')")
    public ResponseEntity<Page<PolicyDTO>> search(@RequestParam(required = false) String id,
                                                  @RequestParam(required = false) String name,
                                                  Pageable pageable) {
        return ResponseEntity.ok(service.search(PolicySpecification
                .builder()
                .id(id)
                .name(name)
                .build(), pageable).map(mapper::toDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#POLICIES:WRITE')")
    public ResponseEntity<PolicyDTO> persist(@RequestBody PolicyDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#POLICIES:WRITE')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
