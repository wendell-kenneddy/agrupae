package com.agrupae.application.port.out.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import com.agrupae.domain.user.User;

public interface UserRepository {
    public User findById(UUID id);

    public User findByEmail(String email);

    public User save(User user);

    public void deleteById(UUID id);

    public Page<User> findAllByIdIn(List<UUID> ids, Pageable pageable);
}
