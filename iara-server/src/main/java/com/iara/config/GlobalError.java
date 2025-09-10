package com.iara.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalError {
    private String key;
    private String message;
    private String path;
    private int status;
}
