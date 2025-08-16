package com.desapabandara.pos.local_db.di

import android.content.Context
import androidx.room.Room
import com.desapabandara.pos.local_db.database.DESAPA_DB
import com.desapabandara.pos.local_db.database.DesapaDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDesapaDB(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, DesapaDB::class.java, DESAPA_DB).build()

    @Provides
    @Singleton
    fun provideLocationDao(
        desapaDB: DesapaDB
    ) = desapaDB.getLocationDao()

    @Provides
    @Singleton
    fun provideOrderDao(
        desapaDB: DesapaDB
    ) = desapaDB.getOrderDao()

    @Provides
    @Singleton
    fun provideOrderItemDao(
        desapaDB: DesapaDB
    ) = desapaDB.getOrderItemDao()

    @Provides
    @Singleton
    fun provideOrderPaymentDao(
        desapaDB: DesapaDB
    ) = desapaDB.getOrderPaymentDao()

    @Provides
    @Singleton
    fun provideOrderTableDao(
        desapaDB: DesapaDB
    ) = desapaDB.getOrderTableDao()

    @Provides
    @Singleton
    fun providePaymentMethodDao(
        desapaDB: DesapaDB
    ) = desapaDB.getPaymentMethodDao()

    @Provides
    @Singleton
    fun providePrinterTemplateDao(
        desapaDB: DesapaDB
    ) = desapaDB.getPrinterTemplateDao()

    @Provides
    @Singleton
    fun provideProductCategoryDao(
        desapaDB: DesapaDB
    ) = desapaDB.getProductCategoryDao()

    @Provides
    @Singleton
    fun provideProductDao(
        desapaDB: DesapaDB
    ) = desapaDB.getProductDao()

    @Provides
    @Singleton
    fun provideStaffDao(
        desapaDB: DesapaDB
    ) = desapaDB.getStaffDao()

    @Provides
    @Singleton
    fun provideStaffPositionDao(
        desapaDB: DesapaDB
    ) = desapaDB.getStaffPositionDao()

    @Provides
    @Singleton
    fun provideTableDao(
        desapaDB: DesapaDB
    ) = desapaDB.getTableDao()

    @Provides
    @Singleton
    fun providePrinterDao(
        desapaDB: DesapaDB
    ) = desapaDB.getPrinterDao()

    @Provides
    @Singleton
    fun providePrinterLocationDao(
        desapaDB: DesapaDB
    ) = desapaDB.getPrinterLocationDao()

}