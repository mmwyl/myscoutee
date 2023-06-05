package com.raxim.myscoutee.profile.service;

import com.raxim.myscoutee.profile.controller.months;
import com.raxim.myscoutee.profile.data.document.mongo.*;
import com.raxim.myscoutee.profile.repository.mongo.EventItemRepository;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.repository.mongo.MemberRepository;
import com.raxim.myscoutee.profile.repository.mongo.PromotionRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventItemRepository eventItemRepository;
    private final PromotionRepository promotionRepository;
    private final MemberRepository memberRepository;

    public EventService(
            EventRepository eventRepository,
            EventItemRepository eventItemRepository,
            PromotionRepository promotionRepository,
            MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.eventItemRepository = eventItemRepository;
        this.promotionRepository = promotionRepository;
        this.memberRepository = memberRepository;
    }

    public List<com.raxim.myscoutee.profile.data.dto.rest.Event> getEventsByStatus(
            String[] tOffset,
            UUID group,
            String status) {
        return this.eventRepository.findEventsByStatus(group, 20, tOffset, status);
    }

    public List<Event> getAllActiveEvents(UUID[] refIds) {
        return this.eventRepository.findActiveEvents(new String[]{"A", "P"}, refIds);
    }

    public List<Token> getAllActiveTokens(UUID[] refIds) {
        return this.eventRepository.findTokensByEvent(refIds);
    }

    public List<com.raxim.myscoutee.profile.data.dto.rest.Event> getEvents(
            String step,
            Integer direction,
            String[] tOffset,
            UUID profileId,
            String[] status) {
        List<com.raxim.myscoutee.profile.data.dto.rest.Event> events = new ArrayList<>();

        if (step == null || step.equals("d")) {
            if (direction == 1) {
                events = this.eventRepository.findEventDown(
                        profileId, 20, 5, "%Y-%m-%d", status,
                        tOffset, "A");
            } else {
                events = this.eventRepository.findEventUp(
                        profileId, 20, 5, "%Y-%m-%d", status,
                        tOffset, "A");
            }
        } else {
            if (step.equals("w")) {
                events = this.eventRepository.findEventDown(
                        profileId, 20, 5, "%Y %U", status,
                        tOffset, "A");
            } else if (step.equals("m")) {
                LocalDate from = LocalDate.parse(tOffset[0], DateTimeFormatter.ISO_DATE_TIME)
                        .with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();

                LocalDate until = LocalDate.parse(tOffset[0], DateTimeFormatter.ISO_DATE_TIME)
                        .with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()
                        .plusMonths(1);

                events = this.eventRepository.findEventByMonth(
                        profileId, 20, 5, "%Y-%m", status,
                        until.format(DateTimeFormatter.ISO_DATE_TIME),
                        tOffset, "A");

                events = events.stream().map(event -> {
                    List<String> dateParts = Arrays.asList(event.getGroupKey().toString().split("-"));
                    event.setGroupKey(dateParts.get(0) + " " + months[dateParts.get(1).toInt() - 1]);

                    RangeLocal range = event.getEvent().getInfo().getRange();
                    if (range != null) {
                        LocalDateTime start = range.getStart();
                        if (start.isBefore(from)) {
                            start = from;
                        }
                        LocalDateTime end = range.getEnd();
                        if (end.isAfter(until)) {
                            end = until.minusSeconds(1);
                        }
                        event.getEvent().getInfo().setRange(new RangeLocal(start, end));
                    }
                    return event;
                }).collect(Collectors.toList());
            }
        }
        return events;
    }

    public Optional<Event> cloneEvent(UUID eventId, Profile profile) {
        Optional<Event> eventRes = this.eventRepository.findById(eventId);

        if (eventRes.isPresent()) {
            Event event = eventRes.get().cloneBy(profile);
            this.memberRepository.saveAll(event.getInfo().getMembers().toList());
            this.eventItemRepository.saveAll(event.getItems());
            Event savedEvent = this.eventRepository.save(event);
            return Optional.of(savedEvent);
        } else {
            return Optional.empty();
        }
    }

    public Optional<com.raxim.myscoutee.profile.data.dto.rest.Event> recommendEvent(UUID eventId) {
        Optional<Event> eventRes = this.eventRepository.findById(eventId);

        if (eventRes.isPresent()) {
            com.raxim.myscoutee.profile.data.dto.rest.Event eventDto;

            Event event = eventRes.get();
            List<EventItem> items = event.getItems().stream()
                    .filter(item -> !item.getType().equals("pr") && !item.getStatus().equals("D"))
                    .map(item -> item.copy(UUID.randomUUID()))
                    .collect(Collectors.toList());
            event.setItems(this.eventItemRepository.saveAll(items));

            event.getInfo().setMembers(new HashSet<>());

            Event newEvent = event.copy(
                    UUID.randomUUID(),
                    "U",
                    event,
                    new Date()
            );

            Event savedEvent = this.eventRepository.save(newEvent);

            LocalDate groupKey = savedEvent.getInfo().getRange().getStart().toLocalDate();
            Long sortKey = savedEvent.getInfo().getRange().getStart().toInstant(ZoneOffset.UTC).toEpochMilli();

            eventDto = new com.raxim.myscoutee.profile.data.dto.rest.Event(
                    savedEvent,
                    groupKey,
                    sortKey
            );

            return Optional.of(eventDto);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Event> getEvent(
            EventItem eventItem,
            Profile profile,
            String status,
            UUID eventId,
            boolean isUpdate) {
        Optional<Event> eventRes;
        if (eventId != null) {
            eventRes = this.eventRepository.findById(eventId);
        } else {
            eventRes = Optional.empty();
        }

        if (eventRes.isPresent()) {
            Event event = eventRes.get();

            if (isUpdate) {
                if (eventItem.getId() != null) {
                    EventItem item = event.getItems().stream()
                            .filter(item -> item.getId().equals(eventItem.getId()))
                            .findFirst()
                            .orElse(null);
                    if (item != null) {
                        return Optional.of(event);
                    } else {
                        return Optional.empty();
                    }
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.of(event);
            }
        } else {
            if (!isUpdate) {
                Event event = new Event(
                        eventItem,
                        eventItem.getPosition(),
                        profile.getGroup(),
                        status,
                        profile.getId(),
                        new Date()
                );
                return Optional.of(event);
            } else {
                return Optional.empty();
            }
        }
    }

    public Optional<Pair<Event, EventItem>> saveEvent(Event pEvent, EventItem pEventItem) {
        Event event = pEvent;
        EventItem eventItem = event.getItems().stream()
                .filter(item -> pEventItem.getId().equals(item.getId()))
                .findFirst()
                .orElse(null);
        if (eventItem == null) {
            event.getItems().add(pEventItem);
        }

        event.getItems().sortBy(item -> item.getRange().getStart());
        event.setPositions(event.getItems().stream().map(EventItem::getPosition).collect(Collectors.toList()));

        event = event.shiftBy(pEventItem);

        List<EventItem> eventItems = this.eventItemRepository.saveAll(event.getItems());
        event = event.copy(eventItems);
        eventItem = eventItems.stream()
                .filter(item -> item.getId().equals(pEventItem.getId()))
                .findFirst()
                .get();

        return Optional.of(new Pair<>(event, eventItem));
    }

    public void saveEvents(List<Set<Profile>> groups) {
        List<Set<Member>> membersByGroup = groups.stream()
                .map(profiles -> profiles.stream()
                        .map(profile -> new Member(profile, "A", "U"))
                        .collect(Collectors.toSet()))
                .collect(Collectors.toList());

        List<EventItem> eventItems = membersByGroup.stream()
                .map(members -> {
                    LocalDateTime fromDT = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(21);
                    LocalDateTime toDT = fromDT.plusHours(3);
                    RangeLocal range = new RangeLocal(fromDT, toDT);

                    return new EventItem(
                            "g",
                            "l",
                            "Generated Event!",
                            "Generated Event for strangers!",
                            members,
                            range
                    );
                })
                .collect(Collectors.toList());

        List<EventItem> eventItemsSaved = this.eventItemRepository.saveAll(eventItems);

        List<Event> events = eventItemsSaved.stream()
                .map(eventItemSaved -> new Event(
                        eventItemSaved,
                        new ArrayList<>(Collections.singletonList(eventItemSaved)),
                        "A"
                ))
                .collect(Collectors.toList());

        this.eventRepository.saveAll(events);
    }

    public List<com.raxim.myscoutee.profile.data.dto.rest.EventItem> getEventItems(
            UUID eventId,
            Integer step,
            Object[] tOffset,
            UUID profileId) {
        return this.eventRepository.findItemsByEvent(
                eventId,
                20,
                step != null ? step : 5,
                "%Y-%m-%d",
                profileId,
                tOffset
        );
    }

    public List<com.raxim.myscoutee.profile.data.dto.rest.Event> getEventsByPromotion(
            UUID profileId,
            UUID eventId,
            Integer step,
            Object[] tOffset) {
        return this.promotionRepository.findEventsByPromotion(
                eventId,
                20,
                step != null ? step : 5,
                "%Y-%m-%d",
                profileId,
                tOffset
        );
    }
}
