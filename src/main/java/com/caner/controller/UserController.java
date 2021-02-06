package com.caner.controller;

import com.caner.bean.CreateUserRequestModel;
import com.caner.bean.Result;
import com.caner.bean.UpdateUserRequestModel;
import com.caner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;

    private Environment environment;

    @Autowired
    public UserController(UserService userService, Environment environment) {
        this.userService = userService;
        this.environment = environment;
    }

    @GetMapping("/status/check")
    public String status() {
        return "Working on port: " + environment.getProperty("local.server.port");
    }

    @PostMapping("/create")
    public Result createUser(@Valid @RequestBody CreateUserRequestModel createUserRequestModel) {
        return userService.createUser(createUserRequestModel);
    }

    @PostMapping("/update")
    public Result updateUser(@Valid @RequestBody UpdateUserRequestModel updateUserRequestModel) {
        return userService.updateUser(updateUserRequestModel);
    }
}
