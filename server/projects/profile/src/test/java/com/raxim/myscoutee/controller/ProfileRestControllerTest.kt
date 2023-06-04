package com.raxim.myscoutee.profile.controller;

import com.raxim.myscoutee.common.config.RQLConfig
import com.raxim.myscoutee.common.config.WebSecurityConfig
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.*

@Disabled
@WebMvcTest(
    excludeAutoConfiguration = [RQLConfig::class, WebSecurityConfig::class],
    controllers = [ImagesRestController::class]
)
class ProfileControllerTest(@Autowired val mvc: MockMvc) {

    @Test
    @Throws(Exception::class)
    fun shouldUploadFile() {

        val canvasFile = let {
            val imgCanvas = Paths.get("src/test/resources/file", "img.canvas");
            val fs = FileInputStream(imgCanvas.toFile());
            MockMultipartFile(
                "file", imgCanvas.fileName.toString(),
                "multipart/form-data", fs
            )
        };

        val profileId = UUID.randomUUID()

        val result = mvc.perform(
            multipart("/images")
                .file(canvasFile)
        ).andExpect(status().isCreated).andReturn()

        /*val regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}".toRegex(RegexOption.MULTILINE);

        val res = regex.find(result.response.contentAsString);

        val requestJson = "{\"images\":[\"${res!!.value}\"]}"

        mvc.perform(
            post("/profiles").contentType(APPLICATION_JSON_VALUE)
                .content(requestJson)
        ).andExpect(status().isCreated);*/
    }
}