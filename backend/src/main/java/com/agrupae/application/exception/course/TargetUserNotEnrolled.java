package com.agrupae.application.exception.course;

import com.agrupae.application.exception.ApplicationException;

public class TargetUserNotEnrolled extends ApplicationException{
    public TargetUserNotEnrolled(){
        super("It's only possible to transfer leadership of a course to a member.");
    }
}
