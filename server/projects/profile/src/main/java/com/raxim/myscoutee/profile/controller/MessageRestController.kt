package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.profile.data.document.mongo.Message
import com.raxim.myscoutee.profile.service.MessageService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("messages")
class MessageRestController(
    private val messageService: MessageService
) {

    @GetMapping("")
    fun getMsg(
        @RequestHeader("accept-language") locale: String
    ): ResponseEntity<Message> {
        val message = this.messageService.getMessageByLang(locale);
        return ResponseEntity.ok(message!!)
    }

}