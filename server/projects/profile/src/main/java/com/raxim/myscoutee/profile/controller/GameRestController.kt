package com.raxim.myscoutee.profile.controller

import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.dto.rest.Error
import com.raxim.myscoutee.profile.data.dto.rest.Page
import com.raxim.myscoutee.profile.data.dto.rest.Profile
import com.raxim.myscoutee.profile.repository.mongo.EventRepository
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository
import com.raxim.myscoutee.profile.repository.mongo.UserRepository
import com.raxim.myscoutee.profile.service.ProfileService
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun GeoJsonPoint.point(): Point {
    return Point(Position(this.coordinates))
}

@RepositoryRestController
@RequestMapping("games")
class GameRestController(
    val profileRepository: ProfileRepository,
    val eventRepository: EventRepository,
    val likeRepository: LikeRepository,
    val userRepository: UserRepository,
    val profileService: ProfileService
) {

    @GetMapping(
        value = ["/rate_none/{id}/schools", "/rate_give/{id}/schools",
            "/rate_give/{id}/schools", "/rate_give/{id}/schools", "/rate_give/{id}/schools",
            "rate_receive/{id}/schools", "/rate_double/{id}/rated/{id}/schools",
            "/rate_double/{id}/none/{id}/schools",
            "/rate_both/{id}/schools", "/rate_met/{id}/schools"]
    )
    fun getSchools(
        @PathVariable id: String, auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.School>> {
        val tOffset: Array<Any> = if (offset != null && offset.size == 3) {
            arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
        } else {
            arrayOf("a", "1900-01-01", "1900-01-01")
        }

        val schools = this.profileService.getSchools(UUID.fromString(id), step, tOffset)

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
        val lOffset = if (schools.isNotEmpty()) {
            schools.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(schools, lOffset)
        )
    }

    @GetMapping("/rate_none")
    fun nonRated(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val tOffset: Array<Any> = if (offset != null && offset.size == 4) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode()
            )
        } else {
            arrayOf(0, 0, 0, "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        val gender = if (profile.gender == "m") "w" else "m"

        if (profile.status == "F") {
            return ResponseEntity.badRequest().body(Error(450, "err.friends_only"))
        }

        if (profile.status == "I") {
            return ResponseEntity.badRequest().body(Error(450, "err.invisible"))
        }

        if (profile.position != null) {
            val profiles = this.profileRepository.findProfile(
                profile.position!!.point(),
                tOffset, 20, step ?: 5,
                profile.id!!,
                gender,
                profile.group!!,
                0.0,
                profile.score
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset;
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping("/rate_give")
    fun rateGive(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val tOffset: Array<Any> = if (offset != null && offset.size == 4) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode()
            )
        } else {
            arrayOf(0, 0, 0, "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        val gender = if (profile.gender == "m") "w" else "m"

        if (profile.status == "F") {
            return ResponseEntity.badRequest().body(Error(450, "err.friends_only"))
        }

        if (profile.status == "I") {
            return ResponseEntity.badRequest().body(Error(450, "err.invisible"))
        }

        if (profile.position != null) {
            val profiles = this.profileRepository.findProfile(
                profile.position!!.point(),
                tOffset, 20, step ?: 5,
                profile.id!!,
                gender,
                profile.group!!,
                1.0
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset;
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping("/rate_receive")
    fun rateGet(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val tOffset: Array<Any> = if (offset != null && offset.size == 4) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode()
            )
        } else {
            arrayOf(0, 0, 0, "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        val gender = if (profile.gender == "m") "w" else "m"

        if (profile.status == "F") {
            return ResponseEntity.badRequest().body(Error(450, "err.friends_only"))
        }

        if (profile.status == "I") {
            return ResponseEntity.badRequest().body(Error(450, "err.invisible"))
        }

        if (profile.position != null) {
            val fProfiles = this.profileRepository.findProfile(
                profile.position!!.point(),
                tOffset, 20, step ?: 5,
                profile.id!!,
                gender,
                profile.group!!,
                2.0
            )

            // nullify rate received
            val profiles = fProfiles.map { fProfile -> fProfile.copy(rate = 0) }

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset;
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping("/rate_double/{id}/rated")
    fun rateDouble(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        if (profile.position != null) {
            val profiles = this.likeRepository.findDoubleById(
                profile.id!!,
                UUID.fromString(id),
                20, step ?: 5,
                arrayOf("A", "F"),
                tOffset
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset;
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping("/rate_double/{id}/none")
    fun doubleNone(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val selected = this.profileRepository.findById(UUID.fromString(id))

        val sProfile = if (selected.isPresent) {
            selected.get()
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }

        val sGender: String = if (sProfile.gender!! == "w") {
            "m"
        } else {
            "w"
        }

        val tOffset: Array<Any> = if (offset != null && offset.size == 4) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode()
            )
        } else {
            arrayOf(0, 0, 0, "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        if (profile.position != null) {
            val profiles = this.profileRepository.findProfileNoType(
                profile.position!!.point(),
                tOffset, 20, step ?: 5,
                sProfile.id!!,
                sGender,
                profile.group!!,
                profile.id!!,
                0.0 // last parameter not used
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset;
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping("/rate_both")
    fun rateBoth(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val tOffset: Array<Any> = if (offset != null && offset.size == 4) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode()
            )
        } else {
            arrayOf(0, 0, 0, "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        val gender = if (profile.gender == "m") "w" else "m"

        if (profile.status == "F") {
            return ResponseEntity.badRequest().body(Error(450, "err.friends_only"))
        }

        if (profile.status == "I") {
            return ResponseEntity.badRequest().body(Error(450, "err.invisible"))
        }

        if (profile.position != null) {
            val profiles = this.profileRepository.findProfile(
                profile.position!!.point(),
                tOffset, 20, step ?: 5,
                profile.id!!,
                gender,
                profile.group!!,
                1.5
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset;
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping("/rate_met")
    fun history(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Page<Profile>> {
        //parameter ? man : woman, filter out type = double, filter out from, to field is the session user and type = single
        // (add field for history rated by)

        val tOffset: Array<Any> = if (offset != null && offset.size == 4) {
            arrayOf(offset[1].decode(), offset[2].decode(), offset[3].decode())
        } else {
            arrayOf(LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME), "1900-01-01", "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        val profiles = this.eventRepository.findProfileByEvent(
            profile.id!!,
            20, step ?: 5, tOffset, "A"
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (profiles.isNotEmpty()) {
            profiles.last().offset;
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(profiles, lOffset, 0)
        )
    }
}