package com.dev.dwamyadmin.di

import com.dev.dwamyadmin.data.repo.FireBaseRepoImpl
import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideMyRepository(database: FirebaseDatabase): FireBaseRepo = FireBaseRepoImpl(database)
}