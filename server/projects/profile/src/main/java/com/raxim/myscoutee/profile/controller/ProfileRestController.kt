package com.raxim.myscoutee.profile.controller

import com.mongodb.client.model.geojson.Point
import com.raxim.myscoutee.common.config.firebase.FirebasePrincipal
import com.raxim.myscoutee.profile.data.document.mongo.*
import com.raxim.myscoutee.profile.data.dto.rest.Like
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository
import com.raxim.myscoutee.profile.repository.mongo.UserRepository
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.*

fun Profile.score(): Int {
    var result = 0
    result = 31 * result + (birthday?.hashCode() ?: 0)
    result = 31 * result + (marital?.hashCode() ?: 0)
    result = 31 * result + (height ?: 0)
    result = 31 * result + (smoker?.hashCode() ?: 0)
    result = 31 * result + (hasChild?.hashCode() ?: 0)
    schools?.forEach { school ->
        result = 31 * result + (school.hashCode() ?: 0)
    }
    result = 31 * result + (physique?.hashCode() ?: 0)
    result = 31 * result + (languages?.hashCode() ?: 0)
    result = 31 * result + (religion?.hashCode() ?: 0)
    result = 31 * result + (profession?.hashCode() ?: 0)
    result = 31 * result + (voice?.hashCode() ?: 0)
    return result
}


