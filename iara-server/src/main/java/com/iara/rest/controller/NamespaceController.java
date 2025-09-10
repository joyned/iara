package com.iara.rest.controller;

import com.iara.core.entity.specification.NamespaceSpecification;
import com.iara.core.service.NamespaceService;
import com.iara.rest.dto.NamespaceDTO;
import com.iara.rest.mapper.NamespaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/namespace")
@RequiredArgsConstructor
public class NamespaceController {

    private final NamespaceService service;
    private final NamespaceMapper mapper;

    @GetMapping
    public ResponseEntity<Page<NamespaceDTO>> search(@RequestParam(required = false) String id,
                                                     @RequestParam(required = false) String name,
                                                     Pageable pageable) {
        return ResponseEntity.ok(service.search(NamespaceSpecification
                .builder()
                .id(id)
                .name(name)
                .build(), pageable).map(mapper::toDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#NAMESPACE:POLICY')")
    public ResponseEntity<NamespaceDTO> persist(@RequestBody NamespaceDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#NAMESPACE:POLICY')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
