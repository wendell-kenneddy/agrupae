package com.agrupae.application.port.out.authentication;

import com.agrupae.domain.user.User;

public interface TokenProvider {
    String generateAccessToken(User user);
}
