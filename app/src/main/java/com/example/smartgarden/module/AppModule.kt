package com.example.smartgarden.module

import android.content.Context
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticationImpl
import com.example.smartgarden.firebase.authentication.FirebaseAuthenticator
import com.example.smartgarden.firebase.authentication.FirebaseFirestoreImpl
import com.example.smartgarden.firebase.authentication.FirebaseFirestoreInterface
import com.example.smartgarden.firebase.authentication.FirebaseRealTimeDatabase
import com.example.smartgarden.firebase.authentication.FirebaseRealTimeDatabaseImpl
import com.example.smartgarden.manager.SharedPreferenceManager
import com.example.smartgarden.repository.DataInternalRepository
import com.example.smartgarden.viewmodels.LoginViewModel
import com.example.smartgarden.viewmodels.MainViewModel
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
    fun provideMainViewModel(
        auth : FirebaseAuthenticator,
        firestore : FirebaseFirestoreImpl,
        database : FirebaseRealTimeDatabase,
        dataInternalRepository: DataInternalRepository
    ) : MainViewModel {
        return MainViewModel(auth, firestore, database, dataInternalRepository)
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
    fun ProvidedataInternalRepository(
        shared : SharedPreferenceManager
    ) : DataInternalRepository{
        return DataInternalRepository(shared)
    }

}