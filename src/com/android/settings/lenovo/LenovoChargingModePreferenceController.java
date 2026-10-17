/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.content.Context;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.widget.SelectorWithWidgetPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Charging mode of the Lenovo battery controls on Settings > Battery: normal, stop at 80% or
 * battery protection, applied by lenovo-input (BatteryController). Drives the radio rows of the
 * Charging category.
 */
public class LenovoChargingModePreferenceController extends LenovoBatteryPreferenceController {
    private static final String[] MODE_KEYS =
            {"lenovo_charging_normal", "lenovo_charging_limit", "lenovo_charging_protect"};

    private final List<SelectorWithWidgetPreference> mModePrefs = new ArrayList<>();

    public LenovoChargingModePreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mModePrefs.clear();
        for (int i = 0; i < MODE_KEYS.length; i++) {
            final int mode = i;
            SelectorWithWidgetPreference pref = screen.findPreference(MODE_KEYS[i]);
            pref.setOnClickListener(p -> {
                Settings.Global.putInt(mContext.getContentResolver(),
                        LenovoBattery.CHARGE_MODE, mode);
                updateChargingMode();
            });
            mModePrefs.add(pref);
        }
    }

    @Override
    public void updateState(Preference preference) {
        updateChargingMode();
    }

    private void updateChargingMode() {
        int mode = Math.max(0, Math.min(MODE_KEYS.length - 1, Settings.Global.getInt(
                mContext.getContentResolver(), LenovoBattery.CHARGE_MODE, 0)));
        for (int i = 0; i < mModePrefs.size(); i++) {
            mModePrefs.get(i).setChecked(i == mode);
        }
    }
}
