/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.display;

import android.content.Context;
import android.provider.Settings;

import com.android.settings.core.TogglePreferenceController;

/**
 * The sunlight high brightness mode of the Lenovo display, the stock ZUI "Brightness
 * enhancement" switch. It is stored in Settings.System zui_screen_enhance_enabled (1 on, 0 off,
 * off by default) and only acts with the automatic brightness on, in the Lenovo display input
 * service. Shown only with the Lenovo framework patch, like the white balance strength.
 */
public class DisplayHighBrightnessModePreferenceController extends TogglePreferenceController {

    /** Settings.System int, 1 on, 0 off. */
    private static final String SETTING = "zui_screen_enhance_enabled";

    public DisplayHighBrightnessModePreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        int marker = mContext.getResources().getIdentifier("config_lenovo_patch_wb_strength",
                "bool", "android");
        return marker != 0 ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return Settings.System.getInt(mContext.getContentResolver(), SETTING, 0) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.System.putInt(mContext.getContentResolver(), SETTING, isChecked ? 1 : 0);
    }
}