package com.example.myfiremessage.common

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat

//lazy
fun <T> lazyFast(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)


//progressDialog
fun setProgressDialog(context: Context, message: String): AlertDialog {
    val llPadding = 30
    val ll = LinearLayoutCompat(context)
    ll.orientation = LinearLayoutCompat.HORIZONTAL
    ll.setPadding(llPadding, llPadding, llPadding, llPadding)
    ll.gravity = Gravity.CENTER
    var llParam = LinearLayoutCompat.LayoutParams(
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    llParam.gravity = Gravity.CENTER
    ll.layoutParams = llParam

    val progressBar = ProgressBar(context)
    progressBar.isIndeterminate = true
    progressBar.setPadding(0, 0, llPadding, 0)
    progressBar.layoutParams = llParam

    llParam = LinearLayoutCompat.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    llParam.gravity = Gravity.CENTER
    val tvText = AppCompatTextView(context)
    tvText.text = message
    tvText.setTextColor(Color.parseColor("#000000"))
    tvText.textSize = 20.toFloat()
    tvText.layoutParams = llParam

    ll.addView(progressBar)
    ll.addView(tvText)

    val builder = AlertDialog.Builder(context)
    builder.setCancelable(true)
    builder.setView(ll)

    val dialog = builder.create()
    val window = dialog.window
    if (window != null) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        layoutParams.height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
    }
    return dialog
}