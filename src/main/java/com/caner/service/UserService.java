package com.caner.service;

import com.caner.bean.CreateUserRequestModel;
import com.caner.bean.Result;
import com.caner.bean.UpdateUserRequestModel;
import com.caner.bean.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Result createUser(CreateUserRequestModel createUserRequestModel);

    UserDto getUserDTOByEmail(String email);

    Result updateUser(UpdateUserRequestModel updateUserRequestModel);
}
