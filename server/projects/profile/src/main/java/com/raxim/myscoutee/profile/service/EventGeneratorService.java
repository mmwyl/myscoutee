package com.raxim.myscoutee.profile.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.Member;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.service.iface.IEventGeneratorService;

@Service
public class EventGeneratorService implements IEventGeneratorService {
    private final EventRepository eventRepository;

    public EventGeneratorService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> generate() {
        List<Event> events = this.eventRepository.findEvents();

        List<Event> handledEvents = events.stream().map(event -> {
            event.syncStatus();

            int maxStage = event.getMaxStage();
            if (maxStage > 0 && "A".equals(event.getStatus())) {
                List<Event> currStageEvents = event.getItems().stream()
                        .filter(item -> "A".equals(item.getStatus()) && item.getStage() == event.getStage()).toList();

                // calculate the winners of each event and sum up

                List<Member> winners = new ArrayList<>();

                event.assignToSlots(winners);

            }
            return event;
        }).toList();

        List<Event> eventsToSave = handledEvents.stream().flatMap(event -> event.flatten().stream()).toList();

        List<Event> savedEvents = this.eventRepository.saveAll(eventsToSave);
        return savedEvents;
    }

}
