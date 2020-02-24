package com.argus3000.emojigus

import io.ktor.application.Application
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

fun Application.container() {
    val alertProcessor = AlertProcessor()
    alertProcessor.start()
    val vehicleManager = VehicleManager(VehicleDataSources(), alertProcessor)
    vehicleManager.startMonitoring()
    kodein {
        bind<VehicleManager>() with singleton { vehicleManager }
        bind<AlertProcessor>() with singleton { alertProcessor }
    }
}