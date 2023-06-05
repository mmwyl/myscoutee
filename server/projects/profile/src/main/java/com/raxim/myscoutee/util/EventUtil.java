package com.raxim.myscoutee.util;

import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.EventItem;
import com.raxim.myscoutee.profile.data.document.mongo.Member;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.RangeLocal;
import com.raxim.myscoutee.profile.data.document.mongo.Slot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EventUtil {

    public static Event shiftBy(Event event, EventItem eventItem) {
        long minutes;
        RangeLocal evtRange;
        if (eventItem.getId().equals(event.getInfo().getId())) {
            minutes = ChronoUnit.MINUTES.between(event.getInfo().getRange().getStart(), eventItem.getRange().getStart());
            evtRange = new RangeLocal(eventItem.getRange().getStart(), eventItem.getRange().getEnd());
            event.setInfo(eventItem.copyWithRange(evtRange, event.getInfo().getMembers(), event.getInfo().getNum()));
        } else {
            minutes = 0L;
            RangeLocal maxRange = event.getItems().stream()
                    .map(item -> {
                        if (item.getId().equals(eventItem.getId())) {
                            return eventItem.getRange();
                        } else {
                            return item.getRange();
                        }
                    })
                    .reduce((acc, range) -> {
                        LocalDateTime start = acc.getStart();
                        if (range.getStart().isBefore(start)) {
                            start = range.getStart();
                        }

                        LocalDateTime end = acc.getEnd();
                        if (range.getEnd().isAfter(end)) {
                            end = range.getEnd();
                        }
                        return new RangeLocal(start, end);
                    })
                    .orElse(null);
            evtRange = maxRange;
            event.setInfo(event.getInfo().copyWithRange(evtRange));
        }

        List<EventItem> updatedItems = event.getItems().stream()
                .map(item -> {
                    EventItem currItem;
                    if (event.getItems().size() == 1 || event.getInfo().getId().equals(item.getId())) {
                        currItem = event.getInfo();
                    } else if (item.getId().equals(eventItem.getId())) {
                        currItem = eventItem.copyWithMembersAndNum(item.getMembers(), item.getNum());
                    } else {
                        currItem = item;
                    }

                    if (minutes > 0) {
                        RangeLocal range = new RangeLocal(
                                item.getRange().getStart().plusMinutes(minutes),
                                item.getRange().getEnd().plusMinutes(minutes)
                        );
                        return currItem.copyWithRange(range);
                    } else {
                        return currItem;
                    }
                })
                .toList();

        event.setItems(updatedItems);

        return event;
    }

    public static Event cloneBy(Event event, Profile profile, Slot slot, boolean isRef) {
        Event newEvent = event.copyWithId(UUID.randomUUID());
        if (slot != null) {
            newEvent.getInfo().setCapacity(slot.getCapacity());
            newEvent.setCnt(slot.getNumOfItems());

            LocalDate slotDate = slot.getRange().getStart().toLocalDate();
            LocalDateTime infoStart = event.getInfo().getRange().getStart();
            LocalDateTime infoEnd = event.getInfo().getRange().getEnd();

            long days = ChronoUnit.DAYS.between(infoStart.toLocalDate(), slotDate);
            RangeLocal evtRange = new RangeLocal(infoStart.plusDays(days), infoEnd.plusDays(days));
            EventItem eventItem = newEvent.getInfo().copyWithRange(evtRange);

            newEvent = shiftBy(newEvent, eventItem);
        }

        List<EventItem> updatedItems = event.getItems().stream()
                .filter(item -> !"pr".equals(item.getType()) && !"D".equals(item.getStatus()))
                .map(item -> item.copyWithId(UUID.randomUUID()))
                .toList();

        updatedItems.set(0, newEvent.getInfo());

        Set<Member> members = new HashSet<>(newEvent.getInfo().getMembers());
        String status = (newEvent.getStatus().equals("T") || !members.isEmpty()) ? "P" : "A";
        String memberStatus = newEvent.getStatus().equals("T") ? "P" : "A";
        String role = newEvent.getStatus().equals("T") ? "P" : "M";
        UUID newEventId = UUID.randomUUID();
        members.add(new Member(profile.getId(), profile, memberStatus, role, newEventId));

        Event ref = !isRef ? event : null;

        newEvent.getInfo().setMembers(members);
        newEvent.getInfo().setNum(newEvent.getInfo().getNum() + 1);

        newEvent = newEvent.copyWithId(newEventId)
                .withStatus(status)
                .withInfo(newEvent.getInfo())
                .withGroup(profile.getGroup())
                .withRef(ref);

        return newEvent;
    }
}

