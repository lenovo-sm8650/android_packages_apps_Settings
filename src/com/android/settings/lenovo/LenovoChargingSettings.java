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
import com.android.settingslib.widget.SelectorWithWidgetPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Charging optimization: charging mode (normal, stop at 80%, battery protection), battery
 * maintenance and the battery status, applied by lenovo-input (BatteryController).
 */
public class LenovoChargingSettings extends SettingsPreferenceFragment {
    private static final String[] CHARGING_KEYS =
            {"lenovo_charging_normal", "lenovo_charging_limit", "lenovo_charging_protect"};
    private static final String KEY_BATTERY_INFO = "lenovo_battery_info";

    private final List<SelectorWithWidgetPreference> mChargingPrefs = new ArrayList<>();
    private Preference mBatteryInfoPref;

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PAGE_UNKNOWN;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.lenovo_charging_settings, rootKey);
        for (int mode = 0; mode < CHARGING_KEYS.length; mode++) {
            final int value = mode;
            SelectorWithWidgetPreference pref = findPreference(CHARGING_KEYS[mode]);
            pref.setOnClickListener(p -> {
                Settings.Global.putInt(getContentResolver(), LenovoBattery.CHARGE_MODE, value);
                updateChargingMode();
            });
            mChargingPrefs.add(pref);
        }
        mBatteryInfoPref = findPreference(KEY_BATTERY_INFO);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.lenovo_charging_optimization_title);
        updateChargingMode();
        Integer[] info = LenovoBattery.batteryInfo(getContext());
        mBatteryInfoPref.setSummary(getString(R.string.lenovo_battery_info_summary,
                info[0] != null ? info[0].toString() : "-",
                info[1] != null ? info[1] + "%" : "-"));
    }

    private void updateChargingMode() {
        int mode = Math.max(0, Math.min(CHARGING_KEYS.length - 1,
                Settings.Global.getInt(getContentResolver(), LenovoBattery.CHARGE_MODE, 0)));
        for (int i = 0; i < mChargingPrefs.size(); i++) {
            mChargingPrefs.get(i).setChecked(i == mode);
        }
    }
}
