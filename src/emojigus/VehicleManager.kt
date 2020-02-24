package com.argus3000.emojigus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

class VehicleManager(private val dataSources: VehicleDataSources, private val alertProcessor: AlertProcessor) {
    private var monitoredVehicles = mapOf<Int, VehicleMonitor>()
    private val mutex = Mutex()

    fun startMonitoring() = GlobalScope.launch {
        launch {
            dataSources.vehicleUpgradesSource.filter {
                !monitoredVehicles.containsKey(it.vehicleId)
            }.collect {
                addVehicle(it.vehicleId)
            }
        }
    }

    private suspend fun addVehicle(vehicleId: Int) {
        val vehicleMonitor = VehicleMonitor(vehicleId, dataSources, alertProcessor)
        monitorForTimeWindow(vehicleMonitor)
        mutex.withLock {
            monitoredVehicles = monitoredVehicles + Pair(vehicleId, vehicleMonitor)
        }
    }

    private suspend fun monitorForTimeWindow(vehicleMonitor: VehicleMonitor) = GlobalScope.launch {
        withTimeoutOrNull(10_000) {
            try {
                vehicleMonitor.startMonitoring()
            } finally {
                removeVehicle(vehicleMonitor.vehicleId)
            }
        }
    }

    private suspend fun removeVehicle(vehicleId: Int) {
        mutex.withLock {
            monitoredVehicles = monitoredVehicles - vehicleId
        }
    }

    fun numVehiclesMonitored() = monitoredVehicles.size

    suspend fun viewNumAlertsForVehicle(vehicleId: Int) = mutex.withLock {
        monitoredVehicles[vehicleId]?.numAlerts ?: 0
    }
}