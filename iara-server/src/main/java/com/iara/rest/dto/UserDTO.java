package com.iara.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String picture;
    private List<RoleDTO> roles;
    private Boolean isSSO = false;
}
