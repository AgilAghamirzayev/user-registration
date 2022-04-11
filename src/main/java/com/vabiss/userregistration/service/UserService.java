package com.vabiss.userregistration.service;

import com.vabiss.userregistration.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void registration(UserDTO user);

    String confirmToken(String token);

    UserDTO getUserDetailsByEmail(String username);

    void delete();

    UserDTO login();
}
