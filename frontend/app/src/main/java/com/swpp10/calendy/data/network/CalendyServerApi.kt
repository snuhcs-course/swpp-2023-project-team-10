package com.swpp10.calendy.data.network

import retrofit2.http.*

interface CalendyServerApi {
    @Headers("Accept-Encoding: identity") // TODO: Test for Network Inspector Korean Encoding...
    @POST("manager/send")
    suspend fun sendMessageToServer(
        @Body
        body: MessageBody
    ): String

    @POST("manager/briefing")
    suspend fun sendBriefingRequestToServer(
        @Body
        body : BriefingBody
    ): String
}

data class MessageBody(
    val message: String,
    val time: String,
    val category: String,
    val todo: String,
    val schedule: String
)

data class BriefingBody(
    val plansString: String,
    val time: String
)
