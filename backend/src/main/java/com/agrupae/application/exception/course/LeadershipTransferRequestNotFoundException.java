package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class LeadershipTransferRequestNotFoundException extends ApplicationException {
    public LeadershipTransferRequestNotFoundException() {
        super("Leadership transfer request not found.");
    }
}
