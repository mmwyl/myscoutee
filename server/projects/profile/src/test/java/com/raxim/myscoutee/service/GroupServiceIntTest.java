package com.raxim.myscoutee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.algo.AbstractAlgoTest;
import com.raxim.myscoutee.common.config.JsonConfig;
import com.raxim.myscoutee.common.config.RepositoryConfig;
import com.raxim.myscoutee.common.repository.MongoDataLoaderTestExecutionListener;
import com.raxim.myscoutee.common.repository.TestData;
import com.raxim.myscoutee.profile.data.document.mongo.Group;
import com.raxim.myscoutee.profile.data.document.mongo.Profile;
import com.raxim.myscoutee.profile.data.document.mongo.User;
import com.raxim.myscoutee.profile.data.dto.rest.GroupDTO;
import com.raxim.myscoutee.profile.repository.mongo.GroupRepository;
import com.raxim.myscoutee.profile.repository.mongo.ProfileRepository;
import com.raxim.myscoutee.profile.repository.mongo.UserRepository;
import com.raxim.myscoutee.profile.service.GroupService;

@DataMongoTest
@DirtiesContext
@Import({ RepositoryConfig.class, JsonConfig.class })
@TestPropertySource(properties = { "de.flapdoodle.mongodb.embedded.version=6.0.6",
        "logging.level.org.springframework.data.mongodb=DEBUG" })
@TestData({ "mongo/groups/groups.json", "mongo/groups/users.json", "mongo/groups/profiles.json" })
@TestExecutionListeners(value = MongoDataLoaderTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class GroupServiceIntTest extends AbstractAlgoTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    private GroupService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String GROUP_DESC_1 = "Changed Date";

    @Test
    public void shouldSaveGroup() throws IOException, CloneNotSupportedException {
        this.groupService = new GroupService(groupRepository, profileRepository, userRepository);

        Group[] groupArray = loadJson(this, "rest/groupsForGroupSave.json",
                                Group[].class,
                                objectMapper);

        List<Group> groups = groupRepository.findAll();
        assertEquals(1, groups.size());

        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());

        List<Profile> profiles = profileRepository.findAll();
        assertEquals(1, profiles.size());

        //save with separate profile
        Optional<GroupDTO> optGroup = this.groupService.saveGroup(groupArray[0], users.get(0));

        groups = groupRepository.findAll();
        assertEquals(2, groups.size());

        profiles = profileRepository.findAll();
        assertEquals(2, profiles.size());

        //save as sub group
        optGroup = this.groupService.saveGroup(groupArray[1], users.get(0));

        groups = groupRepository.findAll();
        assertEquals(3, groups.size());

        profiles = profileRepository.findAll();
        assertEquals(3, profiles.size());

        assertEquals(groups.get(0).getId(), optGroup.get().getGroup().getGroup());

        //change description of a group
        Group group1 = groups.get(1);
        group1.setDesc(GROUP_DESC_1);
        optGroup = this.groupService.saveGroup(group1, users.get(0));

        groups = groupRepository.findAll();
        assertEquals(3, groups.size());

        profiles = profileRepository.findAll();
        assertEquals(3, profiles.size());

        assertEquals(groups.get(1).getDesc(), GROUP_DESC_1);
    }
}
