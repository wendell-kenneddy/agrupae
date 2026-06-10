package com.agrupae.application.exception.group;

public class GroupModeChangeNotAllowedException extends RuntimeException {
    public GroupModeChangeNotAllowedException() {
        super("Group mode changing is not allowed for this assignment.");
    }
}
