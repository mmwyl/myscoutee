package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.common.FileUtil
import com.raxim.myscoutee.common.save
import com.raxim.myscoutee.profile.data.dto.rest.Image
import org.bson.BsonBinary
import org.bson.BsonBinarySubType
import org.bson.UuidRepresentation
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletResponse

fun String.asUUID(): String {
    val bytes = Base64.getDecoder().decode(this)
    return BsonBinary(BsonBinarySubType.UUID_LEGACY, bytes).asUuid(UuidRepresentation.JAVA_LEGACY).toString()
}

@RepositoryRestController
@RequestMapping("images")
class ImagesRestController {

    @Throws(
        IOException::class
    )
    @GetMapping("/{name}")
    fun download(@PathVariable name: String, response: HttpServletResponse) {
        val (separator, tmpDir) = FileUtil.tempToPath(name)

        val file = File("$tmpDir${separator}_$name")
        FileUtil.copyFileToHttp(response, file)
    }

    // images under user uid folder
    @PostMapping(value = ["", "/{name}"])
    fun upload(
        @PathVariable name: String? = null,
        @RequestParam orig: MultipartFile? = null, @RequestParam edited: MultipartFile? = null
    ): ResponseEntity<Image> {

        val fileName: String = name ?: UUID.randomUUID().toString()
        try {
            val (separator, tmpDir) = FileUtil.tempToPath(fileName, true)

            if (edited != null && !edited.isEmpty) {
                val completeImageData = String(edited.bytes, Charset.forName("UTF-8"))
                val imageDataBytes = completeImageData.substring(completeImageData.indexOf(",") + 1)
                val imgStream: InputStream = ByteArrayInputStream(Base64.getMimeDecoder().decode(imageDataBytes))

                imgStream.save("$tmpDir${separator}_$fileName")
            }

            return if (orig != null && !orig.isEmpty) {
                val origIs = ByteArrayInputStream(orig!!.bytes)
                val origFile = "$tmpDir${separator}_${fileName}_orig"

                origIs.save(origFile)

                ResponseEntity(Image(fileName), HttpStatus.CREATED);
            } else {
                val (_, fullDir) = FileUtil.uuidToPath(fileName, true)
                val origFileName = "$fullDir${separator}_${fileName}_orig"
                val tmpOrigFileName = "$tmpDir${separator}_${fileName}_orig"

                val permOrigFile = File(origFileName)
                val tmpOrigFile = File(tmpOrigFileName)

                if (permOrigFile.exists() || tmpOrigFile.exists()) {
                    ResponseEntity(Image(fileName), HttpStatus.OK);
                } else {
                    ResponseEntity(HttpStatus.BAD_REQUEST)
                }
            }
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }
}