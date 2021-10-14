package com.nttn.netlib

import androidx.lifecycle.Observer

abstract class IStateObserve<T> : Observer<ApiResponse<T>> {

    override fun onChanged(apiResponse: ApiResponse<T>) {
        when (apiResponse) {
            is ApiSuccessResponse -> onSuccess(apiResponse.response)
            is ApiEmptyResponse -> onDataEmpty()
            is ApiErrorResponse -> onError(apiResponse.throwable)
            is ApiFailedResponse -> onFailed(apiResponse.errorCode, apiResponse.errorMsg)
        }

        onComplete()
    }

    abstract fun onSuccess(data: T)

    abstract fun onDataEmpty()

    abstract fun onError(e: Throwable)

    abstract fun onComplete()

    abstract fun onFailed(errorCode: Int?, errorMsg: String?)

}