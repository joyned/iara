package com.iara.rest.mapper;

import com.iara.core.model.Authentication;
import com.iara.rest.dto.AuthenticationDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AuthenticationMapper extends BaseMapper<Authentication, AuthenticationDTO> {
}