@RepositoryRestController
@RequestMapping("profiles")
class ProfileRestController(
    val profileRepository: ProfileRepository,
    val likeRepository: LikeRepository,
    val userRepository: UserRepository
) {

    companion object {

        const val WOMAN = "w"
        const val MAN = "m"
        fun currUser(principal: Principal, currUserRes: Optional<Profile>): Triple<UUID, String, Point> {
            val userUid = UUID.fromString(principal.name)
            val gender: String
            val loc: Point
            if (currUserRes.isPresent) {
                val currUser = currUserRes.get()
                gender = if (currUser.gender == WOMAN) {
                    MAN
                } else {
                    WOMAN
                }
                loc = currUser.position!!.point()
            } else {
                throw SecurityException()
            }
            return Triple(userUid, gender, loc)
        }

        object DistanceUtil {
            private const val EARTH_RADIUS = 6370986.0 //meters
            fun haversine(a: GeoJsonPoint, b: GeoJsonPoint): Double {
                val dLat = Math.toRadians(b.x - a.x)
                val dLon = Math.toRadians(b.y - a.y)
                val lat1 = Math.toRadians(a.x)
                val lat2 = Math.toRadians(b.x)
                val dist =
                    sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(
                        lat2
                    )
                val c = 2 * asin(sqrt(dist))
                return EARTH_RADIUS * c
            }

            fun vincenty(a: GeoJsonPoint, b: GeoJsonPoint): Double {
                val dLat = Math.toRadians(b.x - a.x)
                val dLon = Math.toRadians(b.y - a.y)
                val lat1 = Math.toRadians(a.x)
                val lat2 = Math.toRadians(b.x)
                val dist =
                    sin(dLat / 2) * sin(dLat / 2) + cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(
                        dLon / 2
                    )
                val c = 2 * atan2(sqrt(dist), sqrt(1 - dist))
                return EARTH_RADIUS * c
            }
        }

    }

    @GetMapping("/{id}")
    fun getProfile(@PathVariable id: String): ResponseEntity<com.raxim.myscoutee.profile.data.dto.rest.Profile> {
        val profile = this.profileRepository.findById(UUID.fromString(id))
        return if (profile.isPresent) {
            val profileDto = com.raxim.myscoutee.profile.data.dto.rest.Profile(profile.get())
            ResponseEntity.ok(profileDto)
        } else {
            ResponseEntity.notFound().build<com.raxim.myscoutee.profile.data.dto.rest.Profile>()
        }
    }

    // data generation
    // kafka, rabbit config - batch import to neo4j from kafka
    // batch rate, simple rate, double rate
    @PostMapping(value = ["/like"])
    @Transactional
    fun like(
        @RequestBody pfLikes: List<Like>, auth: Authentication
    ): ResponseEntity<List<Like>> {

        val froms: MutableSet<UUID> = mutableSetOf()
        val tos: MutableSet<UUID> = mutableSetOf()
        val all: MutableSet<UUID> = mutableSetOf()

        val profile = (auth.principal as FirebasePrincipal).user.profile!!

        val pLikes = pfLikes.map { like ->
            val from: UUID = if (like.from !== null) {
                UUID.fromString(like.from)
            } else {
                // at simple rate - Principal is the from
                profile.id!!
            }

            val to: UUID = UUID.fromString(like.to)

            froms.add(from)
            tos.add(to)

            Like(from.toString(), to.toString(), like.rate, like.ref)
        }

        all.addAll(froms)
        all.addAll(tos)
        all.add(profile.id!!)

        val profiles = profileRepository.findAllById(all)

        val createdBy = profiles.first { fProfile -> fProfile.id == profile.id }

        // ?? -- bug
        val dbLikes = this.likeRepository.findByIds(profile.id, froms, tos)

        val likesToSave: MutableList<com.raxim.myscoutee.profile.data.document.mongo.Like> = mutableListOf()

        pLikes.forEach { like ->
            var mLike =
                dbLikes.firstOrNull { dbLike ->
                    like.from?.uuid() == dbLike.from?.id && like.to?.uuid() == dbLike.to?.id && dbLike.ref == like.ref?.uuid()
                }
            if (mLike != null) {
                mLike.rate = like.rate
                mLike.distance = calc(mLike.from!!, mLike.to!!)
            } else {
                val from: UUID = if (like.from !== null) {
                    UUID.fromString(like.from)
                } else {
                    // at simple rate - Principal is the from
                    profile.id
                }

                val to: UUID = UUID.fromString(like.to)

                var profileFrom =
                    profiles.firstOrNull { fProfile -> fProfile.id == from }
                var profileTo = profiles.firstOrNull { fProfile -> fProfile.id == to }

                //dirty fix, to make the EventScheduler query optimized
                if (profileFrom!!.gender == WOMAN) {
                    val tProfileFrom = profileFrom.copy()
                    val tProfileTo = profileTo!!.copy()
                    profileFrom = tProfileTo
                    profileTo = tProfileFrom
                }

                val isDouble = from != profile.id

                mLike = Like(
                    UUID.randomUUID(),
                    isDouble,
                    profileFrom,
                    profileTo,
                    like.rate,
                    createdBy,
                    Date(),
                    //distance calculation
                    calc(profileFrom, profileTo!!),
                    like.ref?.uuid()
                )

            }
            likesToSave.add(mLike)
        }

        val likesSaved = this.likeRepository.saveAll(likesToSave)

        val likesAll = likesSaved.map { like ->
            val from = if (like.from != null) {
                like.from?.id.toString()
            } else {
                null
            }
            Like(from, like.to?.id.toString(), like.rate)
        }

        return ResponseEntity(likesAll, HttpStatus.OK)
    }

    fun calc(profile1: Profile, profile2: Profile): Long {
        val locDist = DistanceUtil.haversine(profile1.position!!, profile2.position!!)

        var ageDist = 0.0
        if (profile1.birthday != null && profile2.birthday != null) {
            val profile1YM = YearMonth.from(
                profile1.birthday.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            )

            val profile2YM = YearMonth.from(
                profile2.birthday.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            )

            ageDist = abs(
                ChronoUnit.MONTHS.between(
                    profile1YM, profile2YM
                )
            ).toDouble().pow(3.0)
        }

        var childDist = 0
        if (profile1.hasChild != null && profile2.hasChild != null) {
            childDist = 5 * if (profile1.hasChild == profile2.hasChild) 0 else 1
        }

        var heightDist = 0.0
        if (profile1.height != null && profile2.height != null) {
            heightDist = abs(profile1.height - profile2.height).toDouble().pow(2.0)
        }

        var maritalDist = 0
        if (profile1.marital != null && profile2.marital != null) {
            maritalDist = 10 * if (profile1.marital == profile2.marital) 0 else 1
        }

        var physiqueDist = 0
        if (profile1.physique != null && profile2.physique != null) {
            physiqueDist = 2 * if (profile1.physique == profile2.physique) 0 else 1
        }

        var professionDist = 0
        if (profile1.profession != null && profile2.profession != null) {
            professionDist = 6 * if (profile1.profession == profile2.profession) 0 else 1
        }

        var religionDist = 0
        if (profile1.religion != null && profile2.religion != null) {
            religionDist = 3 * if (profile1.religion == profile2.religion) 0 else 1
        }

        var smokerDist = 0
        if (profile1.smoker != null && profile2.smoker != null) {
            smokerDist = 15 * if (profile1.smoker == profile2.smoker) 0 else 1
        }

        var voiceDist = 0
        if (profile1.smoker != null && profile2.smoker != null) {
            voiceDist = 5 * if (profile1.voice == profile2.voice) 0 else 1
        }

        var langDist = 0
        if (profile1.languages != null && profile2.languages != null) {
            langDist = profile1.languages.intersect(profile2.languages).size * 30
        }

        var schoolDist = 0
        if (profile1.schools != null && profile2.schools != null) {
            val schoolNames1 = profile1.schools.map { school -> school.name }
            val schoolNames2 = profile2.schools.map { school -> school.name }
            schoolDist = schoolNames1.intersect(schoolNames2).size * 10
        }

        val dist =
            locDist + ageDist + childDist + heightDist + maritalDist + physiqueDist + professionDist +
                    religionDist + smokerDist + voiceDist + langDist + schoolDist

        return dist.toLong()
    }
}