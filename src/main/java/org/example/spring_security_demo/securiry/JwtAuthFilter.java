package org.example.spring_security_demo.securiry;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_security_demo.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    log.info("REQUEST URL:{}", request.getRequestURL());
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    try {
      if (authHeader != null
          && (authHeader.startsWith("Bearer ") || authHeader.startsWith("bearer "))) {
        String token = authHeader.substring(7);
        log.debug("TOKEN: {}", token);
        String userName = jwtService.extractUserName(token);

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
          if (jwtService.isValidToken(token)) {
            Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      }
    } catch (Exception ex) {
      log.error("ERROR: {}", ex.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
