package com.iara.rest.controller;

import com.iara.core.entity.specification.KvSpecification;
import com.iara.core.service.KeyValueService;
import com.iara.rest.dto.KvDTO;
import com.iara.rest.dto.KvHistoryDTO;
import com.iara.rest.mapper.KeyValueHistoryMapper;
import com.iara.rest.mapper.KeyValueMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/kv")
@RequiredArgsConstructor
public class KeyValueController {

    private final KeyValueMapper mapper;
    private final KeyValueService service;

    private final KeyValueHistoryMapper keyValueHistoryMapper;

    @GetMapping
    public ResponseEntity<Page<KvDTO>> search(@RequestParam(required = false) String id,
                                              @RequestParam(required = false) String key,
                                              @RequestParam(required = false) String environment,
                                              @RequestParam(required = false) String namespace,
                                              Pageable pageable) {
        return ResponseEntity.ok(service.search(KvSpecification
                .builder()
                .id(id)
                .key(key)
                .environment(environment)
                .namespace(namespace)
                .build(), pageable).map(mapper::toDTO));
    }

    @GetMapping("/{namespace}/{environment}/{kv}")
    public ResponseEntity<KvDTO> get(@PathVariable String namespace,
                                     @PathVariable String environment,
                                     @PathVariable String kv) {
        return ResponseEntity.ok(mapper.toDTO(service.get(namespace, environment, kv)));
    }

    @GetMapping("/{kvId}/history")
    public ResponseEntity<List<KvHistoryDTO>> history(@PathVariable String kvId) {
        return ResponseEntity.ok(service.history(kvId).stream().map(keyValueHistoryMapper::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<KvDTO> persist(@RequestBody KvDTO dto) {
        return ResponseEntity.ok(mapper.toDTO(service.persist(mapper.toEntity(dto))));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
