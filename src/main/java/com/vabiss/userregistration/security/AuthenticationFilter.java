package com.vabiss.userregistration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vabiss.userregistration.dto.UserDTO;
import com.vabiss.userregistration.model.AuthenticationRequestModel;
import com.vabiss.userregistration.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Environment environment;

    public AuthenticationFilter(UserService userService, AuthenticationManager authenticationManager, Environment environment) {
        this.userService = userService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthenticationRequestModel authenticationRequestModel =
                new ObjectMapper().readValue(request.getInputStream(), AuthenticationRequestModel.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequestModel.getEmail(),
                authenticationRequestModel.getPassword(), new ArrayList<>());

        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDTO userDetails = userService.getUserDetailsByEmail(username);

        Key key = Keys.hmacShaKeyFor(environment.getProperty("jwt.secret").getBytes());

        String token = Jwts.builder()
                .setSubject(userDetails.getId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("jwt.expired"))))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        response.addHeader("Authorization", token);
    }
}
