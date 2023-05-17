package com.orbital.cee.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbital.cee.core.Constants.GEOFENCE_RADIUS_M
import com.orbital.cee.core.Constants.LANGUAGE_CODE
import com.orbital.cee.core.Constants.PREFERENCE_ADS_WATCH_TIME
import com.orbital.cee.core.Constants.PREFERENCE_ALERT_COUNT
import com.orbital.cee.core.Constants.PREFERENCE_CREDENTIAL
import com.orbital.cee.core.Constants.PREFERENCE_DISTANCE
import com.orbital.cee.core.Constants.PREFERENCE_FIRST_LAUNCH
import com.orbital.cee.core.Constants.PREFERENCE_MAX_SPEED
import com.orbital.cee.core.Constants.PREFERENCE_NAME
import com.orbital.cee.core.Constants.PREFERENCE_SOUND_STATE
import com.orbital.cee.core.Constants.PREFERENCE_TRIP_HISTORY
import com.orbital.cee.core.Constants.PREFERENCE_USER_TYPE
import com.orbital.cee.core.Constants.REPORT_COUNTER_PER_ONE_HOUR
import com.orbital.cee.core.Constants.SAVE_LAST_REPORT
import com.orbital.cee.model.Trip
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    object PreferenceKey {
        val firstLaunch = booleanPreferencesKey(PREFERENCE_FIRST_LAUNCH)
        val maxSpeed = intPreferencesKey(PREFERENCE_MAX_SPEED)
        val alertCount = intPreferencesKey(PREFERENCE_ALERT_COUNT)
        val distance = floatPreferencesKey(PREFERENCE_DISTANCE)
        val sound = intPreferencesKey(PREFERENCE_SOUND_STATE)
        val credential = stringPreferencesKey(PREFERENCE_CREDENTIAL)
        val tripHistory = stringPreferencesKey(PREFERENCE_TRIP_HISTORY)
        val saveUserType = intPreferencesKey(PREFERENCE_USER_TYPE)
        val lastAdsWatch = longPreferencesKey(PREFERENCE_ADS_WATCH_TIME)
        val languageCode = stringPreferencesKey(LANGUAGE_CODE)
        val geofenceRadius = intPreferencesKey(GEOFENCE_RADIUS_M)
        val reportCounterPerOneHour = intPreferencesKey(REPORT_COUNTER_PER_ONE_HOUR)
        val saveLastReport = longPreferencesKey(SAVE_LAST_REPORT)
        val phone = stringPreferencesKey("phoneNumber")
        val cCode = stringPreferencesKey("cCode")
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveMaxSpeed(maxSpeed: Int) {
        dataStore.edit { preference ->
            if (preference[PreferenceKey.maxSpeed] != null){
                if (preference[PreferenceKey.maxSpeed]!! < maxSpeed){
                    preference[PreferenceKey.maxSpeed] = maxSpeed
                }
            }else{
                preference[PreferenceKey.maxSpeed] = maxSpeed
            }
            //preference[PreferenceKey.maxSpeed] = maxSpeed
        }
    }
    suspend fun updateSoundPreferences(soundState: Int) {
        dataStore.edit { preference ->
            preference[PreferenceKey.sound] = soundState
        }
    }
    suspend fun saveTripHistory(tripList:ArrayList<Trip?> ) {
        val jsonList = Gson().toJson(tripList)
        dataStore.edit { preference ->
            preference[PreferenceKey.tripHistory] = jsonList
        }
    }
    suspend fun saveUserType(saveUserType:Int ) {
        dataStore.edit { preference ->
            preference[PreferenceKey.saveUserType] = saveUserType
        }
    }
    suspend fun saveTheTimeOfTheLastReport(time:Timestamp ) {
        dataStore.edit { preference ->
            preference[PreferenceKey.saveLastReport] = time.seconds
        }
    }
    suspend fun incrementReportCountPerOneHour(count:Int ) {
        dataStore.edit { preference ->
            preference[PreferenceKey.reportCounterPerOneHour] = count
        }
    }
    suspend fun saveGeoFenceRadius(radius:Int ) {
        dataStore.edit { preference ->
            preference[PreferenceKey.geofenceRadius] = radius
        }
    }
    suspend fun saveLanguageCode(langCode:String) {
        dataStore.edit { preference ->
            preference[PreferenceKey.languageCode] = langCode
        }
    }
    suspend fun addAlertCount(alertCount : Int) {
        dataStore.edit { preference ->
            preference[PreferenceKey.alertCount] = alertCount
        }
    }
    suspend fun saveAdsWatchTime(alertCount : Timestamp) {
        dataStore.edit { preference ->
            preference[PreferenceKey.lastAdsWatch] = alertCount.seconds
        }
    }
    suspend fun addDistance(distance : Float) {
        dataStore.edit { preference ->
            preference[PreferenceKey.distance] =
                if(preference[PreferenceKey.distance] == null){
                    distance
                }else{
                    preference[PreferenceKey.distance]!! + distance
                }
        }
    }

    suspend fun saveFirstLaunch(firstLaunch: Boolean) {
        dataStore.edit { preference ->
            preference[PreferenceKey.firstLaunch] = firstLaunch
        }
    }
    suspend fun resetStatistics() {
        dataStore.edit { preference ->
            preference[PreferenceKey.alertCount] = 0
            preference[PreferenceKey.distance] = 0f
            preference[PreferenceKey.maxSpeed] = 0
        }
    }
    suspend fun saveCredential(otp: String,phoneNumber:String,cCode:String) {
        dataStore.edit { preference ->
            preference[PreferenceKey.credential] = otp
        }
        dataStore.edit { preference ->
            preference[PreferenceKey.phone] = phoneNumber
        }
        dataStore.edit { preference ->
            preference[PreferenceKey.cCode] = cCode
        }
    }

    val readFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.firstLaunch] ?: true
            firstLaunch
        }


    val readMaxSpeed: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.maxSpeed] ?: 0
            firstLaunch
        }
    val readAlertsCount: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.alertCount] ?: 0
            firstLaunch
        }
    val reportCountPerOneHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.reportCounterPerOneHour] ?: 0
            firstLaunch
        }
    val readUserType: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.saveUserType] ?: 0
            firstLaunch
        }
    val readGeofenceRadius: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val rad = preference[PreferenceKey.geofenceRadius] ?: 200
            rad
        }
    val languageCode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.languageCode] ?: "en"
            firstLaunch
        }
    val readSoundStatus: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.sound] ?: 1
            firstLaunch
        }
    val readDistance: Flow<Float> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.distance] ?: 0f
            firstLaunch
        }
    val watchTime: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.lastAdsWatch] ?: 0
            firstLaunch
        }
    val loadTimeOfLastReport: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.saveLastReport] ?: 0L
            firstLaunch
        }

    val userCredential: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.credential] ?: ""
            firstLaunch
        }
    val userPhone: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.phone] ?: ""
            firstLaunch
        }
    val userC: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.cCode] ?: ""
            firstLaunch
        }
    val tripList: Flow<ArrayList<Trip?>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val json = preference[PreferenceKey.tripHistory]
            val tripType = object : TypeToken<ArrayList<Trip?>>() {}.type
            val list = Gson().fromJson<ArrayList<Trip?>>(json,tripType)
            val firstLaunch = list ?: arrayListOf<Trip?>(null)
            firstLaunch
        }

}