/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.app.settings.SettingsEnums;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.preference.PreferenceCategory;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Bypass charging: while plugged in, the tablet runs on charger power inside a battery
 * range (lenovo-input BatteryController), like the stock ZUI page.
 */
public class LenovoBypassChargingSettings extends SettingsPreferenceFragment {
    private static final String KEY_MAIN = "lenovo_bypass_main";
    private static final String KEY_MODE_CATEGORY = "lenovo_bypass_mode";
    private static final String KEY_RANGE_CATEGORY = "lenovo_bypass_range_category";
    private static final String KEY_RANGE = "lenovo_bypass_range";
    private static final String[] MODE_KEYS = {"lenovo_bypass_always", "lenovo_bypass_once"};
    private static final int[] MODES =
            {LenovoBattery.BYPASS_MODE_ALWAYS, LenovoBattery.BYPASS_MODE_ONCE};
    private static final String[] OBSERVED = {LenovoBattery.BYPASS_CHARGING,
            LenovoBattery.BYPASS_MODE, LenovoBattery.BYPASS_RANGE};

    private MainSwitchPreference mMainPref;
    private final List<SelectorWithWidgetPreference> mModePrefs = new ArrayList<>();
    private LenovoRangeSliderPreference mRangePref;

    /** "This time only" turns bypass charging off when unplugged, also while shown. */
    private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange) {
            update();
        }
    };

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PAGE_UNKNOWN;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.lenovo_bypass_charging_settings, rootKey);

        mMainPref = findPreference(KEY_MAIN);
        mMainPref.setOnPreferenceChangeListener((p, value) -> {
            Settings.System.putInt(getContentResolver(), LenovoBattery.BYPASS_CHARGING,
                    (Boolean) value ? 1 : 0);
            return true;
        });

        for (int i = 0; i < MODE_KEYS.length; i++) {
            final int mode = MODES[i];
            SelectorWithWidgetPreference pref = findPreference(MODE_KEYS[i]);
            pref.setOnClickListener(p ->
                    Settings.System.putInt(getContentResolver(), LenovoBattery.BYPASS_MODE, mode));
            mModePrefs.add(pref);
        }

        mRangePref = findPreference(KEY_RANGE);
        mRangePref.setBounds(LenovoBattery.BYPASS_LIMIT_LOW, LenovoBattery.BYPASS_LIMIT_HIGH,
                LenovoBattery.BYPASS_STEP);
        mRangePref.setOnRangeChanged((min, max) ->
                Settings.System.putString(getContentResolver(), LenovoBattery.BYPASS_RANGE,
                        min + "_" + max));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.lenovo_bypass_title);
        for (String key : OBSERVED) {
            getContentResolver().registerContentObserver(Settings.System.getUriFor(key), false,
                    mObserver);
        }
        update();
    }

    @Override
    public void onPause() {
        getContentResolver().unregisterContentObserver(mObserver);
        super.onPause();
    }

    private void update() {
        boolean on = Settings.System.getInt(getContentResolver(),
                LenovoBattery.BYPASS_CHARGING, 0) != 0;
        mMainPref.setChecked(on);
        ((PreferenceCategory) findPreference(KEY_MODE_CATEGORY)).setEnabled(on);
        ((PreferenceCategory) findPreference(KEY_RANGE_CATEGORY)).setEnabled(on);

        int mode = Settings.System.getInt(getContentResolver(), LenovoBattery.BYPASS_MODE,
                LenovoBattery.BYPASS_MODE_ALWAYS);
        for (int i = 0; i < mModePrefs.size(); i++) {
            mModePrefs.get(i).setChecked(MODES[i] == mode);
        }

        int[] range = LenovoBattery.bypassRange(getContext());
        mRangePref.setRange(range[0], range[1]);
    }
}
