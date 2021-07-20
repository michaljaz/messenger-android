package com.github.michaljaz.messenger.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.disableTooltip() {
    val content: View = getChildAt(0)
    if (content is ViewGroup) {
        content.forEach {
            it.setOnLongClickListener {
                return@setOnLongClickListener true
            }
            // disable vibration also
            it.isHapticFeedbackEnabled = false
        }
    }
}