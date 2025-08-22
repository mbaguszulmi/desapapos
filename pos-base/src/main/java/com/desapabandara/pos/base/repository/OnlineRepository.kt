package com.desapabandara.pos.base.repository

import com.desapabandara.pos_backend.api.DesapaApi
import com.desapabandara.pos_backend.model.request.OrderRequest
import com.desapabandara.pos_backend.model.request.PosLoginRequest
import com.desapabandara.pos_backend.model.response.BaseResponse
import com.desapabandara.pos_backend.model.response.LoginResponse
import com.desapabandara.pos_backend.util.requestOnlineData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineRepository @Inject constructor(
    private val desapaApi: DesapaApi
) {
    suspend fun posLogin(posLoginRequest: PosLoginRequest) = requestOnlineData<BaseResponse<LoginResponse>, LoginResponse> {
        desapaApi.posLogin(posLoginRequest)
    }

    suspend fun getProducts() = requestOnlineData {
        desapaApi.getProducts()
    }

    suspend fun getProductCategories() = requestOnlineData {
        desapaApi.getProductCategories()
    }

    suspend fun getStaffs() = requestOnlineData {
        desapaApi.getStaffs()
    }

    suspend fun getStaffPositions() = requestOnlineData {
        desapaApi.getStaffPositions()
    }

    suspend fun getPrinterTemplates() = requestOnlineData {
        desapaApi.getPrinterTemplates()
    }

    suspend fun getLocations() = requestOnlineData {
        desapaApi.getLocations()
    }

    suspend fun getPaymentMethods() = requestOnlineData {
        desapaApi.getPaymentMethods()
    }

    suspend fun getTables() = requestOnlineData {
        desapaApi.getTables()
    }

    suspend fun syncOrder(order: OrderRequest) = requestOnlineData {
        desapaApi.syncOrder(order)
    }
}