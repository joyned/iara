package com.iara.rest.mapper;

import com.iara.core.entity.ApplicationParams;
import com.iara.rest.dto.ApplicationParamsDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ApplicationParamsMapper extends BaseMapper<ApplicationParams, ApplicationParamsDTO> {
}
