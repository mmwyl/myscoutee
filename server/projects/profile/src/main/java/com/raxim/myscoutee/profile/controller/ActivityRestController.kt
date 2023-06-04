package com.raxim.myscoutee.profile.controller

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Notification
import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.document.mongo.EventItem
import com.raxim.myscoutee.profile.data.document.mongo.Feedback
import com.raxim.myscoutee.profile.data.document.mongo.Member
import com.raxim.myscoutee.profile.data.document.mongo.Profile
import com.raxim.myscoutee.profile.data.dto.rest.Code
import com.raxim.myscoutee.profile.data.dto.rest.Error
import com.raxim.myscoutee.profile.data.dto.rest.Event
import com.raxim.myscoutee.profile.data.dto.rest.Page
import com.raxim.myscoutee.profile.repository.mongo.*
import com.raxim.myscoutee.profile.service.EventService
import com.raxim.myscoutee.profile.service.ProfileService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

fun EventItem.transform(): com.raxim.myscoutee.profile.data.dto.rest.EventItem {
    return com.raxim.myscoutee.profile.data.dto.rest.EventItem(
        item = this,
        groupKey = this.type
    )
}

fun com.raxim.myscoutee.profile.data.document.mongo.Event.transform(): com.raxim.myscoutee.profile.data.dto.rest.Event {
    val eventStart = this.info?.range?.start
    val groupKey = eventStart?.toLocalDate()
    val sortKey = eventStart?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

    return com.raxim.myscoutee.profile.data.dto.rest.Event(
        event = this,
        groupKey = groupKey,
        sortKey = sortKey
    )
}

fun EventItem.save(
    eventService: EventService,
    profile: Profile,
    isTemplate: Boolean = false
): ResponseEntity<Event> {
    val status = if (isTemplate) "T" else "A"

    val event = eventService.getEvent(this, profile, status)
    return if (event.isPresent) {
        val savedEvent = eventService.saveEvent(event.get(), this)
        if (savedEvent.isPresent) {
            val eventDto = savedEvent.get().first.transform()
            ResponseEntity(eventDto, HttpStatus.CREATED)
        } else {
            ResponseEntity.badRequest().build()
        }
    } else {
        ResponseEntity.notFound().build()
    }
}

fun EventItem.update(
    eventService: EventService,
    id: String,
    profile: Profile
): ResponseEntity<*> {
    // update first item
    val event = eventService.getEvent(this, profile, "A", id.uuid(), true)
    return if (event.isPresent) {
        val savedEvent = eventService.saveEvent(event.get(), this)
        if (savedEvent.isPresent) {
            val eventDto = savedEvent.get().first.transform()
            ResponseEntity(eventDto, HttpStatus.CREATED)
        } else {
            ResponseEntity.badRequest().build()
        }
    } else {
        ResponseEntity.notFound().build<Event>()
    }
}

fun EventItem.update(
    eventService: EventService,
    id: String,
    itemId: String,
    profile: Profile
): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
    val event =
        eventService.getEvent(this, profile, "A", id.uuid(), true)
    return if (event.isPresent) {
        val savedEvent = eventService.saveEvent(event.get(), this)
        if (savedEvent.isPresent) {
            val eventDto = savedEvent.get().second.transform()
            ResponseEntity(eventDto, HttpStatus.CREATED)
        } else {
            ResponseEntity.badRequest().build()
        }
    } else {
        ResponseEntity.notFound().build()
    }
}

fun EventItem.save(
    eventService: EventService,
    id: String,
    profile: Profile
): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
    val event = eventService.getEvent(this, profile, "A", id.uuid())
    return if (event.isPresent) {
        val savedEvent = eventService.saveEvent(event.get(), this)
        if (savedEvent.isPresent) {
            val eventDto = savedEvent.get().second.transform()
            ResponseEntity(eventDto, HttpStatus.CREATED)
        } else {
            ResponseEntity.badRequest().build()
        }
    } else {
        ResponseEntity.badRequest().build()
    }
}

