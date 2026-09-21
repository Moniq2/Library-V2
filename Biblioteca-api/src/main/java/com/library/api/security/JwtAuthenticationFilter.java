package com.library.api.security;

import com.library.api.service.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UsuarioDetailsService usuarioDetailsService) {
        this.jwtUtil = jwtUtil;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String cabecalhoAuthorization = request.getHeader("Authorization");
        if (cabecalhoAuthorization == null || !cabecalhoAuthorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = cabecalhoAuthorization.substring(7);
        String username = jwtUtil.extrairUsername(token);

        boolean ninguemAutenticadoAinda = SecurityContextHolder.getContext().getAuthentication() == null;

        if (username != null && ninguemAutenticadoAinda) {
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(username);

            if (jwtUtil.tokenValido(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credenciais (senha) não são necessárias aqui, o token já provou a identidade
                                userDetails.getAuthorities());

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

       filterChain.doFilter(request, response);
    }
}
