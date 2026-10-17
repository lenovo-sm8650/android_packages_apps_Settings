/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.Preference;

import com.android.settings.R;

/**
 * Bypass charging row of Settings > Battery: its state, and the battery range while it is on.
 * The page itself is LenovoBypassChargingSettings.
 */
public class LenovoBypassChargingPreferenceController extends LenovoBatteryPreferenceController {

    public LenovoBypassChargingPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public void updateState(Preference preference) {
        boolean on = Settings.System.getInt(mContext.getContentResolver(),
                LenovoBattery.BYPASS_CHARGING, 0) != 0;
        if (on) {
            int[] range = LenovoBattery.bypassRange(mContext);
            preference.setSummary(mContext.getString(R.string.lenovo_state_on) + " · "
                    + mContext.getString(R.string.lenovo_range_value, range[0], range[1]));
        } else {
            preference.setSummary(R.string.lenovo_state_off);
        }
    }
}
