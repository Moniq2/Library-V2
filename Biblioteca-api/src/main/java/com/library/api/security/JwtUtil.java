package com.library.api.security;
import com.library.api.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.expiration}")
    private long tempoExpiracaoMs;

    private SecretKey obterChaveAssinatura() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    public String gerarToken(UserDetails usuario) {
        Date dataEmissao = new Date(System.currentTimeMillis());
        Date dataExpiracao = new Date(System.currentTimeMillis() + tempoExpiracaoMs);
        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(dataEmissao)
                .expiration(dataExpiracao)
                .signWith(obterChaveAssinatura())
                .compact();
    }

    public String extrairUsername(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public Date extrairDataExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    public <T> T extrairClaim(String token, Function<Claims, T> resolvedorDeClaim) {
        Claims claims = extrairTodasAsClaims(token);
        return resolvedorDeClaim.apply(claims);
    }

    private Claims extrairTodasAsClaims(String token) {
        return Jwts.parser()
                .verifyWith(obterChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean tokenExpirado(String token) {
        Date dataExpiracao = extrairDataExpiracao(token);
        return dataExpiracao.before(new Date());
    }

    public boolean tokenValido(String token, UserDetails usuario) {
        String usernameDoToken = extrairUsername(token);
        boolean mesmoUsuario = usernameDoToken.equals(usuario.getUsername());
        boolean naoExpirado = !tokenExpirado(token);
        return mesmoUsuario && naoExpirado;
    }
}
