package com.iara.rest.mapper;

import com.iara.core.entity.Environment;
import com.iara.rest.dto.EnvironmentDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface EnvironmentMapper extends BaseMapper<Environment, EnvironmentDTO> {
}
