package com.vabiss.userregistration.controller;

import com.vabiss.userregistration.dto.UserDTO;
import com.vabiss.userregistration.mapper.UserMapper;
import com.vabiss.userregistration.model.CreateUserRequestModel;
import com.vabiss.userregistration.model.CreateUserResponseModel;
import com.vabiss.userregistration.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/account")
public class UserController {
    private static final UserMapper MAPPER = UserMapper.INSTANCE;

    private final UserService userService;

    @PostMapping("/signup")
    public void signUp(@RequestBody @Valid CreateUserRequestModel userRequestModel) {
        UserDTO userDTO = MAPPER.createUserRequestModelToUserDTO(userRequestModel);
        userService.registration(userDTO);

        log.info("IN signUp - user:  successfully signup");
    }

    @GetMapping("/me")
    public ResponseEntity<CreateUserResponseModel> me() {
        UserDTO user = userService.login();
        CreateUserResponseModel returnedValue = MAPPER.userDtoToCreateUserResponseModel(user);
        return ResponseEntity.ok(returnedValue);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.confirmToken(token));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> confirm() {
        userService.delete();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
