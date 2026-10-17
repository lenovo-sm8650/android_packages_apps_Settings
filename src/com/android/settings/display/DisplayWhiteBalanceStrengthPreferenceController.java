/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.display;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.SliderPreferenceController;
import com.android.settingslib.widget.SliderPreference;

/**
 * How strongly the display white balance follows the ambient light. The setting is only
 * read by a ColorDisplayService with the Lenovo white balance strength patch, which marks
 * itself with config_lenovo_patch_wb_strength; without it the slider stays hidden.
 */
public class DisplayWhiteBalanceStrengthPreferenceController extends SliderPreferenceController {

    /** Settings.Secure, 0-100. */
    private static final String SETTING = "display_white_balance_strength";
    private static final int DEFAULT = 100;
    private static final int STEP = 10;

    public DisplayWhiteBalanceStrengthPreferenceController(@NonNull Context context,
            @NonNull String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        int marker = mContext.getResources().getIdentifier("config_lenovo_patch_wb_strength",
                "bool", "android");
        return marker != 0 ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        SliderPreference preference = screen.findPreference(getPreferenceKey());
        if (preference != null) {
            preference.setMin(getMin());
            preference.setMax(getMax());
            preference.setSliderIncrement(STEP);
            preference.setUpdatesContinuously(false);
        }
    }

    @Override
    public int getSliderPosition() {
        return Settings.Secure.getInt(mContext.getContentResolver(), SETTING, DEFAULT);
    }

    @Override
    public boolean setSliderPosition(int position) {
        if (position < getMin() || position > getMax()) return false;
        return Settings.Secure.putInt(mContext.getContentResolver(), SETTING, position);
    }

    @Override
    public int getMin() {
        return 0;
    }

    @Override
    public int getMax() {
        return 100;
    }
}
