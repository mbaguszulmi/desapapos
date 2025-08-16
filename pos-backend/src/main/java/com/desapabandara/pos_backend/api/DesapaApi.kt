package com.desapabandara.pos_backend.api

import com.desapabandara.pos_backend.model.request.PosLoginRequest
import com.desapabandara.pos_backend.model.response.BaseResponse
import com.desapabandara.pos_backend.model.response.CategoryResponse
import com.desapabandara.pos_backend.model.response.LocationResponse
import com.desapabandara.pos_backend.model.response.LoginResponse
import com.desapabandara.pos_backend.model.response.PaymentMethodResponse
import com.desapabandara.pos_backend.model.response.PrinterTemplateResponse
import com.desapabandara.pos_backend.model.response.ProductResponse
import com.desapabandara.pos_backend.model.response.StaffPositionResponse
import com.desapabandara.pos_backend.model.response.StaffResponse
import com.desapabandara.pos_backend.model.response.TableResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DesapaApi {
    @POST("auth/poslogin")
    suspend fun posLogin(@Body loginRequest: PosLoginRequest): Response<BaseResponse<LoginResponse>>

    @GET("products")
    suspend fun getProducts(): Response<BaseResponse<List<ProductResponse>>>

    @GET("productcategories")
    suspend fun getProductCategories(): Response<BaseResponse<List<CategoryResponse>>>

    @GET("staffs")
    suspend fun getStaffs(): Response<BaseResponse<List<StaffResponse>>>

    @GET("staffs/staffpositions")
    suspend fun getStaffPositions(): Response<BaseResponse<List<StaffPositionResponse>>>

    @GET("printertemplates")
    suspend fun getPrinterTemplates(): Response<BaseResponse<List<PrinterTemplateResponse>>>

    @GET("printertemplates/locations")
    suspend fun getLocations(): Response<BaseResponse<List<LocationResponse>>>

    @GET("paymentmethods")
    suspend fun getPaymentMethods(): Response<BaseResponse<List<PaymentMethodResponse>>>

    @GET("tables")
    suspend fun getTables(): Response<BaseResponse<List<TableResponse>>>
}
