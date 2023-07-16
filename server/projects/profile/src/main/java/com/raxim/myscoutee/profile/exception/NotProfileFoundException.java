package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class NotProfileFoundException extends MessageException {

    public NotProfileFoundException() {
        super(new ErrorDTO(450, "err.not_selected_profile"));
    }
}
