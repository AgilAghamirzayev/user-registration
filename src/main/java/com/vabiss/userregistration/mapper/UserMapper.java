package com.vabiss.userregistration.mapper;

import com.vabiss.userregistration.dto.UserDTO;
import com.vabiss.userregistration.entity.UserEntity;
import com.vabiss.userregistration.model.CreateUserRequestModel;
import com.vabiss.userregistration.model.CreateUserResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(UserEntity UserEntity);

    UserEntity userDtoToUser(UserDTO employeeDTO);

    UserDTO createUserRequestModelToUserDTO(CreateUserRequestModel userDetails);

    CreateUserResponseModel userToCreateUserResponseModel(UserDTO userDTO);


    CreateUserResponseModel userToCreateUserResponseModel(UserEntity userEntity);

    CreateUserResponseModel userDtoToCreateUserResponseModel(UserDTO user);

}
