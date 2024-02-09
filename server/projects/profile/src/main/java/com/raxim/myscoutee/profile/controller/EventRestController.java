package com.raxim.myscoutee.profile.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raxim.myscoutee.common.config.firebase.dto.FirebasePrincipal;
import com.raxim.myscoutee.common.util.CommonUtil;
import com.raxim.myscoutee.common.util.ControllerUtil;
import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.User;
import com.raxim.myscoutee.profile.data.dto.rest.CloneDTO;
import com.raxim.myscoutee.profile.data.dto.rest.ErrorDTO;
import com.raxim.myscoutee.profile.data.dto.rest.EventDTO;
import com.raxim.myscoutee.profile.data.dto.rest.MemberDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;
import com.raxim.myscoutee.profile.handler.EventItemParamHandler;
import com.raxim.myscoutee.profile.handler.EventParamHandler;
import com.raxim.myscoutee.profile.handler.EventRecParamHandler;
import com.raxim.myscoutee.profile.handler.MemberParamHandler;
import com.raxim.myscoutee.profile.handler.ParamHandlers;
import com.raxim.myscoutee.profile.service.EventService;

@RestController
@RequestMapping("history")
public class EventRestController {

        private final EventService eventService;
        private final ParamHandlers paramHandlers;

        public EventRestController(EventService eventService,
                        ParamHandlers paramHandlers) {
                this.eventService = eventService;
                this.paramHandlers = paramHandlers;
        }

        @GetMapping("events")
        @Transactional
        public ResponseEntity<Object> getEvents(PageParam pageParam, Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                // override page param
                pageParam = paramHandlers.handle(profile, pageParam, EventParamHandler.TYPE);

                if (profile.getPosition() != null) {
                        List<EventDTO> events = eventService.getEvents(pageParam,
                                        new String[] { "A", "P", "C" }); // the statuses is coming from the page filter
                                                                         // (SettingsRepository)

                        List<Object> lOffset = CommonUtil.offset(events, pageParam.getOffset());

                        return ResponseEntity.ok(
                                        new PageDTO<>(events, lOffset, 0, pageParam.getStep()));
                } else {
                        return ResponseEntity.badRequest().body(new ErrorDTO(450, "err.no_profile"));
                }
        }

        // TODO: multislot fix - saveEvent and saveItem should be the same method!!!
        // status needs to be filled in the event either T or P
        @PostMapping("events")
        @Transactional
        public ResponseEntity<EventDTO> createEvent(@RequestBody Event event,
                        Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                event.setStatus("P");
                ResponseEntity<EventDTO> response = ControllerUtil.handle((p, e) -> eventService.save(p, e), profile,
                                event,
                                HttpStatus.CREATED);
                return response;
        }

        @GetMapping({ "events/recommendations" })
        public ResponseEntity<Object> recommendations(PageParam pageParam, Authentication auth) {

                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                User user = principal.getUser();
                Profile profile = user.getProfile();

                pageParam = paramHandlers.handle(profile, pageParam, EventRecParamHandler.TYPE);
                List<EventDTO> eventDTOs = this.eventService.getRecEvents(pageParam,
                                CommonUtil.point(profile.getPosition()),
                                user.getGroup(), "F");

                List<Object> lOffset = CommonUtil.offset(eventDTOs, pageParam.getOffset());

                return ResponseEntity.ok(
                                new PageDTO<>(eventDTOs, lOffset, 0));
        }

        // can lock event, filter for own events (organized by me)
        @PatchMapping({ "events/{id}" })
        @Transactional
        public ResponseEntity<?> patchEvent(@PathVariable String id, @RequestBody Event event,
                        Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                ResponseEntity<EventDTO> response = ControllerUtil.handle((p, e) -> eventService.save(p, e), profile,
                                event,
                                HttpStatus.OK);
                return response;
        }

        @DeleteMapping({ "events/{id}" })
        @Transactional
        public ResponseEntity<?> deleteEvent(@PathVariable String id,
                        Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                Event event = new Event(UUID.fromString(id));
                event.setStatus("D");

                ResponseEntity<EventDTO> response = ControllerUtil.handle((p, e) -> eventService.save(p, e), profile,
                                event,
                                HttpStatus.OK);
                return response;
        }

