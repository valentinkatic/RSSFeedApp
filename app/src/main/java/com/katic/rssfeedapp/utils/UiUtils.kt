package com.katic.rssfeedapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.katic.rssfeedapp.R
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.util.*

class UiUtils {

    companion object {

        fun handleUiError(
            activity: Activity?,
            throwable: Throwable?
        ) {
            var message: String? = "An error occurred, please try again."

            // check if this is service or network error
            if (throwable is HttpException) {
                message = try {
                    val obj = JSONObject(throwable.response()?.errorBody()?.string() ?: "")
                    obj.getString("message")
                } catch (e: Exception) {
                    Timber.e(e, "handleUiError parsing exception")
                    "Service error. Please, try again."
                }
            }

            Timber.d("displayUiError: dialog")

            val dialog = AlertDialog.Builder(activity)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)

            try {
                dialog.show()
            } catch (e: Exception) {
                Timber.e(e, "handleUiError")
            }

        }

        fun formatPublishedDate(context: Context, date: Date?): String {
            return if (date == null) "" else String.format(
                context.getString(R.string.published),
                date.formatRssDate()
            )
        }

    }

}