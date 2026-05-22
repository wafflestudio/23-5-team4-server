package com.wafflestudio.spring2025.domain.user.controller

import com.wafflestudio.spring2025.domain.auth.AuthRequired
import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.user.dto.GetMeResponse
import com.wafflestudio.spring2025.domain.user.dto.PatchMeRequest
import com.wafflestudio.spring2025.domain.user.dto.PatchMeResponse
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@AuthRequired
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @AuthRequired
    @GetMapping("/me")
    fun me(
        @LoggedInUser user: User,
    ): ResponseEntity<GetMeResponse> = ResponseEntity.ok(userService.me(user))

    @AuthRequired
    @PatchMapping("/me")
    fun patchMe(
        @LoggedInUser user: User,
        @RequestBody request: PatchMeRequest,
    ): ResponseEntity<PatchMeResponse> {
        userService.patchMe(
            user = user,
            name = request.name,
            password = request.password,
            email = request.email,
            profileImage = request.profileImage,
        )
        return ResponseEntity.ok(userService.me(user))
    }

    @DeleteMapping("/me")
    fun deleteMe(
        @LoggedInUser user: User,
    ): ResponseEntity<Void> {
        userService.deleteMe(user = user)
        return ResponseEntity.noContent().build()
    }
}
