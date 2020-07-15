package com.jetbrains.handson.mpp.mobile

import com.jetbrains.handson.mpp.mobile.api.FaresResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.DefaultRequest.Feature.install
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.coroutines.CoroutineContext

import com.jetbrains.handson.mpp.mobile.api.*

class ApplicationPresenter : ApplicationContract.Presenter() {

    private val dispatchers = AppDispatchersImpl()
    private var view: ApplicationContract.View? = null
    private val job: Job = SupervisorJob()
    private val codeMap = mapOf<String, String>(
        "Harrow and Wealdstone" to "HRW",
        "Canley" to "CNL",
        "London Euston" to "EUS",
        "Coventry" to "COV",
        "Birmingham New Street" to "BHM"
    )
    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    override fun onViewTaken(view: ApplicationContract.View) {
        this.view = view
        view.setLabel("Get live train times")
    }

    @ImplicitReflectionSerializer
    @OptIn(UnstableDefault::class)

    override fun onButtonPressed(origin: String, destination: String) {
        val originCode = requireNotNull(codeMap[origin])
        val destinationCode = requireNotNull(codeMap[destination])

        launch {
            val response = client.getFares(originCode, destinationCode)
            view?.showData(response)
        }
    }


}
