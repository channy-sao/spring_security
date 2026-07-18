package org.example.spring_security_demo.securiry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(401);

    response.setStatus(httpStatusCode.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    PrintWriter writer = response.getWriter();

    Map<String, Object> objectMap = new HashMap<>();
    objectMap.put("code", httpStatusCode.value());
    objectMap.put("message", authException.getMessage());

    writer.print(new ObjectMapper().writeValueAsString(objectMap));
    writer.flush();
    writer.close();
  }
}
