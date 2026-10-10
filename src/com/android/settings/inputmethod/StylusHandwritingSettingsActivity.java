/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.activityembedding.ActivityEmbeddingRulesController;

/** Opens handwriting preferences above the current Settings detail page. */
public class StylusHandwritingSettingsActivity extends Activity {
    private static final String TAG = "StylusHandwriting";
    private static final String GBOARD_PACKAGE = "com.google.android.inputmethod.latin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            for (String activity : new String[] {
                    "com.google.android.apps.inputmethod.latin.stylus.StylusSettingsActivity",
                    "com.google.android.apps.inputmethod.latin.preference.SettingsActivity"}) {
                if (open(new Intent().setClassName(GBOARD_PACKAGE, activity))) return;
            }
            open(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
        } finally {
            finish();
        }
    }

    private boolean open(Intent target) {
        ComponentName component = target.resolveActivity(getPackageManager());
        if (component == null) return false;
        ActivityEmbeddingRulesController.registerTwoPanePairRuleForSettingsHome(this,
                component, target.getAction(),
                false /* finishPrimaryWithSecondary */,
                true /* finishSecondaryWithPrimary */,
                false /* clearTop */);
        try {
            // Stay in the caller's task. In particular, do not launch the
            // deep-link homepage, whose task-root policy restarts Settings.
            startActivity(target);
            return true;
        } catch (ActivityNotFoundException | SecurityException e) {
            Log.w(TAG, "Handwriting settings unavailable: " + component, e);
            return false;
        }
    }
}
