package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupNotClosedException extends ApplicationException {
    public GroupNotClosedException() {
        super("Group is not closed.");
    }
}
