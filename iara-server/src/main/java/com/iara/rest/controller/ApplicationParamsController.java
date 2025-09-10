package com.iara.rest.controller;

import com.iara.core.service.ApplicationParamsService;
import com.iara.rest.dto.ApplicationParamsDTO;
import com.iara.rest.mapper.ApplicationParamsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/params")
@RequiredArgsConstructor
public class ApplicationParamsController {

    private final ApplicationParamsService service;
    private final ApplicationParamsMapper mapper;

    @GetMapping("/{key}")
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#*:READ') or hasAuthority('#GENERAL:WRITE') or hasAuthority('#GENERAL:READ')")
    public ResponseEntity<ApplicationParamsDTO> findByKey(@PathVariable String key) {
        return ResponseEntity.ok(mapper.toDTO(service.findByKey(key)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('#*:WRITE') or hasAuthority('#GENERAL:WRITE')")
    public ResponseEntity<ApplicationParamsDTO> persist(@RequestBody ApplicationParamsDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }
}
