package com.example.finly.finance.infraestructure.security;

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

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null && !tokenBlacklistService.isBlacklisted(token)) {

            var decodedJWT = jwtService.validateToken(token);

            if(decodedJWT != null){

                String email = decodedJWT.getSubject();
                var userIdClaim = decodedJWT.getClaim("userId");
                var firstnameClaim = decodedJWT.getClaim("firstname");
                var lastnameClaim = decodedJWT.getClaim("lastname");

                if (!userIdClaim.isMissing() && !userIdClaim.isNull()) {
                    String userId = userIdClaim.asString();
                    String firstname = firstnameClaim.asString();
                    String lastname = lastnameClaim.asString();

                    UserPrincipal principal = new UserPrincipal(
                            UUID.fromString(userId), 
                            email, 
                            firstname != null ? firstname : "", 
                            lastname != null ? lastname : ""
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