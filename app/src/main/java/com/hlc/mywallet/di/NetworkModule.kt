package com.hlc.mywallet.di

import android.content.Context
import com.hlc.mywallet.BuildConfig
import com.hlc.mywallet.data.api.MainService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.hlc.mywallet.data.api.DepositService
import com.hlc.mywallet.data.api.HomeService
import com.hlc.mywallet.data.api.TeamService
import com.hlc.mywallet.data.api.UserService
import com.hlc.mywallet.data.api.WalletService
import com.hlc.mywallet.manager.UserManager
import com.hlc.mywallet.storage.CacheStorage
import com.hlc.mywallet.storage.DataStoreCacheStorage
import com.hlc.mywallet.storage.DataStoreTokenStorage
import com.hlc.mywallet.storage.TokenStorage
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return DataStoreTokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideCacheStorage(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): CacheStorage {
        return DataStoreCacheStorage(context, moshi)
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(userManager: UserManager): TokenInterceptor {
        return TokenInterceptor(userManager)
    }

    @Provides
    @Singleton
    fun provideCommonParamsInterceptor(@ApplicationContext context: Context): CommonParamsInterceptor {
        return CommonParamsInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): NetworkLoggingInterceptor {
        return NetworkLoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        commonParamsInterceptor: CommonParamsInterceptor,
        loggingInterceptor: NetworkLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(commonParamsInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideMainService(retrofit: Retrofit): MainService {
        return retrofit.create(MainService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService {
        return retrofit.create(HomeService::class.java)
    }

    @Provides
    @Singleton
    fun provideTeamService(retrofit: Retrofit): TeamService {
        return retrofit.create(TeamService::class.java)
    }

    @Provides
    @Singleton
    fun provideDepositService(retrofit: Retrofit): DepositService {
        return retrofit.create(DepositService::class.java)
    }

    @Provides
    @Singleton
    fun provideWalletService(retrofit: Retrofit): WalletService {
        return retrofit.create(WalletService::class.java)
    }
}
