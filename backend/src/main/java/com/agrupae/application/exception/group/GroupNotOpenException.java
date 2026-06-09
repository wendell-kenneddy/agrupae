package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupNotOpenException extends ApplicationException {
    public GroupNotOpenException() {
        super("Group is not open.");
    }
}
