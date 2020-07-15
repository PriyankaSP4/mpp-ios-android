package com.jetbrains.handson.mpp.mobile.api

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
data class FaresResponse(
    val outboundJourneys: List<JourneyOption>
)

@Serializable
data class JourneyOption(
    val journeyOptionToken: String,
    val journeyId: String,
    val originStation: Station,
    val destinationStation: Station,
    val departureTime: String,
    val arrivalTime: String,
    val journeyDurationInMinutes: Int
)

@Serializable
data class Station(
    val displayName: String,
    val crs: String,
    val nlc: String
)

@ImplicitReflectionSerializer
@OptIn(UnstableDefault::class)
suspend fun HttpClient.getFares(
    originCode: String,
    destinationCode: String
): FaresResponse {


    val json = Json(JsonConfiguration(ignoreUnknownKeys = true, isLenient = true))

    // Get the content of an URL.
    val response: FaresResponse =
        get<FaresResponse>("https://mobile-api-dev.lner.co.uk/v1/fares?originStation=$originCode&destinationStation=$destinationCode&outboundDateTime=2020-07-15T12%3A16%3A27.371%2B00%3A00&inboundDateTime=2020-03-06T12%3A16%3A27.371%2B00%3A00&numberOfChildren=1&numberOfAdults=0&doSplitTicketing=false")

    return response
}