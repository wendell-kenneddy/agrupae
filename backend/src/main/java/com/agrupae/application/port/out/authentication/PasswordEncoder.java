package com.agrupae.application.port.out.authentication;

public interface PasswordEncoder {
    public String encode(String raw);

    public boolean matches(String raw, String hash);
}
