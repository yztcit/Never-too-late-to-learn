package com.nttn.netlib

import com.jeremyliao.liveeventbus.BuildConfig

open class BaseRepository {
    suspend fun <T> executeHttp(block: suspend () -> ApiResponse<T>): ApiResponse<T> {
        runCatching {
            block.invoke()
        }.onSuccess { data: ApiResponse<T> ->
            return handleHttpOk(data)
        }.onFailure { e ->
            return handleHttpError(e)
        }
        return ApiEmptyResponse()
    }

    private fun <T> handleHttpError(e: Throwable): ApiResponse<T> {
        if (BuildConfig.DEBUG) e.printStackTrace()
        handlingExceptions(e)
        return ApiErrorResponse(e)
    }

    private fun <T> handleHttpOk(data: ApiResponse<T>): ApiResponse<T> {
        return if (data.isSuccess) {
            getHttpSuccessResponse(data)
        } else {
            handlingApiExceptions(data.errorCode, data.errorMsg)
            ApiFailedResponse(data.errorCode, data.errorMsg)
        }
    }

    private fun <T> getHttpSuccessResponse(response: ApiResponse<T>): ApiResponse<T> {
        return if (response.data == null || response.data is List<*> && (response.data as List<*>).isEmpty()) {
            ApiEmptyResponse()
        } else {
            ApiSuccessResponse(response.data!!)
        }
    }
}