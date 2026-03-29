package SkillExchange.Backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.security.web.authentication
        .WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        String token = null;
        String email = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

            // wrap in try-catch so expired token
            // does not crash the app
            try {
                email = jwtUtil.getEmail(token);
            } catch (Exception e) {
                // token expired or invalid — just ignore
                // request will be blocked by security automatically
            }
        }

        if (email != null && SecurityContextHolder
                .getContext().getAuthentication() == null) {

            try {
                if (!jwtUtil.isExpired(token)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    email, null, new ArrayList<>()
                            );
                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            } catch (Exception e) {
                // token expired — do nothing
                // spring security will return 403 automatically
            }
        }

        chain.doFilter(request, response);
    }

}