package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.document.mongo.*
import com.raxim.myscoutee.profile.data.dto.rest.Error
import com.raxim.myscoutee.profile.data.dto.rest.Page
import com.raxim.myscoutee.profile.repository.mongo.EventItemRepository
import com.raxim.myscoutee.profile.repository.mongo.EventRepository
import com.raxim.myscoutee.profile.repository.mongo.PromotionRepository
import com.raxim.myscoutee.profile.service.CampaignService
import com.raxim.myscoutee.profile.service.EventService
import com.raxim.myscoutee.profile.service.ProfileService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun Promotion.save(
    campaignService: CampaignService,
    profile: Profile
): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Promotion> {
    val promotion = campaignService.getPromotion(this, profile)
    return if (promotion.isPresent) {
        val oldPromotion = promotion.get().first
        val savedPromotion = campaignService.savePromotion(oldPromotion, profile)
        if (savedPromotion.isPresent) {
            ResponseEntity(
                com.raxim.myscoutee.profile.data.dto.rest.Promotion(savedPromotion.get()),
                HttpStatus.CREATED
            )
        } else {
            ResponseEntity.badRequest().build()
        }
    } else {
        ResponseEntity.notFound().build()
    }
}

fun Promotion.update(
    campaignService: CampaignService,
    profile: Profile,
    isEvent: Boolean = false //event date range updates promotion if it's true
): ResponseEntity<out Any> {
    val promotion = campaignService.getPromotion(this, profile, true, isEvent)
    val resp = if (promotion.isPresent) {
        val oldPromotion = promotion.get().first

        val refIds =
            oldPromotion.events.filter { event -> event.status != "D" }.map { event -> event.id!! }.toTypedArray()
        val events = campaignService.getAllActiveEvents(refIds)
        if (events.isNotEmpty()) {
            ResponseEntity.badRequest().body(Error(450, "err.has_active_events"))
        } else {
            val savedPromotion = campaignService.savePromotion(
                oldPromotion,
                profile,
                isEvent,
                promotion.get().second
            )
            if (savedPromotion.isPresent) {
                ResponseEntity(
                    com.raxim.myscoutee.profile.data.dto.rest.Promotion(savedPromotion.get()),
                    HttpStatus.CREATED
                )
            } else {
                ResponseEntity.badRequest().build()
            }
        }
    } else {
        ResponseEntity.notFound().build()
    }
    return resp
}

