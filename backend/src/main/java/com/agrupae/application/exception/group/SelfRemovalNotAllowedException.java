package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class SelfRemovalNotAllowedException extends ApplicationException {
    public SelfRemovalNotAllowedException() {
        super("The group leader cannot remove themselves; use the leave group action instead.");
    }
}
