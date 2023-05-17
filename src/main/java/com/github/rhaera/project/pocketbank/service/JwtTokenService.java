package com.github.rhaera.project.pocketbank.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.Token;

import java.time.Instant;

public interface JwtTokenService extends Token {
    String getToken(Authentication auth, Instant instant);
}
