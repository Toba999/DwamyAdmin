package com.dev.dwamyadmin.di

import android.content.Context
import com.dev.dwamyadmin.data.repo.FireBaseRepoImpl
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.dev.dwamyadmin.utils.SharedPrefManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFireStorage(): FirebaseStorage = FirebaseStorage.getInstance()


    @Provides
    @Singleton
    fun provideSharedPrefManager(@ApplicationContext context: Context): SharedPrefManager =
        SharedPrefManager(context)

    @Provides
    @Singleton
    fun provideFireBaseRepo(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        sharedPrefManager: SharedPrefManager
    ): FireBaseRepo {
        return FireBaseRepoImpl(firestore, firebaseStorage, sharedPrefManager)
    }
}