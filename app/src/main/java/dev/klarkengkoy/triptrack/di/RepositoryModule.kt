package dev.klarkengkoy.triptrack.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.klarkengkoy.triptrack.data.repository.AuthRepository
import dev.klarkengkoy.triptrack.data.repository.AuthRepositoryImpl
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.data.repository.TripsRepositoryImpl
import dev.klarkengkoy.triptrack.ui.login.SignInProviderFactory
import dev.klarkengkoy.triptrack.ui.login.SignInProviderFactoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTripsRepository(
        tripsRepositoryImpl: TripsRepositoryImpl
    ): TripsRepository

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindSignInProviderFactory(
        signInProviderFactoryImpl: SignInProviderFactoryImpl
    ): SignInProviderFactory
}
