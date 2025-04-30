package com.example.memorynote2025.constants

import androidx.annotation.StringRes
import com.example.memorynote2025.R

enum class PasswordString(@StringRes val resId: Int) {
    NEW(R.string.password_new),
    ENTER(R.string.password_enter),
    CONFIRM(R.string.password_confirm),
    REENTER(R.string.password_reenter),
    CHANGE(R.string.password_change),
    SETUP_COMPLETE(R.string.password_setup_complete)
}