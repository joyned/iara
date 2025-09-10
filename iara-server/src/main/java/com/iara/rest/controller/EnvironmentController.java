package com.iara.rest.controller;

import com.iara.core.entity.specification.EnvironmentSpecification;
import com.iara.core.service.EnvironmentService;
import com.iara.rest.dto.EnvironmentDTO;
import com.iara.rest.mapper.EnvironmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/environment")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService service;
    private final EnvironmentMapper mapper;

    @GetMapping
    public ResponseEntity<Page<EnvironmentDTO>> search(@RequestParam(required = false) String id,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String namespace,
                                                       Pageable pageable) {
        return ResponseEntity.ok(service.search(EnvironmentSpecification
                .builder()
                .id(id)
                .name(name)
                .namespace(namespace)
                .build(), pageable).map(mapper::toDTO));
    }
}
