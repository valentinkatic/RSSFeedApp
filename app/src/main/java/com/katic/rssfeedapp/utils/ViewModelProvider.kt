package com.katic.rssfeedapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Fragment extension function for obtaining [ViewModel] with [creator] lambda expression.
 *
 * Taken from
 *
 * [https://proandroiddev.com/view-model-creation-in-android-android-architecture-components-kotlin-ce9f6b93a46b]
 *
 * and
 *
 * [https://proandroiddev.com/kotlin-delegates-in-android-development-part-2-2c15c11ff438]
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProvider(noinline creator: (() -> VM)? = null) = lazy {
    if (creator == null) {
        ViewModelProvider(this)[VM::class.java]
    } else {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>) = creator() as T
        })[VM::class.java]
    }
}

/**
 * Fragment extension function for obtaining [ViewModel] from activity with [creator] lambda expression.
 *
 * Taken from
 *
 * [https://proandroiddev.com/view-model-creation-in-android-android-architecture-components-kotlin-ce9f6b93a46b]
 *
 * and
 *
 * [https://proandroiddev.com/kotlin-delegates-in-android-development-part-2-2c15c11ff438]
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProviderActivity(noinline creator: (() -> VM)? = null) = lazy {
    if (creator == null) {
        ViewModelProvider(activity!!)[VM::class.java]
    } else {
        ViewModelProvider(activity!!, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>) = creator() as T
        })[VM::class.java]
    }
}

/**
 * Fragment extension function for obtaining [ViewModel] from parent fragment with [creator] lambda expression.
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProviderParentFragment(noinline creator: (() -> VM)? = null) = lazy {
    if (creator == null) {
        ViewModelProvider(requireParentFragment())[VM::class.java]
    } else {
        ViewModelProvider(requireParentFragment(), object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>) = creator() as T
        })[VM::class.java]
    }
}

/**
 * Fragment extension function for obtaining [ViewModel] from grandparent fragment with [creator] lambda expression.
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProviderGrandparentFragment(noinline creator: (() -> VM)? = null) = lazy {
    if (creator == null) {
        ViewModelProvider(requireParentFragment().requireParentFragment())[VM::class.java]
    } else {
        ViewModelProvider(requireParentFragment().requireParentFragment(), object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>) = creator() as T
        })[VM::class.java]
    }
}

/**
 * FragmentActivity extension function for creating [ViewModel] with [creator] lambda expression.
 *
 * Taken from
 *
 * [https://proandroiddev.com/view-model-creation-in-android-android-architecture-components-kotlin-ce9f6b93a46b]
 *
 * and
 *
 * [https://proandroiddev.com/kotlin-delegates-in-android-development-part-2-2c15c11ff438]
 */
inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(noinline creator: (() -> VM)? = null) = lazy {
    if (creator == null) {
        ViewModelProvider(this)[VM::class.java]
    } else {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>) = creator() as T
        })[VM::class.java]
    }
}

