package com.argus3000.emojigus

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class VehicleDataSources {
    private val numVehicles = 100_000

    val vehicleEventsSource = flow {
        while (true) {
            val smileysPerSecond = Random.nextFloat() * 100
            emit(VehicleEvent(randomVehicle(), smileysPerSecond))

            delayEvery(60_000)
        }
    }

    val vehicleUpgradesSource = flow {
        while (true) {
            val upgradeVersion = "${rand1k()}.${rand1k()}.${rand1k()}"
            emit(VehicleUpgrade(randomVehicle(), upgradeVersion))

            delayEvery(10_000)
        }
    }

    private fun rand1k() = Random.nextInt(1000)

    private fun randomVehicle() = Random.nextInt(numVehicles)

    private suspend fun delayEvery(maxMillis: Int) {
        val millis = Random.nextInt(maxMillis) + 1
        if (System.currentTimeMillis() % millis == 0L) {
            delay(1)
        }
    }
}