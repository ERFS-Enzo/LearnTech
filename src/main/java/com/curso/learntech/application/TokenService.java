package com.curso.learntech.application;

import com.curso.learntech.config.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock clock;

    public TokenService(
            JwtEncoder encoder,
            JwtProperties properties,
            Clock clock) {

        this.encoder = encoder;
        this.properties = properties;
        this.clock = clock;
    }

    public TokenAcesso emitir(
            Authentication authentication,
            UUID sessao) {

        Instant agora = clock.instant();

        var perfis = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .sorted()
                .toList();

        var claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(authentication.getName())
                .issuedAt(agora)
                .expiresAt(agora.plus(properties.expiration()))
                .claim("perfis", perfis);

        if (sessao != null) {
            claims.claim("sid", sessao.toString());
        }

        var header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = encoder.encode(
                        JwtEncoderParameters.from(
                                header,
                                claims.build()))
                .getTokenValue();

        return new TokenAcesso(
                token,
                properties.expiration().toSeconds());
    }

    public record TokenAcesso(String valor, long expiresIn) {
    }
}