@RepositoryRestController
@RequestMapping("promotions")
class CampaignRestController(
    private val campaignService: CampaignService,
    private val eventService: EventService,
    private val eventRepository: EventRepository,
    private val eventItemRepository: EventItemRepository,
    private val promotionRepository: PromotionRepository,
    private val profileService: ProfileService
) {

    @GetMapping("")
    fun getPromotions(
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Any> {

        val profile = (auth.principal as FirebasePrincipal).user.profile

        val tOffset: Array<Any> = if (offset != null && offset.size == 2) {
            arrayOf(offset[0].decode(), offset[1].decode())
        } else {
            arrayOf("1900-01-01", "1900-01-01")
        }

        if (profile?.id == null) {
            return ResponseEntity.notFound().build()
        } else {
            if (profile.position != null) {
                val promotions = this.campaignService.getPromotions(profile.id, step, tOffset)

                //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
                val lOffset = if (promotions.isNotEmpty()) {
                    promotions.last().offset
                } else {
                    tOffset.toList()
                }

                return ResponseEntity.ok(
                    Page(promotions, lOffset)
                )
            } else {
                return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
            }
        }
    }

    @PostMapping("")
    @Transactional
    fun addPromotion(
        auth: Authentication,
        @RequestBody promotion: Promotion
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Promotion> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return promotion.save(this.campaignService, profile!!)
    }

    @PatchMapping("/{id}")
    @Transactional
    fun patchPromotion(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody promotion: Promotion
    ): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return promotion.update(this.campaignService, profile!!)
    }

    // promotion
    @GetMapping("{promoId}/events")
    fun events(
        @PathVariable promoId: String,
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Event>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val tOffset: Array<Any> = if (offset != null && offset.size == 3) {
            arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
        } else {
            arrayOf("1900-01-01", 10, "1900-01-01")
        }

        val events =
            this.eventService.getEventsByPromotion(profileId, UUID.fromString(promoId), step, tOffset).map { event ->
                event.rate = null; event
            }.toList()

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

    // event date can't be changed
    @PatchMapping("{promoId}/events/{id}")
    @Transactional
    fun patchPromotionEvent(
        @PathVariable promoId: String,
        @PathVariable id: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<*> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        val eventResp = eventItem.update(eventService, id, profile!!)

        Promotion(id = UUID.fromString(promoId)).update(this.campaignService, profile, true)

        return eventResp
    }

    @GetMapping("{promoId}/events/{id}/items")
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

    @PostMapping("{promoId}/events/{id}/items")
    fun addPromoEventItem(
        @PathVariable promoId: String,
        @PathVariable id: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        val eventResp = eventItem.save(eventService, id, profile!!)

        Promotion(id = UUID.fromString(promoId)).update(this.campaignService, profile, true)

        return eventResp
    }

    @PatchMapping("{promoId}/events/{id}/items/{itemId}")
    fun patchPromoEventItem(
        @PathVariable promoId: String,
        @PathVariable id: String, @PathVariable itemId: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        val eventResp = eventItem.update(eventService, id, itemId, profile!!)

        Promotion(id = UUID.fromString(promoId)).update(this.campaignService, profile, true)

        return eventResp
    }

    //delete date align
    @DeleteMapping("{promoId}/events/{id}/items/{itemId}")
    fun deletePromoItem(
        @PathVariable id: String,
        @PathVariable itemId: String
    ): ResponseEntity<Any> {

        val event = this.eventRepository.findById(UUID.fromString(id)).get()
        if (event.info!!.id == UUID.fromString(itemId)) {
            return ResponseEntity.badRequest().body(Error(450, "err.first_item"))
        }

        val item = this.eventItemRepository.findById(UUID.fromString(itemId)).get()
        item.status = "D"
        this.eventItemRepository.save(item)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("{promoId}/events/{id}/members")
    fun eventMembers(
        @PathVariable eventId: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?,
        auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Profile>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val tOffset: Array<Any> = if (offset != null && offset.size == 3) {
            arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
        } else {
            arrayOf("A", "1900-01-01", "1900-01-01")
        }

        val profiles = this.eventRepository.findProfilesByPromotion(
            UUID.fromString(eventId), 20, step ?: 5, profileId, tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (profiles.isNotEmpty()) {
            profiles.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(profiles, lOffset)
        )
    }

    @GetMapping(
        value = ["{promoId}/events/{eventId}/members/{id}/schools", "{promoId}/members/{id}/schools"]
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

    @GetMapping("{promoId}/events/{id}/feedbacks")
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

    @GetMapping("{promoId}/members")
    fun members(
        @PathVariable promoId: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?,
        auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Profile>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val tOffset: Array<Any> = if (offset != null && offset.size == 3) {
            arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
        } else {
            arrayOf("A", "1900-01-01", "1900-01-01")
        }

        val profiles = this.promotionRepository.findProfilesByPromotion(
            UUID.fromString(promoId), 20, step ?: 5, profileId, tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (profiles.isNotEmpty()) {
            profiles.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(profiles, lOffset)
        )
    }

    @Deprecated("no ideas anymore")
    @GetMapping("/ideas")
    fun getIdeas(
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Idea>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        if (profileId == null) {
            return ResponseEntity.notFound().build()
        } else {
            val ideas = this.campaignService.getIdeas(profileId, step, tOffset)

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
            val lOffset = if (ideas.isNotEmpty()) {
                ideas.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(ideas, lOffset)
            )
        }
    }

    @Deprecated("no ideas anymore")
    @PostMapping("/ideas")
    @Transactional
    fun addIdea(
        auth: Authentication,
        @RequestBody idea: Idea
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Idea> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val ideaDto = this.campaignService.saveIdea(profileId, null, idea)
            ResponseEntity(ideaDto, HttpStatus.CREATED)
        }
    }

    @Deprecated("no ideas anymore")
    @PatchMapping("/ideas/{id}")
    @Transactional
    fun patchIdea(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody idea: Idea
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Idea> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val ideaDto = this.campaignService.saveIdea(profileId, id.uuid(), idea)
            ResponseEntity(ideaDto, HttpStatus.OK)
        }
    }

    @Deprecated("no jobs anymore")
    @GetMapping("/jobs")
    fun getJobs(
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Job>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        if (profileId == null) {
            return ResponseEntity.notFound().build()
        } else {
            val jobs = this.campaignService.getJobs(profileId, step, tOffset)

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
            val lOffset = if (jobs.isNotEmpty()) {
                jobs.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(jobs, lOffset)
            )
        }
    }

    @Deprecated("no jobs anymore")
    @PostMapping("/jobs")
    @Transactional
    fun addJob(
        auth: Authentication,
        @RequestBody job: Job
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Job> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val jobDto = this.campaignService.saveJob(profileId, null, job)
            ResponseEntity(jobDto, HttpStatus.CREATED)
        }
    }

    @Deprecated("no jobs anymore")
    @PatchMapping("/jobs/{id}")
    @Transactional
    fun patchJob(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody job: Job
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Job> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val jobDto = this.campaignService.saveJob(profileId, id.uuid(), job)
            ResponseEntity(jobDto, HttpStatus.OK)
        }
    }

    @GetMapping("/templates")
    @Transactional
    fun getEvents(
        @RequestParam("step") step: String?,
        @RequestParam("direction") direction: Int? = 1,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Event>> {

        val tOffset: Array<String> = if (offset != null && offset.size == 2) {
            if (step == "Month") {
                val from = LocalDate.parse(offset[0].decode(), DateTimeFormatter.ISO_DATE_TIME).withDayOfMonth(1)
                    .atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)

                arrayOf(from, offset[1].decode())
            } else {
                arrayOf(offset[0].decode(), offset[1].decode())
            }
        } else {
            if (step == "Month") {
                val from = LocalDate.now().withDayOfMonth(1)
                    .atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)
                arrayOf(from, "1900-01-01")
            } else {
                arrayOf(LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME), "1900-01-01")
            }
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        val events =
            this.eventService.getEvents(step, direction, tOffset, profileId = profile.id!!, status = arrayOf("T"))

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val offset = if (events.isNotEmpty()) {
            events.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(events, offset, 0)
        )
    }

    @PostMapping("/templates")
    @Transactional
    fun createEvent(
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Event> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.save(eventService, profile!!, true)
    }

    @PatchMapping("/templates/{id}")
    @Transactional
    fun patchEvent(
        @PathVariable id: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<*> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.update(eventService, id, profile!!)
    }

    @PostMapping("/templates/{id}/items")
    fun addItem(
        @PathVariable id: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.save(eventService, id, profile!!)
    }

    @PatchMapping("/templates/{id}/items/{itemId}")
    fun patchItem(
        @PathVariable id: String, @PathVariable itemId: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.update(eventService, id, itemId, profile!!)
    }

    @DeleteMapping("templates/{id}/items/{itemId}")
    fun deleteItem(
        @PathVariable id: String,
        @PathVariable itemId: String
    ): ResponseEntity<Any> {

        val event = this.eventRepository.findById(UUID.fromString(id)).get()
        if (event.info!!.id == UUID.fromString(itemId)) {
            return ResponseEntity.badRequest().body(Error(450, "err.first_item"))
        }

        val item = this.eventItemRepository.findById(UUID.fromString(itemId)).get()
        item.status = "D"
        this.eventItemRepository.save(item)

        return ResponseEntity.noContent().build()
    }

    @GetMapping(value = ["/templates/{id}/items"])
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
}