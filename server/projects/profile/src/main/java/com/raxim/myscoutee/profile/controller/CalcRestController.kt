package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.profile.repository.mongo.UserRepository
import com.raxim.myscoutee.profile.service.EventScheduler
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("calc")
class CalcRestController(private val eventScheduler: EventScheduler, private val userRepository: UserRepository) {

    @GetMapping("/priority_none/{lastTime}")
    fun calcPriorityNone(@PathVariable lastTime: String): ResponseEntity<Void> {

        this.eventScheduler.generateEvents()

        return ResponseEntity.ok().build()
    }

    @GetMapping("/messaging/{lastTime}")
    fun registerMessaging(@PathVariable lastTime: String): ResponseEntity<Void> {
        this.eventScheduler.topicRegistration(lastTime)

        return ResponseEntity.ok().build()
    }
}