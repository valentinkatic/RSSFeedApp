package com.katic.rssfeedapp.utils

import kotlinx.coroutines.CancellationException

/**
 * Runs [run] block and calls [catch] block if [run] throws exception
 * or calls [cancel] block if coroutine is canceled
 * (if [CancellationException]) is thrown.
 */
inline fun runCatchCancel(
    run: () -> Unit,
    catch: (t: Throwable) -> Unit,
    cancel: (() -> Unit)
) {
    try {
        run()
    } catch (t: Throwable) {
        if (t !is CancellationException) {
            catch(t)
        } else {
            cancel()
        }
    }
}
