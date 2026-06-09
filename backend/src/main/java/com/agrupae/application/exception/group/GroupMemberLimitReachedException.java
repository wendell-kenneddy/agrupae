package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupMemberLimitReachedException extends ApplicationException {
    public GroupMemberLimitReachedException() {
        super("Max group member limit reached.");
    }
}
