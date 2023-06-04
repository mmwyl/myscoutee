package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.common.FileUtil
import com.raxim.myscoutee.profile.data.document.mongo.School
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@BasePathAwareController
class BaseOverrideRestController {
    @RequestMapping(
        path = [
            "schools", "schools/{id}", "cars", "cars/{id}", "items", "items/{id}",
            "feedbacks", "feedbacks/{id}", "setting", "setting/{id}"
        ]
    )
    fun doRequest(): ResponseEntity<School> {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build<School>();
    }

    @Throws(
        IOException::class
    )
    @GetMapping(
        path = [
            "profiles/{id}/images/{name}",
            "user/profile/images/{name}",
            "user/cars/{id}/images/{name}",
            "user/cars/images/{name}",
            "user/groups/images/{name}",
            "user/groups/{id}/images/{name}",
            "user/groups/{id}/profiles/{pId}/images/{name}",
            "profiles/{profileId}/cars/{id}/images/{name}",
            "profiles/{profileId}/cars/images/{name}",
            "games/{type}/{id}/images/{name}",
            "activity/events/{eventId}/members/{id}/images/{name}",
            "activity/events/{eventId}/items/{itemId}/members/{id}/images/{name}",
            "activity/invitations/{eventId}/members/{id}/images/{name}",
            "promotions/{promoId}/events/{eventId}/members/{id}/images/{name}",
            "promotions/{promoId}/members/{id}/images/{name}",
            "ideas/people/{id}/images/{name}",
            "games/rate_double/{selected}/{type}/{id}/images/{name}"
        ]
    )
    fun download(@PathVariable name: String, response: HttpServletResponse) {
        val pName = name.split("_orig")
        val sub = if (pName.size > 1) "_orig" else ""

        val (separator, tmpDir) = FileUtil.tempToPath(pName[0])
        var file = File("$tmpDir${separator}_${pName[0]}$sub")

        if (!file.exists()) {
            val (_, fullDir) = FileUtil.uuidToPath(pName[0])
            file = File("$fullDir${separator}_${pName[0]}$sub")
        }
        FileUtil.copyFileToHttp(response, file)
    }

    @Throws(
        IOException::class
    )
    @GetMapping(
        path = [
            "profiles/{id}/voice/{name}"
        ]
    )
    fun voice(@PathVariable id: String, @PathVariable name: String, response: HttpServletResponse) {

        val (separator, fullDir) = FileUtil.uuidToPath(name)
        val file = File("$fullDir${separator}_${name}_voice")

        FileUtil.copyFileToHttp(response, file)
    }
}