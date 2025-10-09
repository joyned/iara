package com.iara.core.model;

import com.iara.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
    private String accessToken;
    private long expiresIn;
    private User user;
}
