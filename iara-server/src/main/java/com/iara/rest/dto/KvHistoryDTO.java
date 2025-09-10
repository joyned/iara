package com.iara.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class KvHistoryDTO {
    private String id;
    private KvDTO keyValue;
    private String value;
    private Date updatedAt;
    private String user;
}
