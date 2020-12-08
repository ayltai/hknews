package com.github.ayltai.hknews.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class SecurityFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(@NonNull final ServletRequest request, @NonNull final ServletResponse response, @NonNull final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse httpServletResponse = (HttpServletResponse)response;

        httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpServletResponse.setHeader("X-Frame-Options", "DENY");
        httpServletResponse.setHeader("X-XSS-Protection", "1; mode=block");

        chain.doFilter(request, response);
    }
}
