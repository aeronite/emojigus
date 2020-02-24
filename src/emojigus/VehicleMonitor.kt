package com.argus3000.emojigus

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

class VehicleMonitor(
    val vehicleId: Int,
    private val dataSources: VehicleDataSources,
    private val alertProcessor: AlertProcessor
) {

    var numAlerts = 0
        private set

    suspend fun startMonitoring() {

        val upgradeFlow = dataSources.vehicleUpgradesSource.filter {
            it.vehicleId == vehicleId
        }

        dataSources.vehicleEventsSource.filter {
            it.vehicleId == vehicleId
        }.combine(upgradeFlow) { ev, up ->
            val analysis = analyzeVersion(up.softwareVersion)
            VehicleEventEnriched(ev.vehicleId, up.softwareVersion, ev.smilesPerSecond, analysis, ev.time)
        }.filter {
            eventIsAlert(it)
        }.collect {
            numAlerts++
            alertProcessor.registerAlert(it)
        }
    }

    private fun analyzeVersion(version: String): VersionAnalysis {
        val versionNum = abs(version.hashCode())
        val analysisIndex = versionNum % VersionAnalysis.values().size
        return VersionAnalysis.values()[analysisIndex]
    }

    private fun eventIsAlert(ev: VehicleEventEnriched) = when {
        ev.smilesPerSecond < 95 && ev.versionAnalysis in setOf(
            VersionAnalysis.terrible,
            VersionAnalysis.nasty,
            VersionAnalysis.scary
        ) -> true

        ev.smilesPerSecond < 90 && ev.versionAnalysis in setOf(
            VersionAnalysis.weird,
            VersionAnalysis.buggy
        ) -> true

        else -> false
    }
}