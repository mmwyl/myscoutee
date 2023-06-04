package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.common.config.ConfigProperties
import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.document.mongo.Role
import com.raxim.myscoutee.profile.data.dto.rest.Error
import com.raxim.myscoutee.profile.data.dto.rest.Page
import com.raxim.myscoutee.profile.repository.mongo.*
import com.raxim.myscoutee.profile.service.EventService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RepositoryRestController
@RequestMapping("recommendations")
class RecommendationRestController(
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val eventService: EventService,
    private val promotionRepository: PromotionRepository,
    private val groupRepository: GroupRepository,
    private val roleRepository: RoleRepository,
    private val config: ConfigProperties
) {

    @GetMapping("promotions")
    @Transactional
    fun promotions(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 5) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode().toDouble(),
                offset[4].decode()
            )
        } else {
            arrayOf(0.0, 0.0, 0.0, 0.0, LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        // type is promotion type = job, idea, event
        if (profile.position != null) {
            // 'type': ?4, 'group': ?5 is not used, until filter is not in place to make something visible
            val promotions = this.promotionRepository.findPromotionsByRec(
                profile.id!!,
                profile.position!!.point(),
                20, step ?: 5,
                "j", profile.group!!,
                tOffset
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (promotions.isNotEmpty()) {
                promotions.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(promotions, lOffset, 0)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    // promotion
    @GetMapping("promotions/{promoId}/events")
    fun events(
        @PathVariable promoId: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?,
        auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Event>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val tOffset: Array<Any> = if (offset != null && offset.size == 3) {
            arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
        } else {
            arrayOf("1900-01-01", 10, "1900-01-01")
        }

        // rate needs to be added to the query by profile like queried by Ref
        val events = this.eventService.getEventsByPromotion(profileId, UUID.fromString(promoId), step, tOffset)

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (events.isNotEmpty()) {
            events.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(events, lOffset)
        )
    }

    @PostMapping("promotions/{promoId}/events/{id}/clone")
    fun clone(
        @PathVariable promoId: String,
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<*> {
        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        val events = this.eventRepository.findPendingEvents(UUID.fromString(id), profile.id!!)
        val promoter =
            events.firstOrNull { event -> event.info!!.members!!.any { member -> member.profile.id == profile.id } }

        return if (promoter == null) {
            val eventDto = eventService.cloneEvent(UUID.fromString(id), profile)
            if (eventDto.isPresent) {
                val event = eventDto.get().transform()
                ResponseEntity(event, HttpStatus.OK)
            } else {
                ResponseEntity.badRequest().build()
            }
        } else {
            ResponseEntity.badRequest().body(Error(450, "err.clone_not_allowed"))
        }
    }

    @GetMapping("promotions/{promoId}/events/{id}/feedbacks")
    fun eventsFeedbacks(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Feedback>> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        val feedbacks = this.eventRepository.findFeedbacksByEvent(
            UUID.fromString(id), 20, step ?: 5,
            tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (feedbacks.isNotEmpty()) {
            feedbacks.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(feedbacks, lOffset)
        )

    }

    @GetMapping("promotions/{promoId}/events/{id}/items")
    fun eventItems(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?,
        auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.EventItem>> {
        val profile = (auth.principal as FirebasePrincipal).user.profile

        val tOffset: Array<Any> = if (offset != null && offset.size == 2) {
            arrayOf(offset[0].decode(), offset[1].decode())
        } else {
            arrayOf("1900-01-01", "1900-01-01")
        }

        val eventItems = this.eventService.getEventItems(UUID.fromString(id), step, tOffset, profile!!.id!!)

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (eventItems.isNotEmpty()) {
            eventItems.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(eventItems, lOffset)
        )
    }

    @GetMapping("groups")
    @Transactional
    fun groups(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 5) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble()
            )
        } else {
            arrayOf(0.0, "1900-01-01", 0.0)
        }

        val user = (auth.principal as FirebasePrincipal).user
        val profile = user.profile!!

        if (profile.position != null) {
            val group = this.groupRepository.findById(user.group!!).get()

            val groupUUIDs =
                user.profiles!!.filter { profile -> profile.status != "D" }.map { profile -> profile.group!! }

            val groups = this.groupRepository.findGroupByProfile(
                group.type!!,
                profile.position!!.point(),
                20, step ?: 5,
                groupUUIDs,
                tOffset
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (groups.isNotEmpty()) {
                groups.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(groups, lOffset, 0)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @PostMapping("groups/{groupId}/join")
    fun join(
        @PathVariable groupId: String, auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.document.mongo.Group> {

        val user = (auth.principal as FirebasePrincipal).user

        val isProfile =
            user.profiles!!.any { profile -> profile.group == UUID.fromString(groupId) && profile.status != "D" }

        if (!isProfile) {
            val profileToSave =
                user.profile!!.copy(id = UUID.randomUUID(), group = UUID.fromString(groupId), status = "P")
            val profileSaved = this.profileRepository.save(profileToSave)

            val role = Role(UUID.randomUUID(), profileSaved.id!!, "ROLE_USER")
            this.roleRepository.save(role)

            val userToSave = this.userRepository.findById(user.id!!).get()
            userToSave.profiles!!.add(profileSaved)
            this.userRepository.save(userToSave)
        }

        val group = this.groupRepository.findById(UUID.fromString(groupId));

        return if (group.isPresent) {
            ResponseEntity(group.get(), HttpStatus.OK)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("events")
    @Transactional
    fun events(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 2) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble()
            )
        } else {
            arrayOf(0.0, "1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        if (profile.position != null) {
            val events = this.eventRepository.findEventsByProfile(
                profile.id!!,
                profile.position!!.point(),
                20, step ?: 5,
                profile.group!!,
                tOffset
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (events.isNotEmpty()) {
                events.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(events, lOffset, 0)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @PostMapping("events/{id}/clone")
    fun cloneEvent(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Event> {
        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        val eventDto = eventService.cloneEvent(UUID.fromString(id), profile)
        return if (eventDto.isPresent) {
            val event = eventDto.get().transform()
            ResponseEntity(event, HttpStatus.OK)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    //people who you don't met, but your surrounding is ordered by rate
    @Deprecated("profiles recommendation removed")
    @GetMapping("profiles")
    @Transactional
    fun getInvitations(
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 5) {
            arrayOf(
                offset[0].decode().toDouble(),
                offset[1].decode().toDouble(),
                offset[2].decode().toDouble(),
                offset[3].decode().toDouble(),
                offset[4].decode()
            )
        } else {
            arrayOf(0.0, 0.0, 0.0, 0.0, LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        if (profile.position != null) {
            val profiles = this.profileRepository.findPeopleByProfile(
                profile.id!!,
                profile.position!!.point(),
                20, step ?: 5,
                profile.gender!!, profile.group!!,
                tOffset, 1.5
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (profiles.isNotEmpty()) {
                profiles.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(profiles, lOffset, 0)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @GetMapping(value = ["events/{id}/items", "invitations/{id}/items"])
    fun items(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?,
        auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.EventItem>> {
        val profile = (auth.principal as FirebasePrincipal).user.profile

        val tOffset: Array<Any> = if (offset != null && offset.size == 2) {
            arrayOf(offset[0].decode(), offset[1].decode())
        } else {
            arrayOf("1900-01-01", "1900-01-01")
        }

        val eventItems = this.eventRepository.findItemsByEvent(
            UUID.fromString(id), 20, step ?: 5, "%Y-%m-%d", profile!!.id!!,
            tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (eventItems.isNotEmpty()) {
            eventItems.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(eventItems, lOffset)
        )
    }

    @GetMapping(value = ["events/{id}/feedbacks", "invitations/{id}/feedbacks"])
    fun feedbacks(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Feedback>> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        val feedbacks = this.eventRepository.findFeedbacksByEvent(
            UUID.fromString(id), 20, step ?: 5,
            tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (feedbacks.isNotEmpty()) {
            feedbacks.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(feedbacks, lOffset)
        )

    }

}