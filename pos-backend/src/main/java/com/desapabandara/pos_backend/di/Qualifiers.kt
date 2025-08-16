package com.desapabandara.pos_backend.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DesapaPosAuthInterceptor

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DesapaPosHttpClient

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DesapaPosRetrofit
