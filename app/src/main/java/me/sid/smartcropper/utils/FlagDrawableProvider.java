package me.sid.smartcropper.utils;

import android.graphics.drawable.BitmapDrawable;

public interface FlagDrawableProvider {
    BitmapDrawable forCountry(String countryCode);
}
