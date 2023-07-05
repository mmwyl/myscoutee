package com.raxim.myscoutee.profile.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.Token;
import com.raxim.myscoutee.profile.data.dto.rest.EventDTO;
import com.raxim.myscoutee.profile.data.dto.rest.EventItemDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;
import com.raxim.myscoutee.profile.handler.EventParamHandler;
import com.raxim.myscoutee.profile.repository.mongo.EventItemRepository;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.repository.mongo.MemberRepository;
import com.raxim.myscoutee.profile.repository.mongo.PromotionRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventItemRepository eventItemRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository eventRepository,
            EventItemRepository eventItemRepository,
            PromotionRepository promotionRepository,
            MemberRepository memberRepository,
            ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.eventItemRepository = eventItemRepository;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }

    public List<EventDTO> getEventsByStatus(String[] tOffset, UUID group, String status) {
        return eventRepository.findEventsByStatus(group, 20, tOffset, status);
    }

    public List<Event> getAllActiveEvents(UUID[] refIds) {
        return eventRepository.findActiveEvents(new String[] { "A", "P" }, refIds);
    }

    public List<Token> getAllActiveTokens(UUID[] refIds) {
        return eventRepository.findTokensByEvent(refIds);
    }

    /*
     * javascript
     * 
     * const isoOffsetDateTime = '2023-07-05T10:30:00+05:30'; // Example
     * ISO_OFFSET_DATE_TIME string
     * 
     * // Create a new Date object from the ISO_OFFSET_DATE_TIME string
     * const date = new Date(isoOffsetDateTime);
     * 
     * // Get the time zone offset in minutes
     * const timezoneOffsetMinutes = date.getTimezoneOffset();
     * 
     * // Convert the offset to hours and minutes
     * const offsetHours = Math.floor(Math.abs(timezoneOffsetMinutes) / 60);
     * const offsetMinutes = Math.abs(timezoneOffsetMinutes) % 60;
     * 
     * // Determine the sign of the offset
     * const sign = timezoneOffsetMinutes > 0 ? '-' : '+';
     * 
     * // Format the offset as a string (e.g., +05:30 or -05:00)
     * const offsetString =
     * `${sign}${padZero(offsetHours)}:${padZero(offsetMinutes)}`;
     * 
     * console.log('Offset String:', offsetString);
     * 
     * // Helper function to pad a number with leading zeros
     * function padZero(number) {
     * return number.toString().padStart(2, '0');
     * }
     */
    public List<EventDTO> getEvents(PageParam pageParam, String[] status) {
        List<EventDTO> events = Collections.emptyList();
        if (EventParamHandler.DAY.equals(pageParam.getType())) {
            if (pageParam.getDirection() == 1) {
                events = eventRepository.findEventDown(pageParam, status);
            } else {
                events = eventRepository.findEventUp(pageParam, status);
            }
        } else if (EventParamHandler.WEEK.equals(pageParam.getType())) {
            events = eventRepository.findEventDown(pageParam, status);
        } else if (EventParamHandler.MONTH.equals(pageParam.getType())) {
            events = eventRepository.findEventByMonth(pageParam, status);
        }
        return events;
    }

    // TODO: to be fixed
    /*
     * public Optional<Event> cloneEvent(UUID eventId, Profile profile) {
     * Optional<Event> eventRes = eventRepository.findById(eventId);
     * 
     * if (eventRes.isPresent()) {
     * Event clonedEvent = EventUtil.cloneBy(eventRes.get(), profile, null, false,
     * objectMapper);
     * 
     * memberRepository.saveAll(clonedEvent.getInfo().getMembers());
     * eventItemRepository.saveAll(clonedEvent.getItems());
     * Event savedEvent = eventRepository.save(clonedEvent);
     * return Optional.of(savedEvent);
     * } else {
     * return Optional.empty();
     * }
     * }
     */

    // TODO: to be fixed
    /*
     * public Optional<EventDTO> recommendEvent(UUID eventId) {
     * Optional<Event> eventRes = eventRepository.findById(eventId);
     * 
     * if (eventRes.isPresent()) {
     * EventDTO eventDto;
     * 
     * Event event = eventRes.get();
     * List<EventItem> items = event.getItems().stream()
     * .filter(item -> !"pr".equals(item.getType()) &&
     * !"D".equals(item.getStatus()))
     * .map(item -> {
     * EventItem eventItem = JsonUtil.clone(item, objectMapper);
     * eventItem.setId(UUID.randomUUID());
     * return eventItem;
     * })
     * .collect(Collectors.toList());
     * event.setItems(eventItemRepository.saveAll(items));
     * 
     * event.getInfo().setMembers(new HashSet<>());
     * 
     * Event clonedEvent = JsonUtil.clone(event, objectMapper);
     * clonedEvent.setId(UUID.randomUUID());
     * clonedEvent.setStatus("U");
     * clonedEvent.setRef(event);
     * // clonedEvent.setCreatedDate(LocalDateTime.now());
     * 
     * Event savedEvent = eventRepository.save(clonedEvent);
     * 
     * LocalDate groupKey =
     * savedEvent.getInfo().getRange().getStart().toLocalDate();
     * Long sortKey =
     * savedEvent.getInfo().getRange().getStart().toInstant(ZoneOffset.UTC).
     * toEpochMilli();
     * 
     * eventDto = new EventDTO(savedEvent, groupKey, sortKey);
     * 
     * return Optional.of(eventDto);
     * } else {
     * return Optional.empty();
     * }
     * }
     */

    // TODO: to be fixed
    /*
     * public Optional<Event> getEvent(EventItem eventItem, Profile profile, String
     * status) {
     * return getEvent(eventItem, profile, status, null, false);
     * }
     */

    // TODO: to be fixed
    /*
     * public Optional<Event> getEvent(EventItem eventItem, Profile profile, String
     * status,
     * UUID eventId, boolean isUpdate) {
     * Optional<Event> eventRes = eventId != null ?
     * eventRepository.findById(eventId) : Optional.empty();
     * 
     * if (eventRes.isPresent()) {
     * Event event = eventRes.get();
     * 
     * if (isUpdate) {
     * if (eventItem.getId() != null) {
     * EventItem item = event.getItems().stream()
     * .filter(i -> i.getId().equals(eventItem.getId()))
     * .findFirst()
     * .orElse(null);
     * if (item != null) {
     * return Optional.of(event);
     * } else {
     * return Optional.empty();
     * }
     * } else {
     * return Optional.empty();
     * }
     * } else {
     * return Optional.of(event);
     * }
     * } else {
     * if (!isUpdate) {
     * Event event = new Event();
     * event.setInfo(eventItem);
     * event.setPosition(eventItem.getPosition());
     * event.setGroup(profile.getGroup());
     * event.setStatus(status);
     * event.setCreatedBy(profile.getId());
     * event.setCreatedDate(LocalDateTime.now());
     * return Optional.of(event);
     * } else {
     * return Optional.empty();
     * }
     * }
     * }
     */

    // TODO: to be fixed
    /*
     * public Optional<Pair<Event, EventItem>> saveEvent(Event pEvent, EventItem
     * pEventItem) {
     * Event event = pEvent;
     * EventItem eventItem = event.getItems().stream()
     * .filter(item -> pEventItem.getId().equals(item.getId()))
     * .findFirst()
     * .orElse(null);
     * if (eventItem == null) {
     * event.getItems().add(pEventItem);
     * }
     * 
     * event.getItems().sort(Comparator.comparing(item ->
     * item.getRange().getStart()));
     * event.setPositions(event.getItems().stream().map(EventItem::getPosition).
     * collect(Collectors.toList()));
     * 
     * event = EventUtil.shiftBy(event, pEventItem, objectMapper);
     * 
     * List<EventItem> eventItems = eventItemRepository.saveAll(event.getItems());
     * 
     * event = JsonUtil.clone(event, objectMapper);
     * event.setItems(eventItems);
     * 
     * event = eventRepository.save(event);
     * eventItem = eventItems.stream()
     * .filter(item -> item.getId().equals(pEventItem.getId()))
     * .findFirst()
     * .orElse(null);
     * 
     * return Optional.of(Pair.of(event, eventItem));
     * }
     */

    public List<EventItemDTO> getEventItems(UUID eventId, Integer step, Object[] tOffset, UUID profileId) {
        return eventRepository.findItemsByEvent(eventId, 20, step != null ? step : 5, "%Y-%m-%d", profileId, tOffset);
    }
}
