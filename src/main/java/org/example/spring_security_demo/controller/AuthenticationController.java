package org.example.spring_security_demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring_security_demo.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Validated LoginRequest loginRequest) {
    try {
      // ផ្ទៀងផ្ទាត់ Username និង Password ជាមួយទិន្នន័យក្នុង Memory ស្វ័យប្រវត្ត
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getUsername(), loginRequest.getPassword()));

      // បើត្រឹមត្រូវ៖ ត្រឡប់សារជោគជ័យ (នៅត្រង់នេះអ្នកអាចបង្កើត និងផ្ញើ JWT Token ត្រឡប់ទៅវិញបាន)
      return ResponseEntity.ok(
          Map.of(
              "message", "ការចូលប្រព័ន្ធបានជោគជ័យ!",
              "username", authentication.getName(),
              "roles", authentication.getAuthorities()));

    } catch (AuthenticationException e) {
      // បើខុសពាក្យសម្ងាត់ ឬគ្មានគណនី៖ ផ្ញើកំហុស 401 Unauthorized
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "ឈ្មោះអ្នកប្រើប្រាស់ ឬពាក្យសម្ងាត់មិនត្រឹមត្រូវ!"));
    }
  }
}