@RepositoryRestController
@RequestMapping("activity")
class ActivityRestController(
    val profileRepository: ProfileRepository,
    val eventRepository: EventRepository,
    val eventItemRepository: EventItemRepository,
    val feedbackRepository: FeedbackRepository,
    val settingRepository: SettingRepository,
    val eventService: EventService,
    val promotionRepository: PromotionRepository,
    private val tokenRepository: TokenRepository,
    private val groupRepository: GroupRepository,
    private val profileService: ProfileService,
    private val memberRepository: MemberRepository
) {
    @GetMapping("events")
    @Transactional
    fun getEvents(
        @RequestParam("step") pStep: String?,
        @RequestParam("direction") direction: Int? = 1,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val user = (auth.principal as FirebasePrincipal).user
        val profile = user.profile!!

        val setting = this.settingRepository.findSettingByProfileAndKey(profile.id!!, "/activity/events")

        var step = pStep
        if (setting != null) {
            val group = setting.items!!.filter { item -> item.name == "group" }.first()
            val key = (group.data as ArrayList<Int>)[0]
            step = setting.items[0].options!![key].value!!
        }

        val tOffset: Array<String> = if (offset != null && offset.size == 2) {
            if (step == "m") {
                val from = LocalDate.parse(offset[0].decode(), DateTimeFormatter.ISO_DATE_TIME).withDayOfMonth(1)
                    .atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)

                arrayOf(from, offset[1].decode())
            } else {
                arrayOf(offset[0].decode(), offset[1].decode())
            }
        } else {
            if (step == "m") {
                val from = LocalDate.now().withDayOfMonth(1)
                    .atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)
                arrayOf(from, "1900-01-01")
            } else {
                arrayOf(LocalDate.now().atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME), "1900-01-01")
            }
        }

        if (profile.position != null) {

            val events = this.eventService.getEvents(
                step,
                direction,
                tOffset,
                profileId = profile.id,
                status = arrayOf("A", "P", "C")
            )

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (events.isNotEmpty()) {
                events.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(events, lOffset, 0, step)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

    @PostMapping("events/{id}/recommend")
    fun clone(
        @PathVariable id: String,
        @RequestParam("step") step: Int?
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Event> {
        val eventDto = eventService.recommendEvent(UUID.fromString(id))

        return if (eventDto.isPresent) {
            ResponseEntity(eventDto.get(), HttpStatus.OK)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("events")
    @Transactional
    fun createEvent(
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Event> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.save(eventService, profile!!)
    }

    @PatchMapping("events/{id}")
    @Transactional
    fun patchEvent(
        @PathVariable id: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<*> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.update(eventService, id, profile!!)
    }

    @PostMapping("events/{id}/items")
    fun addItem(
        @PathVariable id: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.save(eventService, id, profile!!)
    }

    @PatchMapping("events/{id}/items/{itemId}")
    fun patchItem(
        @PathVariable id: String,
        @PathVariable itemId: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.update(eventService, id, itemId, profile!!)
    }

    //delete date align
    @DeleteMapping("events/{id}/items/{itemId}")
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

    @PostMapping("events/{id}/items/{itemId}/join")
    fun joinItem(
        @PathVariable id: String,
        @PathVariable itemId: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return itemStatus(itemId, "A", profile!!.id!!)
    }

    @PostMapping("events/{id}/items/{itemId}/wait")
    fun waitItem(
        @PathVariable id: String,
        @PathVariable itemId: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return itemStatus(itemId, "W", profile!!.id!!)
    }

    @PostMapping("events/{id}/items/{itemId}/leave")
    fun leaveItem(
        @PathVariable id: String,
        @PathVariable itemId: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return itemStatus(itemId, "L", profile!!.id!!)
    }

    @GetMapping(value = ["events/{id}/items", "invitations/{id}/items", "promotions/{id}/items"])
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

    //pageable feedback GET by eventId order by date - RQL!!!
    //rql equals on type = invitation/event
    //filter event by userId
    @PostMapping("events/{id}/feedbacks")
    @Transactional
    fun addFeedback(@PathVariable id: String, @RequestBody feedback: Feedback): ResponseEntity<Feedback> {
        return this.eventRepository.findById(UUID.fromString(id)).map { event ->
            {
                val savedFeedback = this.feedbackRepository.save(feedback)
                event.feedbacks.add(savedFeedback)
                this.eventRepository.save(event)

                ResponseEntity(savedFeedback, HttpStatus.CREATED)
            }
        }.orElse {
            ResponseEntity.notFound().build()
        }.invoke();
    }

    @PatchMapping("events/{id}/feedbacks/{feedbackId}")
    @Transactional
    fun patchFeedback(
        @PathVariable id: String,
        @PathVariable feedbackId: String,
        @RequestBody feedback: Feedback
    ): ResponseEntity<Feedback> {
        return this.eventRepository.findById(UUID.fromString(id)).map { event ->
            {
                val dbFeedback = event.feedbacks.first { feedback -> feedback.id == feedbackId.uuid() }
                val lFeedback = feedback.copy(id = dbFeedback.id)
                val savedFeedback = this.feedbackRepository.save(lFeedback)
                event.feedbacks.add(savedFeedback)
                this.eventRepository.save(event)

                ResponseEntity(savedFeedback, HttpStatus.CREATED)
            }
        }.orElse {
            ResponseEntity.notFound().build()
        }.invoke();
    }

    @GetMapping(value = ["events/{eventId}/items/{id}/members"])
    fun itemMembers(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Member>> {

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode(), offset[0].decode())
        } else {
            arrayOf("A", "1900-01-01")
        }

        val members = this.eventItemRepository.findMembersByItem(
            UUID.fromString(id), 20, step ?: 5,
            arrayOf("A", "I", "J"), tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (members.isNotEmpty()) {
            members.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(members, lOffset)
        )
    }

    @GetMapping(value = ["events/{id}/members", "invitations/{id}/members"])
    fun members(
        @PathVariable id: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?,
        auth: Authentication
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Member>> {
        val profile = (auth.principal as FirebasePrincipal).user.profile

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode(), offset[0].decode())
        } else {
            arrayOf("A", "1900-01-01")
        }

        val members = this.eventRepository.findMembersByEvent(
            UUID.fromString(id), 20, step ?: 5,
            profile!!.id!!, arrayOf("A", "I", "J"), tOffset
        )

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

        val lOffset = if (members.isNotEmpty()) {
            members.last().offset
        } else {
            tOffset.toList()
        }

        val lRole = if (members.isNotEmpty()) {
            members.last().role
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(members, lOffset, 1, null, role = lRole)
        )
    }

    @GetMapping(
        value = ["events/{eventId}/members/{id}/schools"]
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

    @GetMapping("events/{id}/code")
    fun code(
        @PathVariable id: String, auth: Authentication
    ): ResponseEntity<Code> {
        val eventUUId = UUID.fromString(id)

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        val code = this.eventRepository.findCodeByEvent(eventUUId, profile.id!!)

        return if (code.isPresent) {
            ResponseEntity(code.get(), HttpStatus.OK)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("events/{id}/verify")
    fun verify(
        @PathVariable id: String,
        @RequestBody code: String
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Member> {
        val member = this.eventRepository.findMemberByCode(UUID.fromString(id), code)

        return if (member.isPresent) {
            ResponseEntity(member.get(), HttpStatus.OK)
        } else {
            ResponseEntity.notFound().build()
        }

    }

    //?? maybe it's one function with switch and the leave is {action} param
    @PostMapping("events/{id}/leave")
    fun leave(@PathVariable id: String, auth: Authentication): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        return status(id, "L", profile.id!!)
    }

    @PostMapping(value = ["invitations/{id}/accept", "promotions/{id}/accept"])
    fun join(@PathVariable id: String, auth: Authentication): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        return status(id, "A", profile.id!!)
    }

    @PostMapping(value = ["invitations/{id}/wait"])
    fun wait(@PathVariable id: String, auth: Authentication): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        return status(id, "W", profile.id!!)
    }

    @PostMapping(value = ["invitations/{id}/reject", "promotions/{id}/reject"])
    fun reject(@PathVariable id: String, auth: Authentication): ResponseEntity<out Any> {
        val profile = (auth.principal as FirebasePrincipal).user.profile!!
        return status(id, "R", profile.id!!)
    }

    private fun itemStatus(itemId: String, status: String, profileUid: UUID): ResponseEntity<out Any> {
        val profileOp = this.profileRepository.findById(profileUid)
        if (profileOp.isPresent) {
            val profile = profileOp.get()
            return this.eventItemRepository.findById(UUID.fromString(itemId)).map { eventItem ->
                {
                    var action: String? = null

                    if (status == "A" && eventItem.num == eventItem.capacity!!.max) {
                        ResponseEntity.badRequest().body(Error(450, "err.event_full"))
                    } else {
                        if (eventItem.optional != null && eventItem.optional) {
                            if (status == "A") {
                                val isMember = eventItem.members!!.any { member -> member.id == profile.id }
                                if (!isMember) {
                                    eventItem.members!!.add(Member(profileUid, profile, status, "U"))
                                }
                                action = "joined"
                                eventItem.num++
                            } else if (status == "L") {
                                action = "left"
                                eventItem.num--
                            }

                            val eMembers = eventItem.members!!.map { member ->
                                if (member.id == profile.id) {
                                    member.status = status
                                }
                                member
                            }.toMutableSet()

                            eventItem.members = eMembers
                            val savedEventItem = this.eventItemRepository.save(eventItem)

                            if (action != null) {
                                val promoterIds = savedEventItem.members!!.filter { member -> member.role == "P" }
                                    .map { member -> member.profile.id!! }.toTypedArray()

                                if (promoterIds.isNotEmpty()) {
                                    val deviceKeys =
                                        this.tokenRepository.findByUserIds(promoterIds).map { token -> token.deviceKey }

                                    if (deviceKeys.isNotEmpty()) {
                                        val message: com.google.firebase.messaging.MulticastMessage =
                                            com.google.firebase.messaging.MulticastMessage.builder()
                                                .setNotification(
                                                    Notification.builder()
                                                        .setTitle("Member + $action")
                                                        .setBody("Member '${profile.firstName}' $action the " + eventItem.name + " event!")
                                                        .build()
                                                ).addAllTokens(deviceKeys)
                                                .build()

                                        val response = FirebaseMessaging.getInstance().sendMulticast(message)
                                        println("Successfully sent message: $response")
                                    }
                                }
                            }

                            ResponseEntity.noContent().build<Void>()
                        } else {
                            ResponseEntity.badRequest().build()
                        }
                    }
                }
            }.orElse {
                ResponseEntity.notFound().build()
            }.invoke();
        }
        return ResponseEntity.badRequest().build()
    }

    private fun status(id: String, status: String, profileUid: UUID): ResponseEntity<out Any> {
        val profileOp = this.profileRepository.findById(profileUid)
        if (profileOp.isPresent) {
            val profile = profileOp.get()
            return this.eventRepository.findById(UUID.fromString(id)).map { event ->
                {
                    var action: String? = null

                    if (status == "A" && event.info!!.num == event.info!!.capacity!!.max) {
                        ResponseEntity.badRequest().body(Error(450, "err.event_full"))
                    } else {
                        val isPromotion = event.info!!.members!!.any { member -> member.role == "P" }

                        val eMembers = event.info!!.members!!.map { member ->
                            if (member.id == profile.id) {
                                //promoter left - notification to all members
                                if (member.role == "P") {
                                    if (status == "A") {
                                        event.status = "A"
                                        action = "accepted"
                                        if (event.ref != null) {
                                            event.ref.cnt--
                                        }
                                    } else {
                                        event.status = "C"
                                        action = "cancelled"
                                    }
                                }

                                if (isPromotion) {
                                    //you are leaving when promoter has already accepted the event
                                    if (status == "L" && member.role != "P" && event.status == "A") {
                                        member.status = "LL"
                                    } else {
                                        if (status == "A") {
                                            event.info!!.num++;
                                        } else if (status == "L") {
                                            event.info!!.num--;
                                        }
                                        member.status = status
                                    }
                                } else {
                                    if (status == "A") {
                                        event.info!!.num++;
                                    } else if (status == "L") {
                                        event.info!!.num--;
                                    }
                                    member.status = status
                                }
                            }
                            member
                        }.toMutableSet()

                        if (status == "L" && (event.status == "A" || event.status == "P")) {
                            val hasMember =
                                event.info!!.members!!.firstOrNull { member -> member.status == "A" || member.status == "I" }
                            if (hasMember == null) {
                                event.status = "C"
                                action = "cancelled"
                            }
                        }

                        event.info!!.members = eMembers
                        this.memberRepository.saveAll(event.info!!.members!!.toList())
                        this.eventRepository.save(event)

                        //align promotion cnt with events
                        if (event.ref != null) {
                            this.promotionRepository.findPromotionByEvent(event.ref.id!!).map { promotion ->
                                promotion.cnt = promotion.events.sumBy { event ->
                                    event.cnt
                                }
                                this.promotionRepository.save(promotion)
                            }

                            if (action != null) {
                                val deviceKeys = this.eventRepository.findTokensByEvent(arrayOf(event.ref.id!!))
                                    .map { token -> token.deviceKey }

                                if (deviceKeys.isNotEmpty()) {
                                    val message: com.google.firebase.messaging.MulticastMessage =
                                        com.google.firebase.messaging.MulticastMessage.builder()
                                            .setNotification(
                                                Notification.builder()
                                                    .setTitle("Event + $action")
                                                    .setBody("Promoter $action the " + event.info!!.name + " event!")
                                                    .build()
                                            ).addAllTokens(deviceKeys)
                                            .build()

                                    val response = FirebaseMessaging.getInstance().sendMulticast(message)
                                    println("Successfully sent message: $response")
                                }
                            }
                        }

                        ResponseEntity.noContent().build<Void>()
                    }
                }
            }.orElse {
                ResponseEntity.notFound().build()
            }.invoke();
        }
        return ResponseEntity.badRequest().build()
    }

    // similar to like batch - profiles also has status param, no delete / suspend at the moment, only add

    // user with same principal (owner) can change his status
    @PostMapping("events/{id}/members")
    @Transactional
    fun addMember(
        @PathVariable id: String,
        @RequestBody profiles: List<String>
    ): ResponseEntity<List<com.raxim.myscoutee.profile.data.dto.rest.Member>> {
        return this.eventRepository.findById(UUID.fromString(id)).map { event ->
            {
                val uProfiles = profiles.map { key ->
                    UUID.fromString(key)
                }

                val pProfiles = this.profileRepository.findAllById(uProfiles)

                val tMembers = pProfiles.map { profile ->
                    val member = event.info!!.members!!.firstOrNull { member -> member.id == profile.id }

                    if (member == null) {
                        var code: String? = null
                        if (event.info?.ticket == true) {
                            code = UUID.randomUUID().toString()
                        }

                        Member(
                            id = profile.id,
                            profile = profile,
                            status = "I",
                            createdDate = Date(),
                            code = code,
                            role = "U"
                        )
                    } else {
                        member.copy(status = "I")
                    }
                }.toMutableSet()

                tMembers.addAll(event.info!!.members!!)
                event.info!!.members = tMembers

                this.eventRepository.save(event)

                // only the added members
                val membersDto = tMembers.map { member ->
                    com.raxim.myscoutee.profile.data.dto.rest.Member(
                        member = member,
                        offset = listOf(Date(), member.status!!)
                    )
                }

                ResponseEntity(membersDto, HttpStatus.CREATED)
            }
        }.orElse {
            ResponseEntity.notFound().build()
        }.invoke();
    }

    @GetMapping("invitations")
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

            val events = this.profileRepository.findInvitationByProfile(
                profile.id!!,
                profile.position!!.point(), 20, step ?: 5,
                profile.group!!, tOffset, 1.5
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

    @GetMapping("promotions")
    @Transactional
    fun getPromotions(
        @RequestParam("step") step: Int?,
        @RequestParam("direction") direction: Int? = 1,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val user = (auth.principal as FirebasePrincipal).user
        val profile = user.profile!!

        if (profile.position != null) {

            val tOffset: Array<Any>
            val group = this.groupRepository.findById(profile.group!!)
            val events = if (group.isPresent) {
                val tGroup = group.get()
                if (tGroup.type == "b") {
                    tOffset = if (offset != null && offset.size == 3) {
                        arrayOf(offset[0].decode(), offset[1].decode())
                    } else {
                        arrayOf("1900-01-01", "1900-01-01")
                    }

                    this.promotionRepository.findFullEventsByPromoter(profile.id!!, 20, step ?: 5, "%Y-%m-%d", tOffset)
                } else {
                    tOffset = if (offset != null && offset.size == 3) {
                        arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
                    } else {
                        arrayOf("1900-01-01", 10, "1900-01-01")
                    }

                    this.eventRepository.findEventsByRated(profile.id!!, 20, step ?: 5, "%Y-%m-%d", tOffset)
                }
            } else {
                tOffset = emptyArray()
                emptyList()
            }

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/

            val lOffset = if (events.isNotEmpty()) {
                events.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(events, lOffset, 0, step)
            )
        } else {
            return ResponseEntity.badRequest().body(Error(450, "err.no_profile"))
        }
    }

}