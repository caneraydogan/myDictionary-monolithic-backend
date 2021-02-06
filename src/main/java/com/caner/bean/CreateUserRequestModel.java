package com.caner.bean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateUserRequestModel {

    @Size(min = 2, message = "First name must not be less than 2 characters")
    @NotNull(message = "First name is mandatory!")
    private String firstName;

    @Size(min = 2, message = "Last name must not be less than 2 characters")
    @NotNull(message = "Last name is mandatory!")
    private String lastName;

    @Size(min = 8, max = 16, message = "Password must be equal greater than 8 characters and less than 16 characters")
    @NotNull(message = "Password is mandatory!")
    private String password;

    @Email(message = "Email is not valid")
    @NotNull(message = "Email is mandatory!")
    private String email;
}
