package com.example.finly.finance.infraestructure.security;

import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null){

            var decodedJWT = tokenService.validateToken(token);

            if(decodedJWT != null){

                String email = decodedJWT.getSubject();
                String userId = decodedJWT.getClaim("userId").asString();
                String firstname = decodedJWT.getClaim("firstname").asString();
                String lastname = decodedJWT.getClaim("lastname").asString();

                UserPrincipal principal = new UserPrincipal(
                        UUID.fromString(userId), 
                        email, 
                        firstname, 
                        lastname
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer "))
            return null;

        return authHeader.substring(7);
    }
}