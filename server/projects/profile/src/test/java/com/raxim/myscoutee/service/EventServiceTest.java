package com.raxim.myscoutee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.common.config.JsonConfig;
import com.raxim.myscoutee.data.mongo.TestEventItem;
import com.raxim.myscoutee.data.mongo.TestProfile;
import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.EventItem;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.repository.mongo.EventItemRepository;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;
import com.raxim.myscoutee.profile.repository.mongo.MemberRepository;
import com.raxim.myscoutee.profile.service.EventService;
import com.raxim.myscoutee.util.TestJsonUtil;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(classes = JsonConfig.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventItemRepository eventItemRepository;

    @Mock
    private MemberRepository memberRepository;

    @Autowired
    @Spy
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<List<EventItem>> captorEventItems;

    @Captor
    private ArgumentCaptor<List<Event>> captorEvents;

    @Test
    public void shouldSaveEvents() throws IOException {
        // json property override
        objectMapper.addMixIn(EventItem.class, TestEventItem.class);

        Profile[] profileArray = TestJsonUtil.loadJson(this, "rest/profiles.json",
                TestProfile[].class,
                objectMapper);
        EventItem[] eventItemArray = TestJsonUtil.loadJson(this, "mongo/eventItems.json",
                EventItem[].class,
                objectMapper);

        List<Profile> profiles = Arrays.asList(profileArray);
        List<EventItem> eventItems = Arrays.asList(eventItemArray);

        when(eventItemRepository.saveAll(anyList())).thenReturn(eventItems);

        // build parameters
        List<Set<Profile>> profileByGroups = new ArrayList<>();
        profileByGroups.add(Set.of(profiles.get(0), profiles.get(1))); // Sophia - Oliver
        profileByGroups.add(Set.of(profiles.get(2), profiles.get(4))); // Ethan - Emma

        eventService.saveEvents(profileByGroups);

        Mockito.verify(eventItemRepository).saveAll(captorEventItems.capture());
        List<EventItem> capturedEventItems = captorEventItems.getValue();

        assertEquals(2, capturedEventItems.size());

        Mockito.verify(eventRepository).saveAll(captorEvents.capture());
        List<Event> capturedEvents = (List<Event>) captorEvents.getValue();

        assertEquals(2, capturedEvents.size());
    }
}
