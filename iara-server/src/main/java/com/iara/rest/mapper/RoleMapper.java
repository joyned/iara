package com.iara.rest.mapper;

import com.iara.core.entity.Role;
import com.iara.rest.dto.RoleDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<Role, RoleDTO> {
}
