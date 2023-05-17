package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;
import com.github.rhaera.project.pocketbank.model.entity.domain.ClientEntity;
import com.github.rhaera.project.pocketbank.repository.ClientDetailRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ClientDetailService implements UserDetailsService {
    private final ClientDetailRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClientEntity client = repository.findByUsername(username)
                                        .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND!"));
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new User(client.getEmail(),
                        client.getSenha(),
                        enabled,
                        accountNonExpired,
                        credentialsNonExpired,
                        accountNonLocked,
                        List.of(getAuthority(client.getRole())));
    }
    public Optional<ClientDTO> insertUser(ClientEntity entity) {
        return Optional.of(repository.save(entity).toDto());
    }
    public Optional<ClientDTO> renewUserDetails(ClientEntity entity) throws IOException {
        ClientEntity existingEntity = repository.findByCpf(entity.getCpf())
                                                .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND!"));
        existingEntity.atualizarDadosViaveis(entity);
        return Optional.of(repository.save(existingEntity)
                        .toDto());
    }
    public Optional<ClientDTO> readUserDetails(String cpf) {
        return Optional.of(repository.findByCpf(cpf)
                        .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND!"))
                        .toDto());
    }
    public Optional<ClientDTO> removeUser(String cpf) {
        ClientEntity deletedEntity = repository.findByCpf(cpf)
                                                .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND!"));
        repository.delete(deletedEntity);
        return Optional.of(deletedEntity.toDto());
    }
    private static GrantedAuthority getAuthority(String role) {
        return new SimpleGrantedAuthority(role);
    }
}
