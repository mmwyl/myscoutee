package com.raxim.myscoutee.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;

import com.raxim.myscoutee.common.AppTestConstants;
import com.raxim.myscoutee.common.config.JsonConfig;
import com.raxim.myscoutee.common.config.RepositoryConfig;
import com.raxim.myscoutee.common.repository.MongoDataLoaderTestExecutionListener;
import com.raxim.myscoutee.common.repository.TestData;
import com.raxim.myscoutee.profile.data.document.mongo.Group;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.User;
import com.raxim.myscoutee.profile.data.dto.rest.GroupDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;
import com.raxim.myscoutee.profile.repository.mongo.GroupRepository;
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository;
import com.raxim.myscoutee.profile.repository.mongo.UserRepository;

@DataMongoTest
@DirtiesContext
@Import({ JsonConfig.class, RepositoryConfig.class })
@TestPropertySource(properties = { "de.flapdoodle.mongodb.embedded.version=6.0.6",
        "logging.level.org.springframework.data.mongodb=DEBUG" })
@TestData({ "mongo/user/groups.json", "mongo/user/profiles.json", "mongo/user/users.json" })
@TestExecutionListeners(value = MongoDataLoaderTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class UserRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void testShouldAddProfile() {
        this.profileRepository.findById(AppTestConstants.UUID_PROFILE_OLIVER)
                .ifPresent(profile -> userRepository.addProfile(AppTestConstants.UUID_PROFILE_OLIVER, profile));

        List<User> users = this.userRepository.findAll();

        List<Profile> profiles = new ArrayList<Profile>(users.get(0).getProfiles());
        assertEquals(3, profiles.size());

        assertEquals(AppTestConstants.UUID_PROFILE_OLIVER, profiles.get(0).getId());
    }

    @Test
    public void shouldLoadUserByProfile() {
        Optional<User> optUser = this.userRepository.findUserByProfile(AppTestConstants.UUID_PROFILE_OLIVER);
        assertTrue(optUser.isPresent());

        List<UUID> profileIds = new ArrayList<>();
        profileIds.add(AppTestConstants.UUID_PROFILE_OLIVER);
    }

    @Test
    public void shouldFindProfilesByUser() {
        Optional<User> optUser = this.userRepository.findById(AppTestConstants.UUID_USER_OLIVER);
        assertTrue(optUser.isPresent());

        List<Profile> profiles = this.userRepository.findProfilesByUser(AppTestConstants.UUID_USER_OLIVER);
        assertEquals(3, profiles.size());
    }

    @Test
    public void shouldFindGroupsByUser() {
        Optional<User> optUser = this.userRepository.findUserByProfile(AppTestConstants.UUID_PROFILE_OLIVER);

        PageParam pageParam = new PageParam();

        String role = "A";
        LocalDate updatedDateFrom = LocalDate.now();
        String updatedDateFromF = updatedDateFrom.atStartOfDay(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Object[] tOffset = new Object[] { role, updatedDateFromF };

        pageParam.setId(AppTestConstants.UUID_PROFILE_OLIVER);
        pageParam.setOffset(tOffset);
        
        List<Group> groups = this.groupRepository.findAll();
        assertEquals(3, groups.size());
        
        List<GroupDTO> groupDTOs = this.userRepository.findGroupsByUser(optUser.get().getId(), pageParam);
        assertEquals(3, groupDTOs.size());
    }
}
