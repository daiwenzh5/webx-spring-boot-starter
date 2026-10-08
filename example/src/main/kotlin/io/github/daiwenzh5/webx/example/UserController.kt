package io.github.daiwenzh5.webx.example

import io.github.daiwenzh5.webx.annotation.RawResponse
import io.github.daiwenzh5.webx.api.BusinessException
import io.github.daiwenzh5.webx.api.ErrorCode
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

enum class UserError(override val code: Int, override val message: String) : ErrorCode {
    NOT_FOUND(4040, "User not found"),
    NAME_TAKEN(4090, "Username already taken"),
}

@RestController
@RequestMapping("/api/users")
class UserController {

    data class User(val id: Long, val name: String)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): User {
        if (id <= 0) throw BusinessException(UserError.NOT_FOUND)
        return User(id, "user-$id")
    }

    @PostMapping
    fun create(): User = User(System.nanoTime(), "created")

    /**
     * 即使 Method Override 开启，也不会被 DELETE 之外的 method 改写。
     * 通过 X-HTTP-Method-Override: DELETE 调用：
     *   curl -X POST -H "X-HTTP-Method-Override: DELETE" /api/users/42
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Map<String, Any> = mapOf("deleted" to id)

    @RawResponse
    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf("status" to "UP")
}
