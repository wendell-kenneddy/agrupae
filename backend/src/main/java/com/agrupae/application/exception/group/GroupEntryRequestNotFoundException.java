package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupEntryRequestNotFoundException extends ApplicationException {
    public GroupEntryRequestNotFoundException() {
        super("Entry request not found.");
    }
}
