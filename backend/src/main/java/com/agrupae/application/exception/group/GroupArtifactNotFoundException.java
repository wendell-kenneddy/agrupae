package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class GroupArtifactNotFoundException extends ApplicationException {
    public GroupArtifactNotFoundException() {
        super("Group artifact not found.");
    }
}
