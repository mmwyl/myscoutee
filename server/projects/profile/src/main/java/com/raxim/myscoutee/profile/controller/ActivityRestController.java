package com.raxim.myscoutee.profile.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.common.util.CommonUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;
import com.raxim.myscoutee.profile.data.dto.rest.EventDTO;
import com.raxim.myscoutee.profile.data.dto.rest.MessageDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;
import com.raxim.myscoutee.profile.handler.EventParamHandler;
import com.raxim.myscoutee.profile.handler.MessageParamHandler;
import com.raxim.myscoutee.profile.handler.ParamHandlers;
import com.raxim.myscoutee.profile.service.EventService;
import com.raxim.myscoutee.profile.service.MessageService;

enum EventAction {
    lock("A"),
    unlock("P"); // based on gracePeriod it can be A, if mincapacity is ok

    private final String type;

    EventAction(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

@RestController
@RequestMapping("activity")
public class ActivityRestController {

    private final EventService eventService;
    private final MessageService messageService;
    private final ParamHandlers paramHandlers;

    public ActivityRestController(EventService eventService, MessageService messageService,
            ParamHandlers paramHandlers) {
        this.eventService = eventService;
        this.messageService = messageService;
        this.paramHandlers = paramHandlers;
    }

    @GetMapping("invitations")
    @Transactional
    public ResponseEntity<Object> getInvitations(PageParam pageParam, Authentication auth) {
        FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
        Profile profile = principal.getUser().getProfile();

        // override page param
        pageParam = paramHandlers.handle(profile, pageParam, EventParamHandler.TYPE);

        if (profile.getPosition() != null) {
            List<EventDTO> events = eventService.getInvitations(pageParam,
                    new String[] { "A", "P", "C" }); // the statuses is coming from the page filter (SettingsRepository)

            List<Object> lOffset = CommonUtil.offset(events, pageParam.getOffset());

            return ResponseEntity.ok(
                    new PageDTO<>(events, lOffset, 1, pageParam.getType()));
        } else {
            return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
        }
    }

    @GetMapping("channels")
    @Transactional
    public ResponseEntity<?> getChannels(PageParam pageParam, Authentication auth) {
        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        pageParam = paramHandlers.handle(profile, pageParam, MessageParamHandler.TYPE);

        List<MessageDTO> messageDTOs = this.messageService.getLastMessagesByChannels(pageParam);

        List<Object> lOffset = CommonUtil.offset(messageDTOs, pageParam.getOffset());
        return ResponseEntity.ok(new PageDTO<>(messageDTOs, lOffset));
    }

    @GetMapping("channels/{eventId}/items")
    @Transactional
    public ResponseEntity<?> getChannels(@PathVariable String eventId,
            PageParam pageParam, Authentication auth) {
        Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

        pageParam = paramHandlers.handle(profile, pageParam, MessageParamHandler.TYPE);

        List<MessageDTO> messageDTOs = this.messageService.getMessagesByChannel(UUID.fromString(eventId), pageParam);

        List<Object> lOffset = CommonUtil.offset(messageDTOs, pageParam.getOffset());
        return ResponseEntity.ok(new PageDTO<>(messageDTOs, lOffset, 1));
    }

}
