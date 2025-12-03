package com.example.bagrot_work.utils;
import android.util.Patterns;

import androidx.annotation.Nullable;
public class Validator {
    public static boolean isfNameValid(@Nullable String fName) {
        return fName != null && fName.length() >= 2 ;
    }


    public static boolean islNameValid(@Nullable String lName) {
        return lName != null && lName.length() >= 2 ;
    }

    public static boolean isPasswordValid(@Nullable String password) {
        return password != null && password.length() >= 6 ;
    }

    public static boolean isUsernameValid(@Nullable String Username) {
        return Username != null && Username.length() >= 3;
    }

}
