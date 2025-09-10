package com.iara.rest.controller;

import com.iara.core.entity.specification.RoleSpecification;
import com.iara.core.service.RoleService;
import com.iara.rest.dto.RoleDTO;
import com.iara.rest.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;
    private final RoleMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#*:READ') or hasAuthority('#ROLE:WRITE') or hasAuthority('#ROLE:READ')")
    public ResponseEntity<Page<RoleDTO>> search(@RequestParam(required = false) String id,
                                                @RequestParam(required = false) String name,
                                                Pageable pageable) {
        return ResponseEntity.ok(service.search(RoleSpecification
                .builder()
                .id(id)
                .name(name)
                .build(), pageable).map(mapper::toDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#ROLE:WRITE')")
    public ResponseEntity<RoleDTO> persist(@RequestBody RoleDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#ROLE:WRITE')")
    public ResponseEntity<Void> delete(@PathVariable String roleId) {
        service.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}
