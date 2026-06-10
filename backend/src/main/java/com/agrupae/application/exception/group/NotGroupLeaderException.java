package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class NotGroupLeaderException extends ApplicationException {
    public NotGroupLeaderException() {
        super("Only the group leader can manage entry requests.");
    }
}
