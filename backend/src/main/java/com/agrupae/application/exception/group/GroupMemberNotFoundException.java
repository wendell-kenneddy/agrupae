package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupMemberNotFoundException extends ApplicationException {
    public GroupMemberNotFoundException() {
        super("Group member not found.");
    }
}
