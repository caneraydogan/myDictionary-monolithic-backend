package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestModel {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}
