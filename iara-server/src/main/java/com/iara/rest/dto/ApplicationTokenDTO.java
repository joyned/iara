package com.iara.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ApplicationTokenDTO {

    private String id;
    private String name;
    private String token;
    private Date createdAt;
    private Date expiresAt;
    private String createdBy;
    private PolicyDTO policy;
}
