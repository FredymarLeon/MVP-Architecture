package com.fredymarleon.mvparchitecture.mainModule.model

import com.fredymarleon.mvparchitecture.EventBus
import com.fredymarleon.mvparchitecture.SportEvent
import com.fredymarleon.mvparchitecture.getAdEventsInRealtime
import com.fredymarleon.mvparchitecture.getResultEventsInRealtime
import com.fredymarleon.mvparchitecture.someTime
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MainRepository {
    suspend fun getEvents() {
        val events = getResultEventsInRealtime()
        events.forEach { event ->
            delay(someTime().milliseconds)
           publishEventRepository(event)
        }
    }

    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        val response = if (result.isWarning) {
            SportEvent.ResultError(30, "Error al guardar.")
        } else {
            SportEvent.SaveEvent
        }
        publishEventRepository(response)
    }

    suspend fun registerAd() {
        val events = getAdEventsInRealtime()
        publishEventRepository(events.first())
    }

    suspend fun closedAd(){
        publishEventRepository(SportEvent.CloseAdEvent)
    }

    private suspend fun publishEventRepository(event: SportEvent) {
        EventBus.instance().publishEvent(event)
    }
}