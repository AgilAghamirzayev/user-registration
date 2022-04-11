package com.vabiss.userregistration.security;

import com.vabiss.userregistration.exception.TokenNotValidException;
import com.vabiss.userregistration.service.email.ConfirmationTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

@Component
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final Environment environment;
    private final ConfirmationTokenService tokenService;

    public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment, ConfirmationTokenService tokenService) {
        super(authenticationManager);
        this.environment = environment;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token = request.getHeader("Authorization");
        if (token != null) {
            tokenService.getToken(token).orElseThrow(() -> { throw new TokenNotValidException(token); });

            SecurityContextHolder.getContext().setAuthentication(authenticate(token));
        }
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken authenticate(String token) {

        Key key = Keys.hmacShaKeyFor(environment.getProperty("jwt.secret").getBytes());

        String user = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token.substring(7))
                .getBody().getSubject();

        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
}
