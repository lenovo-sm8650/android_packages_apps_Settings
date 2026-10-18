/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.lenovo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;
import com.android.settingslib.Utils;
import com.google.android.material.slider.RangeSlider;

/**
 * A preference with a two-thumb percent slider; the summary shows the range. The listener
 * gets the range when the user lets go of a thumb.
 */
public class LenovoRangeSliderPreference extends Preference {

    /** Receives the range when a thumb is released. */
    public interface OnRangeChanged {
        void onRangeChanged(int min, int max);
    }

    private int mFrom = 0;
    private int mTo = 100;
    private int mStep = 1;
    private int mLow = 0;
    private int mHigh = 100;
    private OnRangeChanged mListener;

    public LenovoRangeSliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_lenovo_range_slider);
        setSelectable(false);
    }

    public void setOnRangeChanged(OnRangeChanged listener) {
        mListener = listener;
    }

    /** Bounds and step; the thumbs stay at least one step apart. */
    public void setBounds(int from, int to, int step) {
        mFrom = from;
        mTo = to;
        mStep = step;
        notifyChanged();
    }

    public void setRange(int low, int high) {
        if (low == mLow && high == mHigh) return;
        mLow = low;
        mHigh = high;
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        return rangeText(mLow, mHigh);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        // androidx paints the title of an enabled, non-selectable preference in the summary
        // colour, so it looked disabled; keep the normal title colour (its disabled state
        // follows the view state).
        ((TextView) holder.findViewById(android.R.id.title)).setTextColor(
                Utils.getColorAttr(getContext(), android.R.attr.textColorPrimary));
        TextView summary = (TextView) holder.findViewById(android.R.id.summary);
        ((TextView) holder.findViewById(R.id.range_start)).setText(percent(mFrom));
        ((TextView) holder.findViewById(R.id.range_end)).setText(percent(mTo));

        RangeSlider slider = (RangeSlider) holder.findViewById(R.id.range_slider);
        slider.clearOnChangeListeners();
        slider.clearOnSliderTouchListeners();
        slider.setValueFrom(mFrom);
        slider.setValueTo(mTo);
        slider.setStepSize(mStep);
        slider.setMinSeparationValue(mStep);
        slider.setValues((float) mLow, (float) mHigh);
        slider.setEnabled(isEnabled());
        slider.setLabelFormatter(value -> percent(Math.round(value)));
        slider.addOnChangeListener((s, value, fromUser) -> summary.setText(rangeText(
                Math.round(s.getValues().get(0)), Math.round(s.getValues().get(1)))));
        slider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(RangeSlider s) {}

            @Override
            public void onStopTrackingTouch(RangeSlider s) {
                int newLow = Math.round(s.getValues().get(0));
                int newHigh = Math.round(s.getValues().get(1));
                if (newLow == mLow && newHigh == mHigh) return;
                mLow = newLow;
                mHigh = newHigh;
                if (mListener != null) mListener.onRangeChanged(newLow, newHigh);
            }
        });
    }

    private String percent(int value) {
        return getContext().getString(R.string.lenovo_percent_value, value);
    }

    private String rangeText(int low, int high) {
        return getContext().getString(R.string.lenovo_range_value, low, high);
    }
}
