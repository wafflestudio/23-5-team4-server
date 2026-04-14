package com.wafflestudio.spring2025.common.image.controller

import com.wafflestudio.spring2025.common.image.dto.ImageUploadResponse
import com.wafflestudio.spring2025.common.image.service.ImageService
import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService,
) {
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadImage(
        @LoggedInUser user: User?,
        @RequestPart("image") image: MultipartFile,
        @RequestParam(name = "prefix", required = false)
        prefix: String?,
    ): ResponseEntity<ImageUploadResponse> {
        val userId = user?.id
        val response = imageService.uploadImage(ownerId = userId, image = image, prefix = prefix)
        return ResponseEntity.ok(response)
    }
}
