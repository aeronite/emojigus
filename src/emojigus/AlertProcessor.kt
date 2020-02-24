package com.argus3000.emojigus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.format.DateTimeFormatter

class AlertProcessor {
    private var alerts = listOf<VehicleEventEnriched>()
    private val eventsChannel = Channel<VehicleEventEnriched>(1000)
    private val mutex = Mutex()

    fun start() = GlobalScope.launch {
        while (true) {
            val ev = eventsChannel.receive()
            mutex.withLock {
                if (alerts.size >= 6) alerts = alerts - alerts[0]
                alerts = alerts + ev
            }
        }
    }

    suspend fun registerAlert(ev: VehicleEventEnriched) = eventsChannel.send(ev)

    suspend fun viewAlerts(num: Int) = mutex.withLock { if (alerts.size > num) alerts.subList(0, num) else alerts }

    suspend fun viewLatestAlertTime(): String = mutex.withLock {
        val maxTime = alerts.map { it.time }.max() ?: Instant.MIN
        return DateTimeFormatter.ISO_INSTANT.format(maxTime)
    }
}