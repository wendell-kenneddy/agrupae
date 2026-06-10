package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupMemberRemovalNotAllowedException extends ApplicationException {
    public GroupMemberRemovalNotAllowedException() {
        super("Group member removal is not allowed for this assignment.");
    }
}
