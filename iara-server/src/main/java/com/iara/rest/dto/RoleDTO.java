package com.iara.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleDTO {
    private String id;
    private String name;
    private String description;
    private List<PolicyDTO> policies;
}
