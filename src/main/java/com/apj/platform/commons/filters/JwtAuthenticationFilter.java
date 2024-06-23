package com.apj.platform.commons.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.apj.platform.commons.vo.ParsedToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RestTemplate restTemplate;
    private final String tokenValidateUrl;

    public JwtAuthenticationFilter(
            @Value("${platform.token-validate.url}") String tokenValidateUrl,
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tokenValidateUrl = tokenValidateUrl;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt);
            HttpEntity<HttpHeaders> req = new HttpEntity<>(headers);

            ResponseEntity<ParsedToken> res = this.restTemplate.exchange(
                    this.tokenValidateUrl, HttpMethod.GET, req,
                    ParsedToken.class);

            if (res.getStatusCode() == HttpStatus.OK) {
                ParsedToken parsedToken = res.getBody();
                if (null != parsedToken) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            parsedToken.getUsername(),
                            null,
                            null);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
