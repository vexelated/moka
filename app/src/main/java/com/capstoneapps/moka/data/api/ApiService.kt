package com.capstoneapps.moka.data.api

import com.capstoneapps.moka.data.response.CreateTaskResponse
import com.capstoneapps.moka.data.response.DeleteTaskResponse
import com.capstoneapps.moka.data.response.GetTaskResponse
import com.capstoneapps.moka.data.response.LoginResponse
import com.capstoneapps.moka.data.response.SignUpResponse
import com.capstoneapps.moka.data.response.UpdateTaskResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("signup")
    suspend fun register(
     @Body body:RequestBody
    ): SignUpResponse
    @POST("login")
    suspend fun login(
        @Body body: RequestBody
    ): LoginResponse
    @POST("task")
    suspend fun createTask(
        @Body body: RequestBody
    ): CreateTaskResponse
    @GET("task/status/{status}")
    suspend fun getTasksByStatus(
        @Path("status") status: Boolean
    ): GetTaskResponse
    @DELETE("task/{idtask}")
    suspend fun deleteTask(
        @Path("idtask") idTask: String
    ): DeleteTaskResponse
    @PUT("task/{idtask}")
    suspend fun updateTask(
        @Path("idtask") idtask: String,
        @Body body: RequestBody
    ): UpdateTaskResponse
}


