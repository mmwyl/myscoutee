package com.raxim.myscoutee.profile.service;

import com.raxim.myscoutee.common.FileUtil;
import com.raxim.myscoutee.common.Pair;
import com.raxim.myscoutee.common.SaveUtil;
import com.raxim.myscoutee.profile.controller.ScoreCalculator;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.dto.rest.Car;
import com.raxim.myscoutee.profile.data.dto.rest.ProfileStatus;
import com.raxim.myscoutee.profile.data.dto.rest.School;
import com.raxim.myscoutee.profile.repository.mongo.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileEventHandler profileEventHandler;
    private final CarRepository carRepository;
    private final CarEventHandler carEventHandler;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public ProfileService(ProfileRepository profileRepository, ProfileEventHandler profileEventHandler,
                          CarRepository carRepository, CarEventHandler carEventHandler,
                          SchoolRepository schoolRepository, UserRepository userRepository,
                          RoleRepository roleRepository) {
        this.profileRepository = profileRepository;
        this.profileEventHandler = profileEventHandler;
        this.carRepository = carRepository;
        this.carEventHandler = carEventHandler;
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public com.raxim.myscoutee.profile.data.dto.rest.Profile saveProfileStatus(String profileId, ProfileStatus profileStatus) {
        return profileRepository.findById(UUID.fromString(profileId)).map(profile -> {
            Profile profileToSave = profile.withStatus(profileStatus.getStatus());
            Profile profileSaved = profileRepository.save(profileToSave);

            Optional<Role> role = roleRepository.findRoleByProfile(profileSaved.getId());

            Role firstRole = role.get().withRole(profileStatus.getRole());
            roleRepository.save(firstRole);

            return new com.raxim.myscoutee.profile.data.dto.rest.Profile(profileSaved);
        }).orElse(null);
    }

    public com.raxim.myscoutee.profile.data.dto.rest.Profile saveProfile(UUID userId, UUID profileId, UUID group,
                                                                         Profile profile, MultipartFile voice) {
        String fileName = UUID.randomUUID().toString();

        Pair<String,String> separatorPermDir = FileUtil.uuidToPath(fileName, true);
        String separator = separatorPermDir.getKey();
        String permDir = separatorPermDir.getValue();

        if (voice != null && !voice.isEmpty()) {
            InputStream voiceStream = new ByteArrayInputStream(voice.getBytes());
            FileUtil.save(voiceStream, permDir + separator + "_" + fileName + "_voice");
        }

        Profile profileToSave;
        if (profileId != null) {
            Profile profileResult = profileRepository.findById(profileId).get();
            Profile profileEntity = profileResult;
            profileToSave = profile.withId(profileEntity.getId())
                    .withCars(profileEntity.getCars())
                    .withSchools(profileEntity.getSchools())
                    .withVoice(fileName)
                    .withGroup(group);
        } else {
            profileToSave = profile.withId(UUID.randomUUID())
                    .withVoice(fileName)
                    .withGroup(group);
        }

        Profile profileSaved = profileRepository.save(profileToSave.withScore(ScoreCalculator.calculateScore(profileToSave)));

        userRepository.findById(userId).map(user -> {
            user.setProfile(profileSaved);
            user.getProfiles().add(profileSaved);
            userRepository.save(user);
            return null;
        });

        profileEventHandler.handleAfterCreate(profileSaved);
        return new com.raxim.myscoutee.profile.data.dto.rest.Profile(profileSaved);
    }

    public List<Car> getCars(UUID profileId, Integer step, Object[] tOffset) {
        return profileRepository.findCarsByProfile(profileId, 20, step != null ? step : 5, tOffset);
    }

    public Optional<com.raxim.myscoutee.profile.data.document.mongo.Car> getCarByProfile(UUID profileId, UUID carId) {
        return profileRepository.findCarByProfile(profileId, carId);
    }

    public List<School> getSchools(UUID profileId, Integer step, Object[] tOffset) {
        return profileRepository.findSchoolsByProfile(profileId, 20, step != null ? step : 5, tOffset);
    }

    public Optional<com.raxim.myscoutee.profile.data.document.mongo.School> getSchoolByProfile(UUID profileId, UUID schoolId) {
        return profileRepository.findSchoolByProfile(profileId, schoolId);
    }

    public Car addCar(UUID profileId, UUID carId, com.raxim.myscoutee.profile.data.document.mongo.Car newCar) {
        return profileRepository.findById(profileId).map(profile -> {
            com.raxim.myscoutee.profile.data.document.mongo.Car car;
            if (carId != null) {
                com.raxim.myscoutee.profile.data.document.mongo.Car dbCar = profile.getCars().stream()
                        .filter(c -> c.getId().equals(carId))
                        .findFirst().get();
                car = newCar.withId(dbCar.getId());
            } else {
                car = newCar;
            }

            com.raxim.myscoutee.profile.data.document.mongo.Car savedCar = carRepository.save(car);
            profile.getCars().add(savedCar);
            profileRepository.save(profile);
            carEventHandler.handleAfterCreate(savedCar);
            return new Car(savedCar);
        }).orElse(null);
    }

    public List<School> saveSchools(UUID profileId, List<com.raxim.myscoutee.profile.data.document.mongo.School> schools) {
        return profileRepository.findById(profileId).map(profile -> {
            for (com.raxim.myscoutee.profile.data.document.mongo.School school : schools) {
                com.raxim.myscoutee.profile.data.document.mongo.School savedSchool = schoolRepository.save(school);
                boolean hasSchool = profile.getSchools().stream()
                        .anyMatch(s -> s.getId().equals(savedSchool.getId()));
                if (!hasSchool) {
                    profile.getSchools().add(savedSchool);
                }
            }
            profileRepository.save(profile);
            return profile.getSchools().stream()
                    .map(School::new)
                    .toList();
        }).orElse(null);
    }
}
