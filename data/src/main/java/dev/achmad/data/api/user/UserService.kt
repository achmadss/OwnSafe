package dev.achmad.data.api.user

import dev.achmad.data.api.user.urls.create.CreateShortURLRequestBody
import dev.achmad.data.api.user.urls.create.CreateShortURLResponseBody
import dev.achmad.data.api.user.urls.delete.DeleteShortURLRequestBody
import dev.achmad.data.entity.ShortURL
import dev.achmad.data.entity.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

private const val prefix = "user"

interface UserService {

    @GET(prefix)
    suspend fun getUser(): Response<User>

    @PATCH(prefix)
    suspend fun updateUser(
        @Body body: UpdateUserRequestBody
    ): Response<User>

    @GET("$prefix/urls")
    suspend fun getShortURLs(): Response<List<ShortURL>>

    @POST("shorten")
    suspend fun createShortURL(
        @Body body: CreateShortURLRequestBody,
    ): Response<CreateShortURLResponseBody>

    @DELETE("$prefix/urls")
    suspend fun deleteShortURL(
        @Body body: DeleteShortURLRequestBody,
    ): Response<ShortURL>

}