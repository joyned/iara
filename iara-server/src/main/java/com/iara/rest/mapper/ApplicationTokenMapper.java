package com.iara.rest.mapper;

import com.iara.core.entity.ApplicationToken;
import com.iara.rest.dto.ApplicationTokenDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ApplicationTokenMapper extends BaseMapper<ApplicationToken, ApplicationTokenDTO> {
}
