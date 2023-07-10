package com.raxim.myscoutee.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;

import com.raxim.myscoutee.common.config.RepositoryConfig;
import com.raxim.myscoutee.common.repository.MongoDataLoaderTestExecutionListener;
import com.raxim.myscoutee.common.repository.TestData;
import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.dto.rest.EventDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;
import com.raxim.myscoutee.profile.handler.EventItemParamHandler;
import com.raxim.myscoutee.profile.handler.EventParamHandler;
import com.raxim.myscoutee.profile.repository.mongo.EventRepository;

@DataMongoTest
@DirtiesContext
@Import({ RepositoryConfig.class })
@TestPropertySource(properties = { "de.flapdoodle.mongodb.embedded.version=6.0.6",
                "logging.level.org.springframework.data.mongodb=DEBUG" })
// @TestData({"profiles" = mongo/profiles.json})
@TestData({ "mongo/profiles.json", "mongo/list/events.json" })
@TestExecutionListeners(value = MongoDataLoaderTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class EventRepositoryListTest {

        private static final UUID UUID_PROFILE_OLIVER = UUID.fromString("534ccc6b-2547-4bf0-ad91-dca739943ea4");
        private static final UUID UUID_EVENT_32 = UUID.fromString("15ad960f-b104-45ee-8510-ebf6ac78d3d8");

        @Autowired
        private EventRepository eventRepository;

        @Test
        public void shouldFindEventByMonth() {
                LocalDate from = LocalDate.of(2020, 1, 1);
                LocalDate createdDate = LocalDate.of(1901, 1, 1);
                LocalDate until = from.plusMonths(1);

                String fromF = from.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String untilF = until.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String createdDateF = createdDate.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                String[] tOffset = new String[] { fromF, createdDateF, untilF };

                PageParam pageParam = new PageParam(tOffset, EventParamHandler.MONTH);
                pageParam.setId(UUID_PROFILE_OLIVER);

                List<EventDTO> eventDTOs = this.eventRepository.findEventByMonth(pageParam,
                                new String[] { "A", "P", "C" });
                assertEquals(1, eventDTOs.size());

                LocalDateTime untilM = until.atStartOfDay().minus(1, ChronoUnit.MILLIS);
                assertEquals(untilM, eventDTOs.get(0).getItem().getRange().getEnd());
        }

        @Test
        public void shouldFindEventsByDayWithPagination() {
                LocalDate from = LocalDate.of(2020, 1, 1);
                LocalDate createdDate = LocalDate.of(1901, 1, 1);

                String fromF = from.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String createdDateF = createdDate.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                String[] tOffset = new String[] { fromF, createdDateF };

                PageParam pageParam = new PageParam(tOffset, EventParamHandler.MONTH);
                pageParam.setId(UUID_PROFILE_OLIVER);
                pageParam.setGroupKey(EventParamHandler.DAY_FORMAT);

                List<EventDTO> eventDTOs = this.eventRepository.findEventDown(pageParam,
                                new String[] { "A", "P", "C" });
                assertEquals(2, eventDTOs.size());

                // pagination with one item back and forward
                pageParam.setLimit(1);

                eventDTOs = this.eventRepository.findEventDown(pageParam,
                                new String[] { "A", "P", "C" });
                assertEquals(1, eventDTOs.size());
                assertEquals("Event32", eventDTOs.get(0).getItem().getName());

                String[] tOffsetM1 = eventDTOs.get(0).getOffset().stream()
                                .map(Object::toString)
                                .toArray(String[]::new);

                pageParam.setOffset(tOffsetM1);

                eventDTOs = this.eventRepository.findEventDown(pageParam,
                                new String[] { "A", "P", "C" });
                assertEquals(1, eventDTOs.size());
                assertEquals("Event2", eventDTOs.get(0).getItem().getName());

                eventDTOs = this.eventRepository.findEventUp(pageParam,
                                new String[] { "A", "P", "C" });
                assertEquals(1, eventDTOs.size());
                assertEquals(1, eventDTOs.size());
                assertEquals("Event31", eventDTOs.get(0).getItem().getName());
        }

        @Test
        public void shouldFindEventsByWeek() {
                LocalDate from = LocalDate.of(2020, 1, 1);
                LocalDate createdDate = LocalDate.of(1901, 1, 1);

                String fromF = from.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String createdDateF = createdDate.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                String[] tOffset = new String[] { fromF, createdDateF };

                PageParam pageParam = new PageParam(tOffset, EventParamHandler.MONTH);
                pageParam.setId(UUID_PROFILE_OLIVER);
                pageParam.setGroupKey(EventParamHandler.WEEK_FORMAT);

                List<EventDTO> eventDTOs = this.eventRepository.findEventDown(pageParam,
                                new String[] { "A", "P", "C" });
                assertEquals(2, eventDTOs.size());
                assertEquals("2020 04", eventDTOs.get(0).getGroupKey());
                assertEquals("2021 04", eventDTOs.get(1).getGroupKey());

        }

        @Test
        public void shouldFindEventItem() {
                LocalDate from = LocalDate.of(2020, 1, 1);
                LocalDate createdDate = LocalDate.of(1901, 1, 1);

                String fromF = from.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String createdDateF = createdDate.atStartOfDay(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                String[] tOffset = new String[] { fromF, createdDateF };

                PageParam pageParam = new PageParam(tOffset, EventParamHandler.MONTH);
                pageParam.setId(UUID_PROFILE_OLIVER);
                pageParam.setGroupKey(EventItemParamHandler.DAY_FORMAT);

                Optional<Event> optEvent = this.eventRepository.findById(UUID_EVENT_32);

                List<EventDTO> eventItemDTOs = this.eventRepository.findItemsByEvent(UUID_EVENT_32,
                                pageParam);
                assertEquals(1, eventItemDTOs.size());
                assertEquals("EventItem32", eventItemDTOs.get(0).getItem().getName());
                assertEquals("A", eventItemDTOs.get(0).getRole());
        }

}
