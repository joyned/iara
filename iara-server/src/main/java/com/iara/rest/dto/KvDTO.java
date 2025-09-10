package com.iara.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KvDTO {

    private String id;
    private String key;
    private String value;
    private EnvironmentDTO environment;
    private NamespaceDTO namespace;
}
