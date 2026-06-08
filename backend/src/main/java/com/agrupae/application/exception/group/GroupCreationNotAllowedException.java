package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupCreationNotAllowedException extends ApplicationException {
    public GroupCreationNotAllowedException() {
        super("Group creation is not allowed for this assignment.");
    }
}