        @PostMapping({ "events/{id}/items", "events/{parentId}/items/{id}/items" })
        public ResponseEntity<EventDTO> addItem(@PathVariable String id, @RequestBody Event eventItem,
                        Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                ResponseEntity<EventDTO> response = ControllerUtil.handle((p, i, ei) -> eventService.save(p, i, ei),
                                profile, eventItem, id,
                                HttpStatus.CREATED);
                return response;
        }

        @PatchMapping({ "events/{id}/items/{itemId}", "events/{parentId}/items/{id}/items/{itemId}" })
        public ResponseEntity<EventDTO> patchItem(@PathVariable String id, @PathVariable String itemId,
                        @RequestBody Event eventItem, Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                eventItem.setId(UUID.fromString(itemId));

                ResponseEntity<EventDTO> response = ControllerUtil.handle((p, i, ei) -> eventService.save(p, i, ei),
                                profile, eventItem, id,
                                HttpStatus.OK);
                return response;
        }

        @DeleteMapping({ "events/{id}/items/{itemId}", "events/{parentId}/items/{id}/items/{itemId}" })
        public ResponseEntity<EventDTO> deleteItem(@PathVariable String id, @PathVariable String itemId,
                        Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                Event eventItem = new Event(UUID.fromString(itemId));
                eventItem.setStatus("D");

                ResponseEntity<EventDTO> response = ControllerUtil.handle((p, i, ei) -> eventService.save(p, i, ei),
                                profile, eventItem, id,
                                HttpStatus.NO_CONTENT);
                return response;
        }

        @GetMapping(value = { "events/{id}/members" })
        public ResponseEntity<PageDTO<MemberDTO>> getMembersForEvent2(@PathVariable String id, PageParam pageParam,
                        Authentication auth) {

                FirebasePrincipal firebasePrincipal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = firebasePrincipal.getUser().getProfile();

                pageParam = paramHandlers.handle(profile, pageParam, MemberParamHandler.TYPE);
                List<MemberDTO> members = eventService.getMembersByEvent(pageParam, id);
                List<Object> lOffset = CommonUtil.offset(members, pageParam.getOffset());

                Object lRole = !members.isEmpty() ? members.get(members.size() - 1).getRole() : null;

                return ResponseEntity.ok(new PageDTO<>(members, lOffset, null, pageParam.getStep(), lRole));
        }

        // TODO: promotion fix, only events for the current stage can be shown
        @GetMapping(value = { "events/{eventId}/items", "events/{id}/items/{eventId}/items" })
        public ResponseEntity<PageDTO<EventDTO>> getItems(@PathVariable String eventId,
                        PageParam pageParam, Authentication auth) {
                FirebasePrincipal principal = (FirebasePrincipal) auth.getPrincipal();
                Profile profile = principal.getUser().getProfile();

                // override page param
                pageParam = paramHandlers.handle(profile, pageParam, EventItemParamHandler.TYPE);

                List<EventDTO> eventItems = eventService.getEventItems(pageParam, eventId);
                List<Object> lOffset = CommonUtil.offset(eventItems, pageParam.getOffset());

                return ResponseEntity.ok(new PageDTO<>(eventItems, lOffset));
        }

        @PostMapping("events/{id}/clone")
        public ResponseEntity<List<EventDTO>> cloneEvent(
                        @PathVariable String id,
                        Authentication auth, @RequestBody CloneDTO cloneDTO) {
                Profile profile = ((FirebasePrincipal) auth.getPrincipal()).getUser().getProfile();

                ResponseEntity<List<EventDTO>> response = ControllerUtil.handleList(
                                (i, p, ei) -> eventService.cloneBy(i, p, ei),
                                id, profile, cloneDTO,
                                HttpStatus.CREATED);
                return response;
        }

        @PostMapping("events/{id}/{type}")
        public ResponseEntity<List<EventDTO>> lockEvent(
                        @PathVariable String id, @PathVariable String type,
                        Authentication auth) {
                String actionType = EventAction.valueOf(type).getType();

                ResponseEntity<List<EventDTO>> response = ControllerUtil.handleList(
                                (i, a) -> eventService.changeStatus(i, a),
                                id, actionType,
                                HttpStatus.OK);
                return response;
        }
}
