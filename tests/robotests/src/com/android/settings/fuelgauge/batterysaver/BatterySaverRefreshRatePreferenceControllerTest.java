/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fuelgauge.batterysaver;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.provider.Settings;
import android.view.Display;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class BatterySaverRefreshRatePreferenceControllerTest {
    private Context mContext;
    private Resources mResources;
    private Display mDisplay;
    private BatterySaverRefreshRatePreferenceController mController;

    @Before
    public void setUp() {
        mContext = spy(RuntimeEnvironment.application);
        mResources = spy(mContext.getResources());
        doReturn(mResources).when(mContext).getResources();
        final DisplayManager displayManager = mock(DisplayManager.class);
        mDisplay = mock(Display.class);
        doReturn(displayManager).when(mContext).getSystemService(DisplayManager.class);
        doReturn(mDisplay).when(displayManager).getDisplay(Display.DEFAULT_DISPLAY);
        doReturn(false).when(mResources).getBoolean(
                com.android.internal.R.bool.config_lowPowerRefreshRateDefault);
        doReturn(false).when(mResources).getBoolean(R.bool.config_show_smooth_display);
        Settings.System.putString(mContext.getContentResolver(),
                Settings.System.LOW_POWER_REFRESH_RATE, null);
        mController = new BatterySaverRefreshRatePreferenceController(mContext, "test_key");
    }

    @Test
    public void highRefreshDisplay_withoutSmoothDisplay_switchIsAvailable() {
        doReturn(new Display.Mode[]{new Display.Mode(1, 1000, 1000, 144f)})
                .when(mDisplay).getSupportedModes();
        assertThat(mController.getAvailabilityStatus()).isEqualTo(BasePreferenceController.AVAILABLE);
    }

    @Test
    public void sixtyHzDisplay_switchIsUnavailable() {
        doReturn(new Display.Mode[]{new Display.Mode(1, 1000, 1000, 60.000004f)})
                .when(mDisplay).getSupportedModes();
        assertThat(mController.getAvailabilityStatus())
                .isEqualTo(BasePreferenceController.UNSUPPORTED_ON_DEVICE);
    }

    @Test
    public void deviceDefaultOff_explicitUserChoiceIsPersisted() {
        assertThat(mController.isChecked()).isFalse();
        assertThat(mController.setChecked(true)).isTrue();
        final BatterySaverRefreshRatePreferenceController recreated =
                new BatterySaverRefreshRatePreferenceController(mContext, "test_key");
        assertThat(recreated.isChecked()).isTrue();
        assertThat(recreated.setChecked(false)).isTrue();
        assertThat(mController.isChecked()).isFalse();
    }
}
