package com.agrupae.application.port.out.authentication;

import java.time.Duration;

public interface TokenConfig {
    Duration refreshTokenTTL();
}
