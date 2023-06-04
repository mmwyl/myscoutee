package com.raxim.myscoutee.profile.controller

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.raxim.myscoutee.common.config.ConfigProperties
import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.document.mongo.*
import com.raxim.myscoutee.profile.data.document.mongo.Car
import com.raxim.myscoutee.profile.data.document.mongo.EventItem
import com.raxim.myscoutee.profile.data.document.mongo.Group
import com.raxim.myscoutee.profile.data.document.mongo.Link
import com.raxim.myscoutee.profile.data.document.mongo.School
import com.raxim.myscoutee.profile.data.dto.rest.*
import com.raxim.myscoutee.profile.data.dto.rest.Profile
import com.raxim.myscoutee.profile.data.dto.rest.User
import com.raxim.myscoutee.profile.repository.mongo.*
import com.raxim.myscoutee.profile.service.EventService
import com.raxim.myscoutee.profile.service.ProfileService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@RepositoryRestController
@RequestMapping("user")
class UserRestController(
    val groupRepository: GroupRepository,
    val linkRepository: LinkRepository,
    val profileService: ProfileService,
    val profileRepository: ProfileRepository,
    val roleRepository: RoleRepository,
    val userRepository: UserRepository,
    val likeRepository: LikeRepository,
    private val settingRepository: SettingRepository,
    private val formRepository: FormRepository,
    private val config: ConfigProperties,
    private val eventService: EventService,
    private val eventRepository: EventRepository,
    private val carRepository: CarRepository,
    private val schoolRepository: SchoolRepository
) {

    @GetMapping("/profile")
    fun getProfile(auth: Authentication): ResponseEntity<Profile> {
        val profile = (auth.principal as FirebasePrincipal).user.profile

        return if (profile != null) {
            val profileDto = Profile(profile)
            ResponseEntity.ok(profileDto)
        } else {
            // profile not exists
            val profileDto = Profile(com.raxim.myscoutee.profile.data.document.mongo.Profile())
            ResponseEntity.ok(profileDto)
        }
    }

    @PostMapping("/profile", consumes = ["multipart/form-data"])
    @Transactional
    fun saveProfile(
        auth: Authentication,
        @RequestPart profile: com.raxim.myscoutee.profile.data.document.mongo.Profile,
        @RequestPart voice: MultipartFile? = null
    ): ResponseEntity<Profile> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id
        val group = user.group!!

        return try {
            val profileDto = this.profileService.saveProfile(user.id!!, profileId, group, profile, voice)

            if (profileDto == null) {
                ResponseEntity.notFound().build<Profile>()
            } else {
                ResponseEntity.ok(profileDto)
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("")
    fun saveUser(
        auth: Authentication,
        @RequestPart group: Group
    ): ResponseEntity<User> {
        val user = (auth.principal as FirebasePrincipal).user

        var userToSave = this.userRepository.findUserByEmail(auth.name)

        val profile = userToSave!!.profiles!!.firstOrNull { profile -> profile.group == group.id }

        // user logged in to different profile
        if (profile!!.id != user.profile!!.id) {
            profile.lastLogin = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
            this.profileRepository.save(profile)
        }

        userToSave = userToSave.copy(group = group.id, profile = profile)
        val userSaved = this.userRepository.save(userToSave)

        val adminUser = (config.adminUser == auth.name)
        val groups = this.userRepository.findAllGroupsByEmail(auth.name).filter { group ->
            group.role == "ROLE_USER" || (adminUser && group.group!!.type == "b")
        }

        val likes = this.likeRepository.newLikesByProfile(
            profile!!.id!!, profile!!.lastLogin!!.format(DateTimeFormatter.ISO_DATE_TIME)
        )

        return ResponseEntity.ok(User(userSaved, groups, likes))
    }

    @GetMapping("/groups/{groupId}/events")
    @Transactional
    fun getEvents(
        @PathVariable groupId: String,
        @RequestParam("step") step: String?,
        @RequestParam("offset") offset: Array<String>?, auth: Authentication
    ): ResponseEntity<Any> {

        val tOffset: Array<String> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        if (profile.position != null) {
            val events = this.eventService.getEventsByStatus(tOffset, UUID.fromString(groupId), "U")

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

    @PatchMapping("/groups/{groupId}/events/{id}")
    @Transactional
    fun patchEvent(
        @PathVariable id: String,
        @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<*> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.update(eventService, id, profile!!)
    }

    @PostMapping("/groups/{groupId}/events/{id}/items")
    fun addItem(
        @PathVariable id: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.save(eventService, id, profile!!)
    }

    @PatchMapping("/groups/{groupId}/events/{id}/items/{itemId}")
    fun patchItem(
        @PathVariable id: String, @PathVariable itemId: String, @RequestBody eventItem: EventItem,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.EventItem> {
        val profile = (auth.principal as FirebasePrincipal).user.profile
        return eventItem.update(eventService, id, itemId, profile!!)
    }

    @GetMapping(value = ["/groups/{groupId}/events/{id}/items"])
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

    @PostMapping("/groups/{id}/events/{eventId}/publish")
    fun publish(
        @PathVariable eventId: String,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Event> {
        var event = this.eventRepository.findById(UUID.fromString(eventId)).get()
        event.status = "R"
        event = this.eventRepository.save(event)

        val resEvent = com.raxim.myscoutee.profile.data.dto.rest.Event(event)
        return ResponseEntity.ok(resEvent)
    }

    @PostMapping("/groups/{id}/events/{eventId}/reject")
    fun reject(
        @PathVariable eventId: String,
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Event> {
        var event = this.eventRepository.findById(UUID.fromString(eventId)).get()
        event.status = "D"
        event = this.eventRepository.save(event)

        val resEvent = com.raxim.myscoutee.profile.data.dto.rest.Event(event)
        return ResponseEntity.ok(resEvent)
    }

    @PostMapping("/groups/{groupId}/leave")
    fun join(
        @PathVariable groupId: String, auth: Authentication
    ): ResponseEntity<User> {

        val user = (auth.principal as FirebasePrincipal).user
        val profile = user.profile!!

        val dbUser = this.userRepository.findById(user.id!!).get()

        val profilesToDel = dbUser.profiles!!.first { profile ->
            profile.group == UUID.fromString(groupId)
                    && (profile.status == "A" || profile.status == "F" || profile.status == "I")
        }
        profilesToDel.status = "D"
        this.profileRepository.save(profilesToDel)

        dbUser.profile =
            dbUser.profiles!!.first { profile ->
                profile.group != UUID.fromString(groupId)
                        && (profile.status == "A" || profile.status == "F" || profile.status == "I")
            }
        dbUser.group = dbUser.profile!!.group

        val userSaved = this.userRepository.save(dbUser)

        val adminUser = (config.adminUser == auth.name)
        val groups = this.userRepository.findAllGroupsByEmail(auth.name).filter { group ->
            group.role == "ROLE_USER" || (adminUser && group.group!!.type == "b")
        }

        val likes = this.likeRepository.newLikesByProfile(
            profile.id!!, profile!!.lastLogin!!.format(DateTimeFormatter.ISO_DATE_TIME)
        )

        return ResponseEntity.ok(User(userSaved, groups, likes))
    }

    @GetMapping("")
    fun getUser(auth: Authentication): ResponseEntity<User> {
        val user = (auth.principal as FirebasePrincipal).user
        val profile = user.profile!!
        val profileId = profile.id!!

        val adminUser = (config.adminUser == auth.name)
        val groups = this.userRepository.findAllGroupsByEmail(auth.name).filter { group ->
            group.role == "ROLE_USER" || (adminUser && group.group!!.type == "b")
        }

        profile.lastLogin = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)

        this.profileRepository.save(profile)

        val likes = this.likeRepository.newLikesByProfile(
            profileId, profile.lastLogin!!.format(DateTimeFormatter.ISO_DATE_TIME)
        )

        return ResponseEntity.ok(User(user, groups, likes))
    }

    @GetMapping("/settings")
    fun getSetting(
        auth: Authentication, @RequestParam("key") key: String
    ): ResponseEntity<Setting> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val setting = this.settingRepository.findSettingByProfileAndKey(profileId, key)

        return if (setting == null) {
            val form = this.formRepository.findFormByKey(key)
            if (form.isPresent) {
                val sForm = form.get()
                val settingToSave = Setting(UUID.randomUUID(), key, profileId, sForm.items)
                val settingSaved = this.settingRepository.save(settingToSave)
                ResponseEntity.ok(settingSaved)
            } else {
                return ResponseEntity.badRequest().build()
            }
        } else {
            ResponseEntity.ok(setting)
        }
    }

    @PostMapping("/settings")
    fun saveSetting(
        auth: Authentication, @RequestParam("key") key: String, @RequestBody setting: Setting
    ): ResponseEntity<Setting> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val dbSetting = this.settingRepository.findSettingByProfileAndKey(profileId, key)
        return if (dbSetting != null) {
            val settingToSave = dbSetting.copy(items = setting.items)
            val settingsSaved = this.settingRepository.save(settingToSave)
            ResponseEntity.ok(settingsSaved)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/cars")
    fun getCars(
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Car>> {

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
            val cars = this.profileService.getCars(profileId, step, tOffset)

            //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
            val lOffset = if (cars.isNotEmpty()) {
                cars.last().offset
            } else {
                tOffset.toList()
            }

            return ResponseEntity.ok(
                Page(cars, lOffset)
            )
        }

    }

    @GetMapping("/schools")
    fun getSchools(
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.School>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        val tOffset: Array<Any> = if (offset != null && offset.size == 3) {
            arrayOf(offset[0].decode(), offset[1].decode(), offset[2].decode())
        } else {
            arrayOf("a", "1900-01-01", "1900-01-01")
        }

        if (profileId == null) {
            return ResponseEntity.notFound().build()
        } else {
            val schools = this.profileService.getSchools(profileId, step, tOffset)

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
    }

    @PostMapping("/cars")
    @Transactional
    fun addCar(
        auth: Authentication,
        @RequestBody car: Car
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Car> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val carDto = this.profileService.addCar(profileId, null, car)
            ResponseEntity(carDto, HttpStatus.CREATED)
        }
    }

    @PatchMapping("/cars/{id}")
    @Transactional
    fun patchCar(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody car: Car
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Car> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val carDto = this.profileService.addCar(profileId, id.uuid(), car)
            ResponseEntity(carDto, HttpStatus.OK)
        }
    }

    @DeleteMapping("cars/{id}")
    fun deleteCar(
        auth: Authentication,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val car = this.profileService.getCarByProfile(profileId, UUID.fromString(id))
            if (car.isPresent) {
                val carT = car.get()
                carT.status = "D"
                this.carRepository.save(carT)
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        }
    }


    @PostMapping("/schools")
    @Transactional
    fun saveSchool(
        auth: Authentication,
        @RequestBody schools: List<School>
    ): ResponseEntity<List<com.raxim.myscoutee.profile.data.dto.rest.School>> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val schoolsDto = this.profileService.saveSchools(profileId, schools)
            ResponseEntity(schoolsDto, HttpStatus.CREATED)
        }
    }

    @PatchMapping("/schools/{id}")
    @Transactional
    fun patchSchool(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody schools: List<School>
    ): ResponseEntity<List<com.raxim.myscoutee.profile.data.dto.rest.School>> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val schoolsDto = this.profileService.saveSchools(profileId, schools)
            ResponseEntity(schoolsDto, HttpStatus.OK)
        }
    }

    @DeleteMapping("/schools/{id}")
    fun deleteSchool(
        auth: Authentication,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id

        return if (profileId == null) {
            ResponseEntity.notFound().build()
        } else {
            val school = this.profileService.getSchoolByProfile(profileId, UUID.fromString(id))
            if (school.isPresent) {
                val schoolT = school.get()
                schoolT.status = "D"
                this.schoolRepository.save(schoolT)
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        }
    }

    @PostMapping("/schools/parse")
    fun upload(
        auth: Authentication,
        @RequestParam file: MultipartFile? = null
    ): ResponseEntity<List<School>> {
        return if (file != null && !file.isEmpty) {
            val pdfReader = PdfReader(file.bytes)

            val schools = mutableListOf<School>()
            for (i in 1..pdfReader.numberOfPages) {
                val cnt = PdfTextExtractor.getTextFromPage(pdfReader, i)
                cnt.split("\n").map { line ->
                    try {
                        val matchResult = dateRegex.toRegex().find(line)
                        if (matchResult != null) {
                            val str = matchResult.groupValues[1]
                            val school = School(range = str.range())
                            schools.add(school)
                        }
                    } catch (ex: Exception) {
                        println(ex)
                    }
                }
            }

            ResponseEntity(schools, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/groups")
    fun saveGroup(
        auth: Authentication,
        @RequestBody group: Group
    ): ResponseEntity<Group> {
        val groupSaved = this.groupRepository.save(group)

        val profile = com.raxim.myscoutee.profile.data.document.mongo.Profile(group = groupSaved.id)
        val profileSaved = this.profileRepository.save(profile)

        val user = this.userRepository.findUserByEmail(auth.name)
        val authProfile = user!!.profile!!

        val groupProfile = groupSaved.copy(createdBy = authProfile.id, position = authProfile.position)
        val groupProfileSaved = this.groupRepository.save(groupProfile)

        user.profiles!!.add(profileSaved)
        this.userRepository.save(user)

        val roleToSave = Role(UUID.randomUUID(), profileSaved.id!!, "ROLE_ADMIN")
        this.roleRepository.save(roleToSave)

        return ResponseEntity.ok(groupProfileSaved)
    }

    @PatchMapping("/groups/{id}")
    @Transactional
    fun patchGroup(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody group: Group
    ): ResponseEntity<Group> {

        val groupSaved = this.groupRepository.findById(UUID.fromString(id)).map { groupOld ->
            val groupToSave =
                group.copy(id = groupOld.id, createdDate = groupOld.createdDate, createdBy = groupOld.createdBy)
            this.groupRepository.save(groupToSave)
        }

        return if (groupSaved.isPresent) {
            ResponseEntity.ok(groupSaved.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/profile/rewards")
    @Transactional
    fun getRewards(
        auth: Authentication
    ): ResponseEntity<List<Reward>> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val rewards = this.linkRepository.findRewards(profileId)

        return ResponseEntity.ok(rewards)
    }

    @GetMapping("/profile/share")
    @Transactional
    fun shareGroup(
        auth: Authentication
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Link> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val link = Link(key = UUID.randomUUID(), refId = user.group, type = "g", createdBy = profileId)

        val linkSaved = this.linkRepository.save(link)

        val group = this.groupRepository.findById(user.group!!)

        return if (group.isPresent) {
            val groupReq = group.get()
            val linkResp = com.raxim.myscoutee.profile.data.dto.rest.Link(
                linkSaved,
                LinkInfo("Please be invited for ${groupReq.name} group!", groupReq.desc)
            )
            ResponseEntity.ok(linkResp)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // type is a group link
    @GetMapping("/groups/{id}/share")
    @Transactional
    fun shareGroup(
        auth: Authentication,
        @PathVariable id: String
    ): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Link> {
        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!

        val link = Link(key = UUID.randomUUID(), refId = UUID.fromString(id), type = "g", createdBy = profileId)

        val linkSaved = this.linkRepository.save(link)

        val group = this.groupRepository.findById(UUID.fromString(id))

        return if (group.isPresent) {
            val groupReq = group.get()
            val linkResp = com.raxim.myscoutee.profile.data.dto.rest.Link(
                linkSaved,
                LinkInfo("Please be invited for ${groupReq.name} group!", groupReq.desc)
            )
            ResponseEntity.ok(linkResp)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    //suspend/activate account from a group or all group managed by a particular user
    @PatchMapping("/groups/{groupId}/profiles/{profileId}")
    @Transactional
    fun suspendProfile(
        auth: Authentication,
        @PathVariable groupId: String,
        @PathVariable profileId: String,
        @RequestBody profileStatus: ProfileStatus
    ): ResponseEntity<Void> {

        val profile = this.profileService.saveProfileStatus(profileId, profileStatus)
        return if (profile == null) {
            ResponseEntity.notFound().build<Void>()
        } else {
            ResponseEntity.ok().build<Void>()
        }
    }

    //list groups with system groups if Role_Admin has in admin group
    @GetMapping("/groups")
    fun getGroups(
        auth: Authentication,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Group>> {


        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile!!.id!!
        val group = this.groupRepository.findById(user.group!!)
        val isAdmin = group.get().type == "b"

        //groups which has been createdBy a business profile
        val groups =
            this.userRepository.findGroupsByEmail(auth.name, "ROLE_ADMIN", isAdmin, profileId, 20, step ?: 5, tOffset)

        //http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
        val lOffset = if (groups.isNotEmpty()) {
            groups.last().offset
        } else {
            tOffset.toList()
        }

        return ResponseEntity.ok(
            Page(groups, lOffset)
        )
    }

    @GetMapping("/groups/{groupId}/profiles")
    fun getProfilesByGroup(
        auth: Authentication,
        @PathVariable groupId: String,
        @RequestParam("step") step: Int?,
        @RequestParam("offset") offset: Array<String>?
    ): ResponseEntity<Page<com.raxim.myscoutee.profile.data.dto.rest.Profile>> {

        val user = (auth.principal as FirebasePrincipal).user
        val profileId = user.profile?.id!!

        val tOffset: Array<Any> = if (offset != null && offset.size == 1) {
            arrayOf(offset[0].decode())
        } else {
            arrayOf("1900-01-01")
        }

        val profiles = this.profileRepository.findProfilesByGroup(profileId, groupId.uuid()!!, 20, step ?: 5, tOffset)


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

}