package com.iara.rest.mapper;

import com.iara.core.entity.Namespace;
import com.iara.rest.dto.NamespaceDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface NamespaceMapper extends BaseMapper<Namespace, NamespaceDTO> {
}
