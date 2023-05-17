package com.orbital.cee.view.authentication

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.cee.core.Constants.DB_REF_USER
import com.orbital.cee.data.Event
import com.orbital.cee.data.repository.DSRepositoryImpl
import com.orbital.cee.data.repository.DataStoreRepository
import com.orbital.cee.data.repository.EligibilityUserException
import com.orbital.cee.data.repository.UserStatistics
import com.orbital.cee.model.Response
import com.orbital.cee.model.Response.Success
import com.orbital.cee.model.ResponseDto
import com.orbital.cee.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.HashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repo: AuthRepository,
    val oneTapClient: SignInClient,
    private val dataStoreRepository: DataStoreRepository,
    private val ds : DSRepositoryImpl
) : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val isUserAuthenticated get() = repo.isUserAuthenticatedInFirebase
    val displayName get() = repo.displayName
    val photoUrl get() = repo.photoUrl
    val isUserLogging = mutableStateOf(false)
    init {
        if (auth.currentUser != null){
            isUserLogging.value = true
            //phoneNumber.value = auth.currentUser?.phoneNumber.toString()
            //checkingUserInformation()
        }
    }
    var oneTapSignInResponse by mutableStateOf<Response<BeginSignInResult>>(Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<Response<Boolean>>(Success(null))
        private set
    var createUserResponse by mutableStateOf<Response<Boolean>>(Success(null))
        private set
    var signOutResponse by mutableStateOf<Response<Boolean>>(Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<Response<Boolean>>(Success(false))
        private set

    var userCre = dataStoreRepository.userCredential.asLiveData()
    var userPhone = dataStoreRepository.userPhone.asLiveData()
    var userCCode = dataStoreRepository.userC.asLiveData()

    fun oneTapSignIn() = viewModelScope.launch {
        repo.oneTapSignInWithGoogle().collect { response ->
            oneTapSignInResponse = response
        }
    }
    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        repo.firebaseSignInWithGoogle(googleCredential).collect { response ->
            signInWithGoogleResponse = response
        }
    }
    fun createUser() = viewModelScope.launch {
        repo.createUserInFirestore().collect { response ->
            createUserResponse = response
        }
    }
    fun signOut() = viewModelScope.launch {
        repo.signOut().collect { response ->
            signOutResponse = response
        }
    }
    fun revokeAccess() = viewModelScope.launch {
        repo.revokeAccess().collect { response ->
            revokeAccessResponse = response
        }
    }
    var loggedPhoneNumber = ""
    var countryCodee = ""
    val isOtpSent = mutableStateOf(false)

    val verifyOtp = mutableStateOf("")
    val responseMessage = mutableStateOf<Event<String>?>(null)

     fun sendOTP(countryCode: String,mobileNum: String, context: Context) : Flow<ResponseDto> {
        val activity = context as Activity
         return callbackFlow {
             if (!isUserLogging.value) {
                 val options = PhoneAuthOptions.newBuilder(auth)
                     .setPhoneNumber(countryCode+mobileNum)
                     .setActivity(activity)
                     .setTimeout(60L, TimeUnit.SECONDS)
                     .setCallbacks(object :
                         PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                         override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                             //handleException(customMessage = "Verification Completed")
                             trySend(ResponseDto(isSuccess = true,serverMessage = "Verification Completed"))
                         }
                         override fun onVerificationFailed(p0: FirebaseException) {
                             Log.d("ERROR-A01",p0.message.toString())
                             trySend(ResponseDto(isSuccess = false,serverMessage = "${p0.message}"))
                         }
                         override fun onCodeSent(
                             otp: String,
                             p1: PhoneAuthProvider.ForceResendingToken
                         ) {
                             super.onCodeSent(otp, p1)
                             verifyOtp.value = otp
                             isOtpSent.value = true
//                             loggedPhoneNumber = mobileNum
//                             countryCodee = countryCode
                             saveUserCredential(firstLaunch = otp, phone = mobileNum, cc = countryCode.substring(1))

                             //handleException(customMessage = "Otp Send Successfully")
                             trySend(ResponseDto(isSuccess = true,serverMessage = "one time password sent to $mobileNum Successfully."))
                         }
                     }).build()
                 PhoneAuthProvider.verifyPhoneNumber(options)
             }else{
                 trySend(ResponseDto(isSuccess = false,serverMessage = "Sorry an error occurred."))
             }
             awaitClose { close() }
         }
    }
    suspend fun isUserBanned():Boolean{
        auth.currentUser?.apply {
            return db.collection(DB_REF_USER).document(uid).get().await().get("isUserBanned") as Boolean
        }
        auth.signOut()
        return false
    }
    fun otpVerification(otp: String,otpCre : String) : Flow<ResponseDto> {
        val credential = PhoneAuthProvider.getCredential(otpCre, otp)
        return callbackFlow {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            trySend(ResponseDto(isSuccess = true, serverMessage = "Login successfully."))
                        } else {
                            trySend(ResponseDto(isSuccess = false, serverMessage = "${task.exception!!.message}"))
                        }
                    }
            } catch (e:Exception){
                trySend(ResponseDto(isSuccess = false, serverMessage = "${e.message}"))
            }

            awaitClose{close()}
        }
    }
    fun isAlreadyRegistered(phone:String,cc:String) : Flow<Boolean> {
         return callbackFlow {
             db.collection(DB_REF_USER)
                 .whereEqualTo("phoneNumber",phone)
                 .whereEqualTo("countryCode",cc)
                 .get()
                 .addOnSuccessListener { documents ->
                     trySend(documents.size() > 0)
                 }.addOnFailureListener {
                     handleException(customMessage = "Register Fail")
                     cancel("Register Fail",it)
                      false
                 }
             awaitClose{ close() }
         }
    }
    suspend fun register(phone:String,cc:String) : Flow<ResponseDto>{
        return callbackFlow {
        try {
            val id = auth.currentUser!!.uid
            val document = db.collection(DB_REF_USER).document(id)
            var user : HashMap<String, Any> = HashMap<String, Any>()
            user["countryCode"] = cc
            user["isUserBanned"] = false
            user["joiningTimeStamp"] = Timestamp.now()
            user["lastSeen"] = Timestamp.now()
            user["phoneNumber"] = phone
            user["userCoin"] = 0
            user["pushId"] = ""
            user["status"] = "online"
            user["subscriptionPlan"] = "Free"
            user["userAvatar"] = ""
            user["userLoginCount"] = 1
            user["userContribution"] = 0
            user["userId"] = auth.currentUser!!.uid
            user["provider"] = "Phone"
            user["userEmail"] = ""
            user["userGender"] = ""
            user["username"] = "";
                isAlreadyRegistered(phone,cc).collect{
                    if (!it){
                        document.set(user)
                            .addOnSuccessListener {
                                trySend(ResponseDto(isSuccess = true, serverMessage = "Successfully registered."))
                            }
                            .addOnFailureListener { it ->
                                trySend(ResponseDto(isSuccess = false, serverMessage = "Sorry, an error occurred:${it.message}"))
                            }
                    }else{
                        trySend(ResponseDto(isSuccess = false, serverMessage = "This phone number already used."))
                    }
                }
            } catch (e: Exception) {
            trySend(ResponseDto(isSuccess = false, serverMessage = "Sorry, an error occurred:${e.message}"))
            }
            awaitClose{ close()}
        }
    }
    fun singOut(){
        auth.signOut()
    }
    private fun handleException(exception : Exception? = null , customMessage: String = "") {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage : $errorMsg"
        responseMessage.value = Event(message)
    }
