package com.iara.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SecretDTO {
    private String id;
    private String name;
    private List<SecretVersionDTO> versions;
    private NamespaceDTO namespace;
    private EnvironmentDTO environment;
}
