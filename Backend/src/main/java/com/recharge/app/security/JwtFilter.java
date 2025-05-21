package com.recharge.app.security;

import com.recharge.app.repository.LogRepository;
import com.recharge.app.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
    private JwtUtil jwtUtil;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private LogRepository logRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String phoneNumber = jwtUtil.extractPhoneNumber(token);
        String role = jwtUtil.extractRole(token);

        if (phoneNumber != null && role != null) {
            if (jwtUtil.isTokenExpired(token)) {
                handleExpiredToken(phoneNumber);
            } else {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(phoneNumber, null,
                                Collections.singletonList(new SimpleGrantedAuthority(role)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private void handleExpiredToken(String phoneNumber) {
        userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
            logRepository.findTopByUserOrderByLogIdDesc(user).ifPresent(log -> {
                if (log.getLogOut() == null) {
                    log.setLogOut(new Timestamp(System.currentTimeMillis())); // Set logout time if not already set
                    logRepository.save(log);
                }
            });
        });
    }
}
