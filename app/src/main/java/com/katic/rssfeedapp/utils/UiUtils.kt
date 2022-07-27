package com.katic.rssfeedapp.utils

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updateMargins
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.data.model.RssChannel
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.util.*

class UiUtils {

    companion object {

        fun handleUiError(context: Context?, throwable: Throwable?) {
            if (context == null) {
                Timber.d("displayUiError: failed")
                return
            }

            var message: String? = context.resources.getString(R.string.general_error)

            // check if this is service or network error
            if (throwable is HttpException) {
                message = try {
                    val obj = JSONObject(throwable.response()?.errorBody()?.string() ?: "")
                    obj.getString("message")
                } catch (e: Exception) {
                    Timber.e(e, "handleUiError parsing exception")
                    context.resources.getString(R.string.service_error)
                }
            }

            throwable?.also {
                message += "\n${it.message}"
            }

            Timber.d("displayUiError: dialog")

            val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)

            try {
                dialog.show()
            } catch (e: Exception) {
                Timber.e(e, "handleUiError")
            }
        }

        fun showInputDialog(
            context: Context,
            @StringRes title: Int,
            @StringRes description: Int? = null,
            editableString: String = "",
            listener: InputDialogListener,
            multiLine: Boolean = false
        ) {
            AlertDialog.Builder(context)
                .apply {
                    setTitle(title)
                    if (description != null) setMessage(description)
                    val et = EditText(context)
                    if (!multiLine) {
                        et.imeOptions = EditorInfo.IME_ACTION_DONE
                        et.setSingleLine()
                        et.onDone {
                            listener.onConfirmed(et.text.toString())
                        }
                    }
                    val container = FrameLayout(context)
                    val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.updateMargins(
                        left = context.resources.getDimensionPixelSize(R.dimen.input_dialog_horizontal_margin),
                        top = context.resources.getDimensionPixelSize(R.dimen.input_dialog_top_margin),
                        right = context.resources.getDimensionPixelSize(R.dimen.input_dialog_horizontal_margin),
                        bottom = context.resources.getDimensionPixelSize(R.dimen.input_dialog_bottom_margin),
                    )
                    et.layoutParams = params
                    et.setText(editableString)
                    et.showKeyboardOnFocus()
                    et.requestFocus()
                    container.addView(et)
                    setView(container)
                    setPositiveButton(R.string.ok) { _, _ -> listener.onConfirmed(et.text.toString()) }
                    setNegativeButton(R.string.cancel, null)
                    setCancelable(false)
                    create()
                    show()
                }
        }

        fun formatPublishedDate(context: Context, date: Long?): String {
            return if (date == null) "" else String.format(
                context.getString(R.string.published),
                Date(date).formatRssDate()
            )
        }

        fun formatRemovedRssChannelMessage(context: Context, rssChannel: RssChannel): String {
            return String.format(
                context.getString(R.string.channel_removed),
                rssChannel.title
            )
        }
    }

    fun interface InputDialogListener {
        fun onConfirmed(text: String?)
    }
}

fun EditText.onDone(callback: () -> Unit) {
    setOnKeyListener { _, keyCode, event ->
        if (event.action == EditorInfo.IME_ACTION_DONE && keyCode == KeyEvent.KEYCODE_ENTER) {
            callback.invoke()
            true
        } else {
            false
        }
    }
}

fun EditText.showKeyboardOnFocus() {
    setOnFocusChangeListener { _, _ ->
        post {
            setSelection(length())
            context.showKeyboard(this)
        }
    }
}

fun Context.showKeyboard(view: View?) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}
