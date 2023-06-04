package com.raxim.myscoutee.profile.service

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class])
class EventSchedulerTest {

    @Autowired
    lateinit var eventScheduler: EventScheduler

    @Test
    @Throws(Exception::class)
    fun generateEvent() {
        this.eventScheduler.generateEvents()
    }
}