package com.iara.rest.mapper;

import com.iara.core.entity.Secret;
import com.iara.rest.dto.SecretDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface SecretMapper extends BaseMapper<Secret, SecretDTO> {
}
