package dev.achmad.data.api.user

import dev.achmad.core.network.await
import dev.achmad.data.api.user.urls.create.CreateShortURLRequestBody
import dev.achmad.data.api.user.urls.delete.DeleteShortURLRequestBody

class UserDataSource(
    private val userService: UserService,
) {
    suspend fun getUser() = await { userService.getUser() }
    suspend fun updateUser(body: UpdateUserRequestBody) = await { userService.updateUser(body) }
    suspend fun getShortURLs() = await { userService.getShortURLs() }
    suspend fun createShortURL(body: CreateShortURLRequestBody) = await { userService.createShortURL(body) }
    suspend fun deleteShortURL(body: DeleteShortURLRequestBody) = await { userService.deleteShortURL(body) }
}