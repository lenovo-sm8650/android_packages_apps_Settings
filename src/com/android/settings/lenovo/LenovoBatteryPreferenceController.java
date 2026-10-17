/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.content.Context;

import com.android.settings.core.BasePreferenceController;

/** Shows a Settings > Battery row only on devices with the Lenovo battery controls. */
public class LenovoBatteryPreferenceController extends BasePreferenceController {

    public LenovoBatteryPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return LenovoBattery.isSupported(mContext) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }
}
