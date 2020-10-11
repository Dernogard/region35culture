package ru.dernogard.region35culture.utils

import retrofit2.HttpException
import ru.dernogard.region35culture.R
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Class getting error and returning correct message from string resources
 */

class ErrorHandler {

    fun getErrorStringResId(throwable: Throwable): Int {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> R.string.error_400
                    401, 403 -> R.string.error_403
                    404 -> R.string.error_404
                    408 -> R.string.error_timeout
                    500 -> R.string.error_500
                    502 -> R.string.error_502
                    else -> {
                        when {
                            throwable.code().toString()
                                .startsWith("3") -> R.string.unknown_redirect_error
                            throwable.code().toString()
                                .startsWith("4") -> R.string.unknown_connection_error
                            throwable.code().toString()
                                .startsWith("5") -> R.string.unknown_server_error
                            else -> R.string.unknown_internet_error
                        }
                    }
                }
            }
            is NullPointerException -> R.string.error_wrong_data
            is SocketTimeoutException -> R.string.error_timeout
            is IOException -> R.string.error_internet_connection
            else -> R.string.error_unknown
        }
    }
}