package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.service.JwtTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private static final String NO_TOKEN_MADE = "NO TOKEN MADE!";
    private String token;

    @Override
    public String getToken(Authentication auth) {
        token = jwtEncoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
                                                                        .issuer(auth.getPrincipal()
                                                                        .toString()
                                                                        .concat(auth.getDetails()
                                                                        .toString()))
                                                                        .issuedAt(Instant.now())
                                                                        .expiresAt(Instant.now()
                                                                        .plus(1, ChronoUnit.HOURS))
                                                                        .subject(auth.getName())
                                                                        .claim("scope", auth.getAuthorities()
                                                                        .stream()
                                                                        .map(GrantedAuthority::getAuthority)
                                                                        .collect(Collectors.joining(" ")))
                                                                        .build()))
                                                                        .getTokenValue();
        return token;
    }
    @Override
    public String getKey() {
        return Objects.nonNull(token) ? Objects.requireNonNull(jwtDecoder.decode(token)
                                                .getIssuedAt())
                                                .toString() : NO_TOKEN_MADE;
    }
    @Override
    public long getKeyCreationTime() {
        return Objects.nonNull(token) ? Objects.requireNonNull(jwtDecoder.decode(token)
                                                .getIssuedAt())
                                                .toEpochMilli() : 0L;
    }
    @Override
    public String getExtendedInformation() {
        return Objects.nonNull(token) ? jwtDecoder.decode(token)
                                                    .getSubject()
                                                    .concat(("-").concat(jwtDecoder.decode(token)
                                                    .getClaims()
                                                    .toString())) : NO_TOKEN_MADE;
    }
}
