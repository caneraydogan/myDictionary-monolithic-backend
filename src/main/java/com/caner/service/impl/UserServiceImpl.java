package com.caner.service.impl;

import com.caner.bean.*;
import com.caner.dao.InvitationRepo;
import com.caner.dao.UserRepo;
import com.caner.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    private UserRepo userRepo;
    private InvitationRepo invitationRepo;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, InvitationRepo invitationRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.invitationRepo = invitationRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Result createUser(CreateUserRequestModel createUserRequestModel) {
        Result result = isInvitationCodeValid(createUserRequestModel.getInvitationCode());

        if (result.isOk()) {

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserDto userDto = modelMapper.map(createUserRequestModel, UserDto.class);

            userDto.setUserUUId(UUID.randomUUID().toString());

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            User user = modelMapper.map(userDto, User.class);
            user.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userRepo.save(user);

            Invitation invitation = invitationRepo.findByCode(createUserRequestModel.getInvitationCode());
            invitation.setUsedBy(user);
            invitationRepo.save(invitation);
        }

        return result;
    }

    private Result isInvitationCodeValid(String invitationCode) {
        Result result = new Result().setStatus(ResultStatus.FAIL);

        Invitation invitation = invitationRepo.findByCode(invitationCode);

        if (invitation == null) {
            result.setErrorCode("INVALID_INVITATION_CODE");
        } else if (invitation.getUsedBy() != null) {
            result.setErrorCode("INVITATION_CODE_ALREADY_USED");
        }

        return result.getErrorCode() == null ? result.setStatus(ResultStatus.OK) : result;
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
