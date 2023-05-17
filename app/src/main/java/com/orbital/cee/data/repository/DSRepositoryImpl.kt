package com.orbital.cee.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orbital.cee.core.Constants
import com.orbital.cee.repository.Abstract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val DATASTORE_NAME = "CEE_STORE"
val Context.datastore : DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class DSRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) : Abstract{

    companion object{
        val ALERTED_COUNT = intPreferencesKey(Constants.PREFERENCE_ALERT_COUNT)
        val TRAVELED_DISTANCE = floatPreferencesKey(Constants.PREFERENCE_DISTANCE)
        val MAX_SPEED = intPreferencesKey(Constants.PREFERENCE_MAX_SPEED)
        val REPORT_COUNT_IN_THIS_HOUR = intPreferencesKey(Constants.REPORT_COUNTER_PER_ONE_HOUR)
        val SAVE_LAST_REPORT_TIME = longPreferencesKey(Constants.SAVE_LAST_REPORT)
        val DEBUG_MODE_PREF_KEY = booleanPreferencesKey(Constants.DEBUG_MODE_PREF_KEY)
    }

    override suspend fun saveStatistics(uStatistics : UserStatistics) {
        context.datastore.edit { data ->
            data[ALERTED_COUNT] = uStatistics.alertedCount
            data[TRAVELED_DISTANCE] = uStatistics.traveledDistance
            data[MAX_SPEED] = uStatistics.maxSpeed
        }
    }

    override suspend fun retrieveStatistics() = context.datastore.data.map {data ->
        UserStatistics(
            alertedCount = data[ALERTED_COUNT] ?: 0,
            traveledDistance = data[TRAVELED_DISTANCE] ?: 0f ,
            maxSpeed = data[MAX_SPEED] ?: 0,
        )
    }

    override suspend fun debugModeSave(isDebugMode: Boolean) {
        context.datastore.edit { data->
        data[DEBUG_MODE_PREF_KEY] = isDebugMode
        }
    }

    override suspend fun retrieveDebugMode() = context.datastore.data.map { data->
        data[DEBUG_MODE_PREF_KEY] ?: false
    }

    override suspend fun retrieveUserActivityLog() = context.datastore.data.map { log ->
        UserActivityLog(
            lastReportTime = log[SAVE_LAST_REPORT_TIME] ?: 0,
            reportCountInThisHour = log[REPORT_COUNT_IN_THIS_HOUR] ?: 0
        )
    }
}

data class UserStatistics(val alertedCount:Int, val traveledDistance : Float,val maxSpeed : Int)
data class UserActivityLog(val lastReportTime:Long, val reportCountInThisHour : Int)