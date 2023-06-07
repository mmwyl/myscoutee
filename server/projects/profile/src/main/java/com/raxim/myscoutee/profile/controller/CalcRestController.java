package com.raxim.myscoutee.profile.controller;

import com.raxim.myscoutee.profile.repository.mongo.UserRepository;
import com.raxim.myscoutee.profile.service.EventScheduler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping("calc")
public class CalcRestController {

    private final EventScheduler eventScheduler;
    private final UserRepository userRepository;

    public CalcRestController(EventScheduler eventScheduler, UserRepository userRepository) {
        this.eventScheduler = eventScheduler;
        this.userRepository = userRepository;
    }

    @GetMapping("/priority_none/{lastTime}")
    public ResponseEntity<Void> calcPriorityNone(@PathVariable String lastTime) {
        eventScheduler.generateEvents();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messaging/{lastTime}")
    public ResponseEntity<Void> registerMessaging(@PathVariable String lastTime) {
        eventScheduler.topicRegistration(lastTime);
        return ResponseEntity.ok().build();
    }
}
