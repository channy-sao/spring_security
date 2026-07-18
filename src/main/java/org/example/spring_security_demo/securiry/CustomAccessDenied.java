package org.example.spring_security_demo.securiry;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAccessDenied implements AccessDeniedHandler {
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(403);

    response.setStatus(httpStatusCode.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    PrintWriter writer = response.getWriter();

    Map<String, Object> objectMap = new HashMap<>();
    objectMap.put("code", httpStatusCode.value());
    objectMap.put("message", accessDeniedException.getMessage());
    writer.print(new ObjectMapper().writeValueAsString(objectMap));
    writer.flush();
    writer.close();
  }
}
