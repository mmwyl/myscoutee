package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class IllegalAccessException extends MessageException {

    public IllegalAccessException() {
        super(new ErrorDTO(450, "err.event_illegal_access"));
    }
}
