package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class AssignmentArchivedException extends ApplicationException {
    public AssignmentArchivedException() {
        super("Cannot create group in an archived assignment.");
    }
}
