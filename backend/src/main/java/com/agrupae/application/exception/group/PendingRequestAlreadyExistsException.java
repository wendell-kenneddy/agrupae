package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class PendingRequestAlreadyExistsException extends ApplicationException {
    public PendingRequestAlreadyExistsException() {
        super("A pending entry request already exists for this assignment.");
    }
}
