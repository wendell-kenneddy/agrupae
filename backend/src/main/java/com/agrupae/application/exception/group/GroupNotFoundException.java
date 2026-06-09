package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupNotFoundException extends ApplicationException {
    public GroupNotFoundException() {
        super("Group not found.");
    }
}