//    fun incrementLoginCount(): Flow<ResponseDto> {
//        val userId = auth.currentUser!!.uid
//        Log.d("UID: ",userId)
//        return callbackFlow {
//            db.collection("UsersDebug")
//                .whereEqualTo("userId",userId).get().addOnSuccessListener {
//                    if (it.size() > 0){
//                        var doc = it.documents.last()
//                        db.collection("UsersDebug").document(doc.id).update("userLoginCount",FieldValue.increment(1)).addOnSuccessListener {
//                            trySend(ResponseDto(isSuccess = true,""))
//                        }
//                            .addOnFailureListener{
//                                trySend(ResponseDto(isSuccess = false,it.message.toString()))
//                            }
//                    }else{
//                        trySend(ResponseDto(isSuccess = false,"Un error"))
//                    }
//                }
//            awaitClose{close()}
//        }
//    }
    fun saveUserCredential(firstLaunch: String,phone:String,cc:String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveCredential(otp = firstLaunch, phoneNumber = phone,cCode = cc)
        }

    suspend fun saveStatisticsFromFirestore(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserStatistics().collect{
                Log.d("DEBUG_STATISTICS_VM",it.maxSpeed.toString())
                ds.saveStatistics(uStatistics = it)
            }
        }
    }
    private suspend fun getUserStatistics(): Flow<UserStatistics> {
        return callbackFlow {
            try {
                val userStatistics = db.collection(DB_REF_USER).document(auth.currentUser?.uid!!).collection("statistic")
                    .document("GeneralStats").get().await()
                val totalAlerted = (userStatistics.get("alertedTime") as? Long? ?: 0).toInt()
                val totalDistance = (userStatistics.get("traveldDistance") as? Long? ?: 0).toFloat()
                val maxSpeed = (userStatistics.get("maxSpeed") as? Long? ?: 0).toInt()
                Log.d("DEBUG_STATISTICS_VM",maxSpeed.toString())
                trySend(
                    UserStatistics(
                    alertedCount = totalAlerted,
                    traveledDistance = totalDistance,
                    maxSpeed = maxSpeed
                )
                )
            }catch (e:Exception){
                Log.d("HOME_VIEW_MODEL_001",e.message.toString())
                throw e
            }
            awaitClose{close()}
        }
    }

}