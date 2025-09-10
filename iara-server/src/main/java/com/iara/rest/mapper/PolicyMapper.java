package com.iara.rest.mapper;

import com.iara.core.entity.Policy;
import com.iara.rest.dto.PolicyDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PolicyMapper extends BaseMapper<Policy, PolicyDTO> {
}
