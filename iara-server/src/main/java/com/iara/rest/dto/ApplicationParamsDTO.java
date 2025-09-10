package com.iara.rest.dto;

import lombok.Data;

@Data
public class ApplicationParamsDTO {
    private String id;
    private String key;
    private String value;
    private Boolean secure;
}
