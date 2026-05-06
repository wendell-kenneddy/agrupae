package com.agrupae.application.port.out.user;

import java.util.UUID;

import com.agrupae.domain.user.User;

public interface UserRepository {
    public User findById(UUID id);

    public User findByEmail(String email);

    public User save(User user);

    public void deleteById(UUID id);
}
