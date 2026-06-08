package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class MaxGroupsReachedException extends ApplicationException {
    public MaxGroupsReachedException() {
        super("Maximum number of groups for this assignment has been reached.");
    }
}
