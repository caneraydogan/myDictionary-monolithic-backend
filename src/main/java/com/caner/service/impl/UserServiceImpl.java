package com.caner.service.impl;

import com.caner.bean.*;
import com.caner.dao.UserRepo;
import com.caner.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Result createUser(CreateUserRequestModel createUserRequestModel) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(createUserRequestModel, UserDto.class);

        userDto.setUserUUId(UUID.randomUUID().toString());

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        User user = modelMapper.map(userDto, User.class);
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userRepo.save(user);

        return new Result().setStatus(ResultStatus.OK);
    }

    @Override
    public Result updateUser(UpdateUserRequestModel updateUserRequestModel) {
        User user = userRepo.findByUserUUId(updateUserRequestModel.getUserUUId());

        user.setFirstName(updateUserRequestModel.getFirstName());
        user.setLastName(updateUserRequestModel.getLastName());
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(updateUserRequestModel.getPassword()));
        userRepo.save(user);

        return new Result().setStatus(ResultStatus.OK);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);

        if (user == null) throw new UsernameNotFoundException(username);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDTOByEmail(String email) {
        User user = userRepo.findByEmail(email);

        if (user == null) throw new UsernameNotFoundException(email);

        return new ModelMapper().map(user, UserDto.class);
    }
}
