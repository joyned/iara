package com.iara.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecretVersionDTO {
    private String id;
    private Integer version;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String value;

    private Boolean disabled;
    private Boolean destroyed;
}
