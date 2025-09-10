package com.iara.rest.mapper;

import com.iara.core.entity.Kv;
import com.iara.rest.dto.KvDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface KeyValueMapper extends BaseMapper<Kv, KvDTO> {
}
