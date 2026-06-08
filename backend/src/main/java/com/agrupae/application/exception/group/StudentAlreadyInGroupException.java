package com.agrupae.application.exception.group;

import com.agrupae.application.exception.ApplicationException;

public class StudentAlreadyInGroupException extends ApplicationException {
    public StudentAlreadyInGroupException() {
        super("Student already belongs to a group in this assignment.");
    }
}
