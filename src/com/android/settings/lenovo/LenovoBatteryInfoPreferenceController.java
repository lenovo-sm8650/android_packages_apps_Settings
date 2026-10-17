/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.content.Context;

import androidx.preference.Preference;

import com.android.settings.R;

/** Charge cycles and health of the battery, shown as the Battery status row of Settings > Battery. */
public class LenovoBatteryInfoPreferenceController extends LenovoBatteryPreferenceController {

    public LenovoBatteryInfoPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public void updateState(Preference preference) {
        Integer[] info = LenovoBattery.batteryInfo(mContext);
        preference.setSummary(mContext.getString(R.string.lenovo_battery_info_summary,
                info[0] != null ? info[0].toString() : "-",
                info[1] != null ? info[1] + "%" : "-"));
    }
}
