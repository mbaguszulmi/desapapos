package com.desapabandara.pos.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.mbznetwork.android.base.di.IoDispatcher
import co.mbznetwork.android.base.util.handleOnlineData
import com.desapabandara.pos.R
import com.desapabandara.pos.local_db.dao.PaymentMethodDao
import com.desapabandara.pos.local_db.dao.TableDao
import com.desapabandara.pos.local_db.entity.LocationEntity
import com.desapabandara.pos.local_db.entity.PaymentMethodEntity
import com.desapabandara.pos.local_db.entity.PrinterTemplateEntity
import com.desapabandara.pos.local_db.entity.ProductCategoryEntity
import com.desapabandara.pos.local_db.entity.ProductEntity
import com.desapabandara.pos.local_db.entity.StaffEntity
import com.desapabandara.pos.local_db.entity.StaffPositionEntity
import com.desapabandara.pos.local_db.entity.TableEntity
import com.desapabandara.pos.model.ui.SyncStatus
import com.desapabandara.pos.preference.datastore.SyncDataStore
import com.desapabandara.pos.base.repository.OnlineRepository
import com.desapabandara.pos.base.repository.PrinterRepository
import com.desapabandara.pos.base.repository.ProductRepository
import com.desapabandara.pos.base.repository.StaffRepository
import com.desapabandara.pos.preference.datastore.AuthDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class DataSyncViewModel @Inject constructor(
    private val syncDataStore: SyncDataStore,
    private val onlineRepository: OnlineRepository,
    private val productRepository: ProductRepository,
    private val staffRepository: StaffRepository,
    private val printerRepository: PrinterRepository,
    private val paymentMethodDao: PaymentMethodDao,
    private val tableDao: TableDao,
    private val authDataStore: AuthDataStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Syncing(R.string.syncing_data))
    val syncStatus = _syncStatus.asStateFlow()

    init {
        startSyncing()
    }

    private fun startSyncing() {
        viewModelScope.launch(ioDispatcher) {
            try {
                syncStoreData()
                syncProductCategories()
                syncProducts()
                syncStaffs()
                syncStaffPositions()
                syncPrinterTemplates()
                syncLocations()
                syncPaymentMethods()
                syncTables()

                syncDataStore.setSyncStatus(true)
            } catch (t: Throwable) {
                Timber.e(t, "Error when syncing data")
            }
        }
    }

    private suspend fun syncStoreData() {
        _syncStatus.value = SyncStatus.Syncing(R.string.syncing_product_categories)
        authDataStore.getCurrentStoreData().first()?.let { store ->
            try {
                val logoUrl = URL(store.company.logoUrl)
                val bitmap = BitmapFactory.decodeStream(logoUrl.openConnection().getInputStream())
                val imageWhiteBG = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(imageWhiteBG)
                canvas.drawARGB(255, 255, 255, 255)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                val resizedBitmap = Bitmap.createScaledBitmap(imageWhiteBG, 200, 200, true)
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val logoBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                authDataStore.saveStoreLogoBase64(logoBase64)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing store data")
            }
        }
    }

    private suspend fun syncProductCategories() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_product_categories)
                onlineRepository.getProductCategories()
            }, {
                productRepository.saveProductCategories(it.map { category ->
                    with(category) {
                        ProductCategoryEntity(
                            id,
                            name,
                            description,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncProducts() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_products)
                onlineRepository.getProducts()
            }, {
                productRepository.saveProducts(it.map { product ->
                    with(product) {
                        ProductEntity(
                            id,
                            name,
                            productCode,
                            description,
                            priceExcludingTax,
                            tax,
                            isTaxInclusive,
                            cost,
                            stock,
                            preparingDuration,
                            imageUrl,
                            categoryId,
                            locationId,
                            isActive,
                            isIngredientOnly,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncStaffs() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_staffs)
                onlineRepository.getStaffs()
            }, {
                staffRepository.saveStaffs(it.map { staff ->
                    with(staff) {
                        StaffEntity(
                            id,
                            name,
                            pin,
                            isActive,
                            userId,
                            multiShift,
                            phoneNumber,
                            positionId,
                            avatarUrl,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncStaffPositions() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_staff_positions)
                onlineRepository.getStaffPositions()
            }, {
                staffRepository.saveStaffPositions(it.map { position ->
                    with(position) {
                        StaffPositionEntity(
                            id,
                            name,
                            locationId,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncPrinterTemplates() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_printer_templates)
                onlineRepository.getPrinterTemplates()
            }, {
                printerRepository.savePrinterTemplates(it.map { pt ->
                    with(pt) {
                        PrinterTemplateEntity(
                            id,
                            name,
                            template,
                            type,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncLocations() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_locations)
                onlineRepository.getLocations()
            }, {
                printerRepository.saveLocations(it.map { location ->
                    with(location) {
                        LocationEntity(
                            id,
                            name,
                            printerTemplateId,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncPaymentMethods() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_payment_methods)
                onlineRepository.getPaymentMethods()
            }, {
                paymentMethodDao.insertMany(it.map { paymentMethod ->
                    with(paymentMethod) {
                        PaymentMethodEntity(
                            id,
                            name,
                            paymentMethodType,
                            isActive,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

    private suspend fun syncTables() {
        handleOnlineData(
            Unit,
            {
                _syncStatus.value = SyncStatus.Syncing(R.string.syncing_tables)
                onlineRepository.getTables()
            }, {
                tableDao.insertMany(it.map { table ->
                    with(table) {
                        TableEntity(
                            id,
                            name,
                            isActive,
                            tableCapacity,
                            createdAt.time,
                            deletedAt?.time,
                            updatedAt.time
                        )
                    }
                })
            }, { code, message ->
                _syncStatus.value = SyncStatus.Error("$message ($code)")
                throw Exception("$message ($code)")
            }
        )
    }

}