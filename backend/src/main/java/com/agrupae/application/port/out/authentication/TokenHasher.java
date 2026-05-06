package com.agrupae.application.port.out.authentication;

public interface TokenHasher {
    String hash(String raw);
}
