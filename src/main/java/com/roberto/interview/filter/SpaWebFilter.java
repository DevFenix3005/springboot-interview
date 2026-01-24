package com.roberto.interview.filter;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SpaWebFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    final HttpServletRequest request,
    final @NonNull HttpServletResponse response,
    final @NonNull FilterChain filterChain)
    throws ServletException, IOException {

    // Request URI includes the contextPath if any, removed it.
    String path = request.getRequestURI().substring(request.getContextPath().length());
    if (
      !path.startsWith("/api") &&
        !path.startsWith("/management") &&
        !path.startsWith("/v3/api-docs") &&
        !path.startsWith("/h2-console") &&
        !path.contains(".") &&
        path.matches("/(.*)")
    ) {
      request.getRequestDispatcher("/index.html").forward(request, response);
      return;
    }

    filterChain.doFilter(request, response);

  }
}
