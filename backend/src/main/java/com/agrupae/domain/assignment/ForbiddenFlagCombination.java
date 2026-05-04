package com.agrupae.domain.assignment;

import com.agrupae.domain.exception.DomainException;

public class ForbiddenFlagCombination extends DomainException {
    public ForbiddenFlagCombination(String message) {
        super(message);
    }
}
