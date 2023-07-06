package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class EventFullException extends MessageException {

    public EventFullException() {
        super(new ErrorDTO(450, "err.event_full"));
    }
}
