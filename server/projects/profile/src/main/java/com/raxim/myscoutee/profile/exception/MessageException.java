package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class MessageException extends Exception {
    private final ErrorDTO errorDTO;

    public MessageException(ErrorDTO errorDTO) {
        this.errorDTO = errorDTO;
    }

    public ErrorDTO getErrorDTO() {
        return errorDTO;
    }
}
