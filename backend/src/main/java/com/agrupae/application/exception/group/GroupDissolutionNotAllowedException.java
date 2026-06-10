package com.agrupae.application.exception.group;

public class GroupDissolutionNotAllowedException extends RuntimeException {
    public GroupDissolutionNotAllowedException() {
        super("Group dissolution is not allowed for this assignment.");
    }
}
