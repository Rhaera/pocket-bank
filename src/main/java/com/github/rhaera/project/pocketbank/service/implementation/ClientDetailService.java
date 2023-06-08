package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;
import com.github.rhaera.project.pocketbank.model.entity.domain.ClientEntity;
import com.github.rhaera.project.pocketbank.repository.ClientDetailRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ClientDetailService implements UserDetailsManager, Authentication {
    private final ClientDetailRepository repository;
    private final JwtDecoder jwtDecoder;
    private final JavaMailSender mailSender;
    private final Map<String, Object> credentials;
    private static final String NOT_DEFINED = "NOT DEFINED";
    private boolean isAuthenticated = false;
    private ClientEntity possibleUser;

    public Optional<ClientDTO> readUser() {
        return Optional.of(possibleUser.toDto());
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    @Override
    public void createUser(UserDetails user) {
        try {
            possibleUser = new ClientDTO().reverseToDto(credentials.get("principal")
                                                                    .toString())
                                                                    .toEntity();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
        possibleUser.setEmprego(((String) credentials.get("details"))
                    .substring(String.valueOf(credentials.get("details"))
                    .indexOf("**") + 2, String.valueOf(credentials.get("details"))
                    .substring(String.valueOf(credentials.get("details"))
                    .indexOf("**") + 2)
                    .indexOf("**") + 2));
        possibleUser.setRendaMensal(Long.parseLong(((String) credentials.get("details"))
                    .replace(possibleUser.getEmprego(), "")
                    .replace("**", "")));
        if (Objects.nonNull(credentials.get("token")) &&
            jwtDecoder.decode((String) credentials.get("token"))
                        .getClaims()
                        .get("scope")
                        .equals(user.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.joining(" ")))) {
            repository.save(possibleUser);
            return;
        }
        credentials.put("error", true);
    }

    @Override
    public void updateUser(UserDetails user) {
        if (user.getAuthorities()
                .equals(Collections.singleton(new SimpleGrantedAuthority(repository.findByUsername(user.getUsername())
                                                                                    .orElseThrow()
                                                                                    .getRole())))) {
            repository.save(possibleUser);
            return;
        }
        credentials.put("error", true);
    }

    @Override
    public void deleteUser(String username) {
        repository.delete(possibleUser);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword)) {
            credentials.put("error", true);
            return;
        }
        possibleUser.setSenha(newPassword);
        repository.save(possibleUser);
    }

    @Override
    public boolean userExists(String username) {
        if (repository.findByUsername(username)
                        .isEmpty()) return false;
        possibleUser = repository.findByUsername(username)
                                .get();
        return true;
    }

    @Override
    public String getName() {
        try {
            return Objects.nonNull(possibleUser) ? possibleUser.getName() : new ClientDTO().reverseToDto((String) credentials.get("principal"))
                                                                                            .toEntity()
                                                                                            .getName();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!userExists(username)) throw new UsernameNotFoundException("USER NOT FOUND!");
        return new User(possibleUser.getEmail(),
                        possibleUser.getSenha(),
                        true,
                        true,
                        true,
                        true,
                        getAuthorities());
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (Objects.isNull(possibleUser)) {
            try {
                return Collections.singleton(new SimpleGrantedAuthority(new ClientDTO().reverseToDto((String) credentials.get("principal"))
                                                                                        .toEntity()
                                                                                        .getRole()));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return Collections.singleton(new SimpleGrantedAuthority(possibleUser.getRole()));
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return Objects.nonNull(credentials.get("details")) ? credentials.get("details") : NOT_DEFINED;
    }

    @Override
    public Object getPrincipal() {
        return Objects.nonNull(credentials.get("principal")) ? credentials.get("principal") : NOT_DEFINED;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }
}
