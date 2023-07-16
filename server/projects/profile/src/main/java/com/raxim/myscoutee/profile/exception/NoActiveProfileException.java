package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class NoActiveProfileException extends MessageException {

    public NoActiveProfileException() {
        super(new ErrorDTO(450, "err.no_profile"));
    }
}
