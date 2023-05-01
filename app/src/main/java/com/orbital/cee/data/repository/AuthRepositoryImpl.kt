package com.orbital.cee.data.repository

import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.cee.core.Constants.CREATED_AT
import com.orbital.cee.core.Constants.DB_REF_USER
import com.orbital.cee.core.Constants.DISPLAY_NAME
import com.orbital.cee.core.Constants.EMAIL
import com.orbital.cee.core.Constants.NO_DISPLAY_NAME
import com.orbital.cee.core.Constants.PHOTO_URL
import com.orbital.cee.core.Constants.SIGN_IN_REQUEST
import com.orbital.cee.core.Constants.SIGN_UP_REQUEST
import com.orbital.cee.model.Response
import com.orbital.cee.repository.AuthRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl  @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private var signInClient: GoogleSignInClient,
    private val db: FirebaseFirestore
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null
    override val displayName = auth.currentUser?.displayName ?: NO_DISPLAY_NAME
    override val photoUrl = auth.currentUser?.photoUrl.toString()
    override val uid = auth.currentUser?.uid.toString()

    override suspend fun oneTapSignInWithGoogle() = flow {
        try {
            emit(Response.Loading)
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            emit(Response.Success(signInResult))
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                emit(Response.Success(signUpResult))
            } catch (e: Exception) {
                Log.d("ERRORRR",e.message.toString())
                emit(Response.Error(e))
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            auth.currentUser?.apply {
               val isUserBanned = db.collection(DB_REF_USER).document(uid).get().await().get("isUserBanned")

                if (isUserBanned == true){
                    emit(Response.Error(EligibilityUserException("current user banned!")))
                }else{
                    emit(Response.Success(isNewUser))
                }
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun createUserInFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                val user = toUser()
                db.collection(DB_REF_USER).document(uid).set(user).await()
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun signOut() = flow {
        try {
            emit(Response.Loading)
            oneTapClient.signOut().await()
            auth.signOut()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun revokeAccess() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(DB_REF_USER).document(uid).delete().await()
                delete().await()
                signInClient.revokeAccess().await()
                oneTapClient.signOut().await()
            }
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }
}

fun FirebaseUser.toUser() = mapOf(
    "provider" to "Google",
    "isUserBanned" to false,
    "lastSeen" to serverTimestamp(),
    "phoneNumber" to "",
    "userCoin" to 0,
    "pushId" to "",
    "userId" to uid,
    "status" to "online",
    "subscriptionPlan" to "Free",
    "userLoginCount" to 1,
    "userContribution" to 0,
    "userGender" to "",
    DISPLAY_NAME to displayName,
    EMAIL to email,
    PHOTO_URL to photoUrl?.toString(),
    CREATED_AT to serverTimestamp()
)
class EligibilityUserException(message: String) : Exception(message)