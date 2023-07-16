package com.raxim.myscoutee.profile.exception;

import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;

public class FriendsOnlyException extends MessageException {

    public FriendsOnlyException() {
        super(new ErrorDTO(450, "err.not_selected_profile"));
    }
}
