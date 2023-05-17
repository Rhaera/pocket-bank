package com.github.rhaera.project.pocketbank.config.security;

import com.github.rhaera.project.pocketbank.service.implementation.ClientDetailService;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import org.apache.tomcat.util.codec.binary.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final ClientDetailService detailService;
    private final File privateKeyFile = new File("src/main/resources/certs/private.pem");
    private final File publicKeyFile = new File("src/main/resources/certs/public.pem");
    public SecurityConfig(ClientDetailService detailService) {
        this.detailService = detailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.anyRequest()
                                                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(detailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public RSAPrivateKey rsaPrivateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.decodeBase64(Files.readString(privateKeyFile.toPath(), Charset.defaultCharset())
                                                                    .replace("-----BEGIN PRIVATE KEY-----", "")
                                                                    .replaceAll(System.lineSeparator(), "")
                                                                    .replace("-----END PRIVATE KEY-----", ""))));
    }
    @Bean
    public RSAPublicKey rsaPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.decodeBase64(Files.readString(publicKeyFile.toPath(), Charset.defaultCharset())
                                                                    .replace("-----BEGIN PUBLIC KEY-----", "")
                                                                    .replaceAll(System.lineSeparator(), "")
                                                                    .replace("-----END PUBLIC KEY-----", "")))
        );
    }
    @Bean
    public JwtDecoder jwtDecoder() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey()).build();
    }
    @Bean
    public JwtEncoder jwtEncoder() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return new NimbusJwtEncoder(
                new ImmutableJWKSet<>(
                        new JWKSet(
                                new RSAKey.Builder(rsaPublicKey())
                                        .privateKey(rsaPrivateKey())
                                        .build()
                        )
                )
        );
    }
}
