package com.orbital.cee

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orbital.cee.data.repository.DataStoreRepository
import com.orbital.cee.view.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@RequiresApi(Build.VERSION_CODES.S)
val appModule = module {
    viewModel { HomeViewModel(
        auth = FirebaseAuth.getInstance(),
        db = FirebaseFirestore.getInstance(),
        storage = FirebaseStorage.getInstance(),
        dataStoreRepository = DataStoreRepository(androidApplication()),application = androidApplication()
        )}
    viewModel { ApplicationViewModel(androidApplication())}
}