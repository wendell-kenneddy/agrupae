package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class LeavingGroupNotAllowed extends ApplicationException {
    public LeavingGroupNotAllowed() {
        super("Leaving a group is not allowed for this assignment.");
    }
}
