package com.example.smartgarden.module

import android.content.Context
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticationImpl
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.storage.FirebaseFirestoreImpl
import com.example.smartgarden.firebase.storage.FirebaseFirestoreInterface
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabase
import com.example.smartgarden.firebase.storage.FirebaseRealTimeDatabaseImpl
import com.example.smartgarden.manager.RaspberryConnectionManager
import com.example.smartgarden.manager.SharedPreferenceManager
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.viewmodels.LoginViewModel
import com.example.smartgarden.viewmodels.MainViewModel
import com.example.smartgarden.viewmodels.ThresholdViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthenticator(
        @ApplicationContext context : Context
    ) : FirebaseAuthenticator {
        return FirebaseAuthenticationImpl(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestoreInterface {
        return FirebaseFirestoreImpl()
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabase() : FirebaseRealTimeDatabase {
        return FirebaseRealTimeDatabaseImpl()
    }

    @Provides
    @Singleton
    fun provideLoginViewModel(
        auth : FirebaseAuthenticator,
        firestore : FirebaseFirestoreImpl,
        database : FirebaseRealTimeDatabase,
        dataInternalRepository: DataInternalRepository
    ) : LoginViewModel {
        return LoginViewModel(auth, firestore, database, dataInternalRepository)
    }

    @Provides
    @Singleton
    fun provideThresholdViewModel(
        database : FirebaseRealTimeDatabase,
        dataInternalRepository: DataInternalRepository
    ) : ThresholdViewModel {
        return ThresholdViewModel(database, dataInternalRepository)
    }

    @Provides
    @Singleton
    fun provideMainViewModel(
        @ApplicationContext context : Context,
        auth : FirebaseAuthenticator,
        firestore : FirebaseFirestoreImpl,
        database : FirebaseRealTimeDatabase,
        dataInternalRepository: DataInternalRepository,
        raspberryConnection : RaspberryConnectionManager
    ) : MainViewModel {
        return MainViewModel(context, auth, firestore, database, dataInternalRepository, raspberryConnection)
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceManager(
        @ApplicationContext context : Context
    ) : SharedPreferenceManager{
        return SharedPreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideRaspberryConnectionManager(
        @ApplicationContext context : Context
    ) : RaspberryConnectionManager {
        return RaspberryConnectionManager(context)
    }

    @Provides
    @Singleton
    fun ProvidedataInternalRepository(
        shared : SharedPreferenceManager
    ) : DataInternalRepository{
        return DataInternalRepository(shared)
    }

}