/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;

import com.android.settings.R;

import java.io.File;
import java.nio.file.Files;

/**
 * Settings keys and helpers of the Lenovo battery controls on Settings > Battery
 * (charging mode, battery maintenance, standby saver, bypass charging). The keys are
 * applied by lenovo-input in system_server (BatteryController, StandbyController) and
 * stay the ones the device settings used before they moved here. The controls are
 * shown only where config_lenovo_battery_features is set, so this Settings stays
 * harmless on other devices.
 */
public final class LenovoBattery {
    /** Settings.Global: 0 normal, 1 stop at 80%, 2 protection 40-60%. */
    public static final String CHARGE_MODE = "lenovo_battery_charge_mode";
    /** Settings.Global switches. */
    public static final String MAINTENANCE = "battery_maintenance_on";
    public static final String STANDBY_SAVER = "lenovo_standby_saver";

    /** Settings.System int. */
    public static final String BYPASS_CHARGING = "global_bypass_charging_enable";
    /** Settings.System int: 1 always, 2 this time only (turns off when unplugged). */
    public static final String BYPASS_MODE = "global_bypass_charging_work_mode";
    public static final int BYPASS_MODE_ALWAYS = 1;
    public static final int BYPASS_MODE_ONCE = 2;
    /** Settings.System string "min_max" in percent (charges again at min, holds from max). */
    public static final String BYPASS_RANGE = "global_bypass_charging_level_range";
    public static final int BYPASS_LIMIT_LOW = 20;
    public static final int BYPASS_LIMIT_HIGH = 80;
    public static final int BYPASS_STEP = 5;
    private static final int DEFAULT_BYPASS_MIN = 40;
    private static final int DEFAULT_BYPASS_MAX = 60;

    private LenovoBattery() {}

    public static boolean isSupported(Context context) {
        return context.getResources().getBoolean(R.bool.config_lenovo_battery_features);
    }

    /**
     * The bypass charging range (min, max) from the "min_max" string, normalized like
     * BatteryController; the default when unset or malformed.
     */
    public static int[] bypassRange(Context context) {
        String value = Settings.System.getString(context.getContentResolver(), BYPASS_RANGE);
        int min = DEFAULT_BYPASS_MIN;
        int max = DEFAULT_BYPASS_MAX;
        if (value != null) {
            String[] parts = value.split("_");
            if (parts.length == 2) {
                try {
                    min = Integer.parseInt(parts[0].trim());
                    max = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    min = DEFAULT_BYPASS_MIN;
                    max = DEFAULT_BYPASS_MAX;
                }
            }
        }
        min = Math.max(BYPASS_LIMIT_LOW,
                Math.min(snap(min), BYPASS_LIMIT_HIGH - BYPASS_STEP));
        max = Math.max(min + BYPASS_STEP, Math.min(snap(max), BYPASS_LIMIT_HIGH));
        return new int[] {min, max};
    }

    private static int snap(int value) {
        return Math.round(value / (float) BYPASS_STEP) * BYPASS_STEP;
    }

    /** Charge cycles and health percent of the battery, null when unknown. */
    public static Integer[] batteryInfo(Context context) {
        Intent sticky = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Integer cycles = null;
        if (sticky != null) {
            int value = sticky.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1);
            if (value >= 0) cycles = value;
        }
        if (cycles == null) cycles = readInt("/sys/class/power_supply/battery/cycle_count");
        Integer health = readInt("/sys/class/qcom-battery/fg1_soh");
        if (health == null) health = readInt("/sys/class/qcom-battery/soh");
        if (health != null && (health < 1 || health > 100)) health = null;
        return new Integer[] {cycles, health};
    }

    private static Integer readInt(String path) {
        try {
            return Integer.parseInt(new String(Files.readAllBytes(new File(path).toPath())).trim());
        } catch (Exception e) {
            return null;
        }
    }
}
