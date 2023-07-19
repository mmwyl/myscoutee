package com.raxim.myscoutee.profile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.raxim.myscoutee.profile.data.document.mongo.Event;
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

        List<Event> eventsToSave = events.stream().map(event -> {
            event.syncStatus();
            return event;
        }).toList();

        List<Event> savedEvents = this.eventRepository.saveAll(eventsToSave);
        return savedEvents;
    }

}
