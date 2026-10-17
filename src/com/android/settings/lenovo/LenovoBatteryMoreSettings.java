/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.app.settings.SettingsEnums;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

/**
 * More battery settings, like the stock ZUI page: standby power optimization and the bypass
 * charging row.
 */
public class LenovoBatteryMoreSettings extends SettingsPreferenceFragment {
    private static final String KEY_BYPASS = "lenovo_bypass_charging";

    private Preference mBypassPref;

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PAGE_UNKNOWN;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.lenovo_battery_more_settings, rootKey);
        mBypassPref = findPreference(KEY_BYPASS);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.lenovo_battery_more_title);
        if (Settings.System.getInt(getContentResolver(), LenovoBattery.BYPASS_CHARGING, 0) != 0) {
            int[] range = LenovoBattery.bypassRange(getContext());
            mBypassPref.setSummary(getString(R.string.lenovo_state_on) + " · "
                    + getString(R.string.lenovo_range_value, range[0], range[1]));
        } else {
            mBypassPref.setSummary(R.string.lenovo_state_off);
        }
    }
}
