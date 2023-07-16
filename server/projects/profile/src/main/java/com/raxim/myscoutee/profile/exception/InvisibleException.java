package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class InvisibleException extends MessageException {

    public InvisibleException() {
        super(new ErrorDTO(450, "err.err.invisible"));
    }
}
