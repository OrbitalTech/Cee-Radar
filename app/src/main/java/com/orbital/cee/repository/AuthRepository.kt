package com.orbital.cee.repository

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.orbital.cee.data.repository.AppSetting
import com.orbital.cee.data.repository.UserActivityLog
import com.orbital.cee.data.repository.UserStatistics
import com.orbital.cee.model.Response
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserAuthenticatedInFirebase: Boolean
    val displayName: String
    val photoUrl: String
    val uid: String

    suspend fun oneTapSignInWithGoogle(): Flow<Response<BeginSignInResult>>

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): Flow<Response<Boolean>>

    suspend fun createUserInFirestore(): Flow<Response<Boolean>>

    suspend fun signOut(): Flow<Response<Boolean>>

    suspend fun revokeAccess(): Flow<Response<Boolean>>
}
interface Abstract {
    suspend fun saveStatistics(uStatistics : UserStatistics)
    suspend fun retrieveStatistics(): Flow<UserStatistics>
    suspend fun debugModeSave(isDebugMode : Boolean)
    suspend fun saveAppSetting(isEnable: Boolean?,time : Float?)
    suspend fun retrieveAppSetting(): Flow<AppSetting>
    suspend fun retrieveDebugMode(): Flow<Boolean>
    suspend fun retrieveUserActivityLog() : Flow<UserActivityLog>
}