package com.ivanavicius.robertas.barcodescanner

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.text.NumberFormat
import java.util.*

@GlideModule
class MyGlideModule: AppGlideModule()

fun Activity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
}

inline fun <reified  T: ViewModel> getViewModel(owner: ViewModelStoreOwner): T =
    ViewModelProvider(owner).get(T::class.java)

fun Activity.tryNavigate(directions: NavDirections){
    try {
        Navigation.findNavController(this, R.id.navHostFragment).navigate(directions)
    } catch (e: Exception){
        e.printStackTrace()
    }
}

fun Number.toNumeric(digits: Int = 0): String =
    NumberFormat.getIntegerInstance(getLocal()).apply {
        this.maximumFractionDigits = digits
        this.minimumFractionDigits = digits
    }.format(this)

fun getLocal(): Locale =
    Locale.getAvailableLocales().toList().firstOrNull { it.language == "lt" && it.country == "LT" }?: Locale.getDefault()