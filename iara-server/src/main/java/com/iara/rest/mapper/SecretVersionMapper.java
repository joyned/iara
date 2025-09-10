package com.iara.rest.mapper;

import com.iara.core.entity.SecretVersion;
import com.iara.rest.dto.SecretVersionDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface SecretVersionMapper extends BaseMapper<SecretVersion, SecretVersionDTO> {
}
