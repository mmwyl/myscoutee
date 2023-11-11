package com.raxim.myscoutee.profile.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raxim.myscoutee.common.config.MqttConfig.MqttGateway;
import com.raxim.myscoutee.profile.service.EventScheduler;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("admin")
public class AdminController {

    private final EventScheduler eventScheduler;
    private MqttGateway mqttGateway;

    public AdminController(EventScheduler eventScheduler, MqttGateway mqttGateway) {
        this.eventScheduler = eventScheduler;
        this.mqttGateway = mqttGateway;
    }

    @GetMapping("random")
    @Transactional
    public void getEvents() {
        this.eventScheduler.autoGenerateRooms();
    }

    @PostMapping("publish/**")
    public void publishMessage(HttpServletRequest request, @RequestBody String message) {
        String topic = request.getRequestURI().split("publish/")[1];
        mqttGateway.sendToMqtt(decode(topic), message);
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error decoding path variable", e);
        }
    }
}
