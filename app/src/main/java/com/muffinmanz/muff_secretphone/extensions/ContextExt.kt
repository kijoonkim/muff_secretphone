package com.muffinmanz.muff_secretphone.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getColorRes(@ColorRes id: Int) = ContextCompat.getColor(applicationContext, id)