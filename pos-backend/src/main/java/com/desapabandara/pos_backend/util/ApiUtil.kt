package com.desapabandara.pos_backend.util

import co.mbznetwork.android.base.model.network.NetworkResult
import com.desapabandara.pos_backend.model.response.BaseResponse
import retrofit2.Response
import timber.log.Timber

suspend fun <T : BaseResponse<R>, R> requestOnlineData(onlineRequest: suspend () -> Response<BaseResponse<R>>): NetworkResult<R> =
    try {
        onlineRequest().let {
            if (it.isSuccessful) {
                it.body().let { body ->
                    if (body == null) {
                        NetworkResult.Error("null")
                    } else {
                        if (body.status.code == 200) {
                            body.data?.let { data ->
                                NetworkResult.Success(data)
                            } ?: NetworkResult.Error("No data provided", null, 500)
                        } else {
                            NetworkResult.Error(code = it.code(), message = body.status.message)
                        }
                    }
                }
            } else {
                NetworkResult.Error(it.message(), code = it.code())
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "Error from call ${e.message}")
        NetworkResult.Error(e.message)
    }