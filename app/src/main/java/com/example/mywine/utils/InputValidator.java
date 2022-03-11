package com.example.mywine.utils;

import android.text.Editable;
import android.util.Patterns;

import androidx.annotation.Nullable;

public class InputValidator {

    public InputValidator() {
    }

    public static boolean isFullNameValid(@Nullable Editable text) {
        return !isFieldEmpty(text);
    }

    public static boolean isLastNameValid(@Nullable Editable text) {
        return !isFieldEmpty(text);
    }

    public static boolean isPasswordValid(@Nullable Editable text) {
        return (text != null && text.length() >= 8);
    }

    public static boolean isEmailValid(@Nullable Editable text) {
        return (text != null && Patterns.EMAIL_ADDRESS.matcher(text).matches());
    }

    private static boolean isFieldEmpty(@Nullable Editable text) {
        return (text == null || text.length() == 0);
    }
}
